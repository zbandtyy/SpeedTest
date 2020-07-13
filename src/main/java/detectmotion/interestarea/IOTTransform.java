package detectmotion.interestarea;
import detectmotion.utils.MatWrapper;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import com.google.gson.Gson;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;


import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.opencv.core.CvType.*;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/30 15:46
 * @description：对感兴趣的区域进行提取和透视变换，以获得鸟瞰坐标,针对有json文件进行处理
 * @modified By：
 * @version: $
 */
public class IOTTransform implements Serializable,PerspectiveConversion {
    private static final Logger logger = Logger.getLogger(IOTTransform.class);
    private Mat transformMap;//透视转换矩阵
    private Mat inverseMap;//反向转换矩阵
    private ArrayList<Point> picRect;  //感兴趣区域,四个点的坐标 单通道Mat
    private ArrayList<Point> realRect;              //转换后的目标区域 单通道
    private  Rect transformAfter;
    private Size picSize;            //原图宽高
    private Size realSize;            //     截图区域现实对应的宽高,用实际的m表示
    public IOTTransform(String jsoname) throws IOException {

        loadFromFile(jsoname);//从文件中加载所有的参数
    }
    public List<Point> transformPoint(List<List<Double>> dst){
        Mat  src = MatWrapper.doubleArrayToMat(dst,CV_64F);
        src = MatWrapper.SingleTo2Channels(src);//必须转换成双通道
       // logger.debug(MatWrapper.MatToString(src));
        Mat res = new Mat(src.rows(),src.cols(),CV_32FC2);
        Core.perspectiveTransform(src,res,transformMap);
      //  logger.debug(res.type() == CV_32FC2);

        Mat tr = res.reshape(2,src.rows() * src.cols());
       // System.out.println(MatWrapper.MatToString(tr));

        ArrayList<Point>  arr = new ArrayList<>();
        Converters.Mat_to_vector_Point2d(tr,arr);

        return  arr.subList(0,dst.size());
    }
    public List<Point> transformPointList(List<Point> dst){

        Mat src = Converters.vector_Point_to_Mat(dst,CV_32F);

        logger.debug(src.type() + " " +src.channels()+" rows: " + src.rows() + " cols: " + src.cols());

        Mat res = new Mat(src.rows(),src.cols(),CV_32FC2);
        Core.perspectiveTransform(src,res,transformMap);
        Mat tr = res.reshape(2,src.rows() * src.cols());

        ArrayList<Point>  arr = new ArrayList<>();
        Converters.Mat_to_vector_Point2d(tr,arr);

        return  arr.subList(0,dst.size());
    }
    private Mat transformMatFrame(Mat src){
        Mat res = new Mat(src.rows(),src.cols(),src.type());
        Imgproc.warpPerspective(src,res,transformMap,src.size());
        return  res;

    }
    void loadFromFile(String  jsonname) throws IOException {

        //2.转换成固定格式
        String jsonStr = readJsonFile(jsonname);
        logger.debug("the json file content is" + jsonStr);
        Gson s = new Gson();
        Map<String,  ArrayList> m = s.fromJson(jsonStr,Map.class);
        if(m == null){
            logger.error("the json file is null,cannot running");
            return;
        }
        ArrayList<List<Double>> mtxlist = m.get("mtx");
        transformMap = MatWrapper.doubleArrayToMat(mtxlist,CV_64F);
        ArrayList<List<Double>> imtxlist = m.get("imtx");
        inverseMap = MatWrapper.doubleArrayToMat(imtxlist,CV_64F);
        //原图片大小
        ArrayList<Double> imageSize = m.get("imagsize");
        this.picSize = new Size(imageSize.get(0),imageSize.get(1));

        ArrayList<ArrayList<Double>> picRect = m.get("picRect");
        ArrayList<List<Double>> picRect1 = transformScale(picRect);
        this.picRect = doubleIntArray_to_PointArray(picRect1);
        logger.info("感兴趣的图片上的区域"+ this.picRect);


        ArrayList<ArrayList<Double>> realRect1 = m.get("realRect");
        this.realRect = doubleIntArray_to_PointArray(realRect1);

        this.transformAfter = this.pointListToRect(this.realRect);

        logger.info("转换后的区域"+ this.transformAfter);



        ArrayList<Double> real = m.get("realSize");
        this.realSize = new Size(real.get(0),real.get(1));
    }
    public Size getPicSize() {
        return picSize;
    }

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String xmlPath =  Thread.currentThread().getContextClassLoader().getResource("multitracker/calibrate_camera_scale.json" ).getPath();//获IOTTransform its  =  new IOTTransform(xmlPath);
            //测试转换
//          List<Double> src =  Arrays.asList(800.0,500.0);
//          List<Double> src1 = Arrays.asList(800.0,600.0);
//          IOTTransform its = new IOTTransform(xmlPath);
//          List<List<Double>> dst = Arrays.asList(src,src1);
//          List<Point> result = its.transformPoint(dst);//{847.0089111328125, 875.4133911132812},
//          System.out.println(result);// {834.0802001953125, 1170.2252197265625}
        //测试距离获取
//            List<Point> list = Arrays.asList(new Point(328,582),new Point(541,357));
//            IOTTransform its = new IOTTransform(xmlPath);
//            List<Point> result = its.transformPointList(list);
//            double dis = its.getDistance(result.get(0),result.get(1),its.getXRatio(),its.getYRatio());
//            System.out.println(dis);
//        //测试 是否在范围内部
//        IOTTransform iot = new IOTTransform(xmlPath);
//        Point p = new Point (1061,681);
//        boolean a = iot.isInsidePicArea(p);
//        System.out.println(a);


        IOTTransform its = new IOTTransform(xmlPath);
        Mat frame = Imgcodecs.imread("F:\\shared\\spark-example\\speedtest\\0.jpg");
        Mat result = its.transformMatFrame(frame);
        Imgcodecs.imwrite("F:\\shared\\spark-example\\speedtest\\0-te.jpg",result);

    }

    private  String readJsonFile(String jsonFileName) throws IOException {
        //读取json文件
        logger.info("read json file " + jsonFileName);
        String jsonStr = "";

            //1.读取文件内容
            File jsonFile = new File(jsonFileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            logger.info("The file name is" + jsonStr);

        return jsonStr;
    }
    private ArrayList<List<Double>> transformScale ( List<ArrayList<Double>> list){
        ArrayList<List<Double>> newlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Double x= list.get(i).get(0) * picSize.width;
            x = (double) Math.round(x * 100) / 100;

            Double y = list.get(i).get(1) * picSize.height;
            y = (double) Math.round(y * 100) / 100;


            newlist.add(Arrays.asList(x, y));
        }
        return newlist;
    }
    public boolean isInsidePicArea(Point p) {

        Point[] mpa = new Point[this.picRect.size()];
        Point[] ps = this.picRect.toArray(mpa);
        MatOfPoint2f area = new MatOfPoint2f(ps);
        double res = Imgproc.pointPolygonTest(area,p,false);
        if(res < 0) {
            return false;
        }else {
            return true;
        }
    }


    private ArrayList<Point> doubleIntArray_to_PointArray(List<? extends List<Double> > arrs){
        ArrayList<Point> ps = new ArrayList<>();
        for (int i = 0; i < arrs.size(); i++) {
            List<Double> j = arrs.get(i);
            assert (j.size() == 2);
            ps.add(new Point(j.get(0),j.get(1)));
        }
        return ps;
    }
    private Rect pointListToRect(List<Point>  list){
        if(list == null || list.size() <= 0){
            return  null;
        }
        double minx = list.get(0).x;
        double miny = list.get(0).y;

        double maxX = 0;
        double maxY = 0;
        for (int i = 0; i < list.size(); i++) {
            Point p = list.get(i);
            minx = Math.min(minx,p.x);
            miny = Math.min(minx,p.y);

            maxX = Math.max(maxX,p.x);
            maxY = Math.max(maxY,p.y);
        }
        return  new Rect(new Point(minx,miny),new Point(maxX,maxY));
    }

    public double getXRatio(){
                // 实际大小             转换后的区域宽

        return   realSize.width *1.0 /  transformAfter.width ;
    }
    public double getYRatio(){
        return   realSize.height *1.0/  transformAfter.height;
    }


    private   double getDistance(Point previousPos, Point netxPos, double xratio, double yratio){

        if(previousPos != null && netxPos != null){
            double pcx = previousPos.x ;
            double pcy = previousPos.y ;
            double ccx = netxPos.x;
            double ccy = netxPos.y ;
            if(pcx < 0 || pcy < 0 || ccx < 0 || ccy <0){
                System.out.println("previouspos = ===============================================" + previousPos);
                System.out.println("nextpos" + netxPos);
            }


            return  Math.sqrt( ((pcx - ccx) *(pcx -ccx)*xratio*xratio)  +
                    ((pcy - ccy)*(pcy - ccy)*yratio*yratio)   );
        }
        return -1;
    }
}
