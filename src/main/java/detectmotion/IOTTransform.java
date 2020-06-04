package detectmotion;
import detectmotion.utils.MatWrapper;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import com.google.gson.Gson;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;


import java.io.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.opencv.core.CvType.*;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/30 15:46
 * @description：对感兴趣的区域进行提取和透视变换，以获得鸟瞰坐标
 * @modified By：
 * @version: $
 */
public class IOTTransform {
    private static final Logger logger = Logger.getLogger(IOTTransform.class);
    private Mat transformMap;//透视转换矩阵
    private Mat inverseMap;//反向转换矩阵
    private ArrayList<Point> picRect;  //感兴趣区域,四个点的坐标 单通道Mat
    private ArrayList<Point> realRect;              //转换后的目标区域 单通道
    private Size picSize;            //原图宽高
    private Size realSize;            //     截图区域现实对应的宽高,用实际的m表示
    public IOTTransform(String jsoname){
        loadFromFile(jsoname);//从文件中加载所有的参数
    }
    List<Point> transformPoint(List<List<Double>> dst){
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
    void loadFromFile(String  jsonname){

        //2.转换成固定格式
        String jsonStr = readJsonFile(jsonname);
        Gson s = new Gson();
        Map<String,  ArrayList> m = s.fromJson(jsonStr,Map.class);
        ArrayList<List<Double>> mtxlist = m.get("mtx");
        transformMap = MatWrapper.doubleArrayToMat(mtxlist,CV_64F);
        ArrayList<List<Double>> imtxlist = m.get("imtx");
        inverseMap = MatWrapper.doubleArrayToMat(imtxlist,CV_64F);

        ArrayList<ArrayList<Double>> picRect1 = m.get("picRect");
        this.picRect = doubleIntArray_to_PointArray(picRect1);
        logger.debug("感兴趣的区域"+ this.picRect);
        ArrayList<ArrayList<Double>> realRect1 = m.get("realRect");
        this.realRect = doubleIntArray_to_PointArray(realRect1);
        logger.debug("转换后的区域"+ this.realRect);
        ArrayList<Double> imageSize = m.get("imagSize");
        picSize = new Size(imageSize.get(0),imageSize.get(1));
        ArrayList<Double> real = m.get("realSize");
        realSize = new Size(real.get(0),real.get(1));
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String xmlPath =  Thread.currentThread().getContextClassLoader().getResource("calibrate_camera.json" ).getPath();//获IOTTransform its  =  new IOTTransform(xmlPath);
            //测试转换
//          List<Double> src =  Arrays.asList(800.0,500.0);
//          List<Double> src1 = Arrays.asList(800.0,600.0);
//          IOTTransform its = new IOTTransform(xmlPath);
//          List<List<Double>> dst = Arrays.asList(src,src1);
//          List<Point> result = its.transformPoint(dst);
//          System.out.println(result);
        //测试 是否在范围内部
        IOTTransform iot = new IOTTransform(xmlPath);
        Point p = new Point (1061,681);
        boolean a = iot.isInsidePicArea(p);
        System.out.println(a);

    }

    private  String readJsonFile(String jsonFileName){
        //读取json文件
        String jsonStr = "";
        try {
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
            System.out.println(jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
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

    public ArrayList<Point> getIntrestAreaInReal(Point p) {
        return  this.realRect;
    }

    public Size getPicSize() {
        return picSize;
    }

    public Size getRealSize() {
        return realSize;
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

}

