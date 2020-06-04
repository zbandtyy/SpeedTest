package detectmotion;
import detectmotion.utils.MatWrapper;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import com.google.gson.Gson;
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
 * @description：对感兴趣的区域进行提取和透视变换，以获得鸟瞰坐标
 * @modified By：
 * @version: $
 */
public class IOTTransform {
    private static final Logger logger = Logger.getLogger(IOTTransform.class);
    Mat transformMap;//透视转换矩阵
    Mat inverseMap;//反向转换矩阵
    Mat picRect;  //感兴趣区域,四个点的坐标 单通道Mat
    Mat realRect;              //转换后的目标区域 单通道
    Size picSize;            //原图宽高
    Size realSize;            //     截图区域现实对应的宽高,用实际的m表示

    public IOTTransform(String jsoname){
        loadFromFile(jsoname);//从文件中加载所有的参数
    }
    Mat transformPoint(Mat src){

        src = MatWrapper.SingleTo2Channels(src);
       // logger.debug(MatWrapper.MatToString(src));
        Mat three_channel = Mat.zeros(src.rows(),src.cols(),CV_32FC2);
        Core.perspectiveTransform(src,three_channel,transformMap);
        return three_channel;
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

        ArrayList<List<Float>> picpoints = m.get("picRect");
        picRect = MatWrapper.doubleArrayToMat(picpoints,CV_32F);

        ArrayList<List<Float>> realpoints = m.get("realRect");
        realRect = MatWrapper.doubleArrayToMat(realpoints,CV_32F);

        ArrayList<Double> imageSize = m.get("imagSize");
        System.out.println();
        picSize = new Size(imageSize.get(0),imageSize.get(1));
        ArrayList<Double> real = m.get("realSize");
        realSize = new Size(real.get(0),real.get(1));
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String xmlPath =  Thread.currentThread().getContextClassLoader().getResource("calibrate_camera.json" ).getPath();//获IOTTransform its  =  new IOTTransform(xmlPath);
//      List<Integer> src = Arrays.asList(800,500);
//      List<Integer> src1 = Arrays.asList(800,600);
//      List<List<Integer>> dst = Arrays.asList(src,src1);
//      Mat  mmm = MatWrapper.doubleArrayToMat(dst);
//      Mat s = its.transformPoint(mmm);
//        IOTTransform iot = new IOTTransform(xmlPath);
//        List<Float> p2 = new ArrayList<>();
//        Mat m = iot.getIntrestAreaInPic();
//        System.out.println(m.type());
//        MatOfPoint2f area = new MatOfPoint2f(m);
//        Point p = new Point (200,300);
//        double res = Imgproc.pointPolygonTest(area,p,false);
//        System.out.println(res);


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

    public Mat getIntrestAreaInPic() {
        return picRect;
    }

    public Mat getIntrestAreaInReal() {
        return realRect;
    }

    public Size getPicSize() {
        return picSize;
    }

    public Size getRealSize() {
        return realSize;
    }
}
