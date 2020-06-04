package detectmotion;
import detectmotion.utils.RectCompute;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.tracking.Tracking;
import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.ellipse;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;


/**
 * @author ：tyy
 * @date ：Created in 2020/4/27 21:14
 * @description： 检测对象类
 * @modified By：
 * @version: $
 */

public class DetectCar {
    CascadeClassifier cascade;

    String xmlList[] = {
            "car_1.xml",
            "car_2.xml",
            "car_3.xml",
            "myhaar.xml",
            "pedestrian_1.xml",
            "pedestrian_2.xml",
            "fullbody.xml"
    };

    int WIDTH = 720;
    int HEIGHT = 560;
    public  DetectCar(){
        cascade = new CascadeClassifier();//用的是Haar或者LBP提取特征:滑动窗口机制+级联分类器的方式
        String xmlPath =  Thread.currentThread().getContextClassLoader().getResource(xmlList[3] ).getPath();//获取资源路径
        if(System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            xmlPath=xmlPath.substring(1);
        }
        System.out.println("load xml" + xmlPath);
        cascade.load(xmlPath);
    }

    public List<Rect2d> detectObject(Mat frame)
    {
        Mat cur = new Mat();
        Mat frame_gray = new Mat();
        cvtColor(frame, frame_gray,COLOR_RGB2GRAY);
        //-- Detect objects
        MatOfRect outputs = new MatOfRect();
        //minNeighbors（3）：表示每一个目标至少要被检测到3次才算是真的目标(因为周围的像素和不同的窗口大小都可以检测到人脸),
        //CV_HAAR_SCALE_IMAGE表示不是缩放分类器来检测，而是缩放的图像
        cascade.detectMultiScale(frame_gray, outputs, 1.5, 13, 0 , new Size(24, 24));
        Rect[] objects =outputs.toArray();
        //进行过滤
        List<Rect2d> resultRects = new LinkedList<>();
        ArrayList<Rect2d> deleted = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            Rect2d s = new Rect2d(objects[i].x, objects[i].y, objects[i].width, objects[i].height);
            resultRects.add(s);
            deleted.clear();
            for (int i1 = 0; i1 < resultRects.size()  - 1; i1++) {
                Double rc = RectCompute.getOverlappedArea(s,resultRects.get(i1));
                if(rc > 70){
                    if(s.area() > resultRects.get(i1).area() ){
                        deleted.add(resultRects.get(i1));
                    }else {
                        deleted.add(s);
                    }
                }
            }
            resultRects.removeAll(deleted);
        }

        return resultRects ;
    }
    public  void  showBoundigBox(Mat frame, List<Rect2d> objects) {
        for (int i = 0; i < objects.size(); i++) {
            Imgproc.rectangle(frame,objects.get(i).tl(), objects.get(i).br(), new Scalar(0, 255, 0));

        }
    }
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture();
        String path = Thread.currentThread().getContextClassLoader().getResource("tese.mp4").getPath();//获取资源路径
        System.out.println(path);
        DetectCar dc = new DetectCar();
        Mat image = new Mat();
        int frameCount = -1;
        videoCapture.open(path);
        List<Rect2d> listAll = new ArrayList<>();
        while (true) {
            if (!videoCapture.read(image)) {
                break;
            }
            long  start= new Date().getTime();
            List<Rect2d> l = dc.detectObject(image);
            long end = new Date().getTime();
            dc.showBoundigBox(image,l);
            imshow("process Image",image);
            double fps = 1000.0/(end - start);
            System.out.println(end - start);
            Imgproc.putText(image,"FPS:" + Double.toString(fps),new Point(20,20),Core.FONT_HERSHEY_SIMPLEX,0.75,new Scalar(255,11,0),2);
            int keyboard = HighGui.waitKey(30);
            if (keyboard == 'q' || keyboard == 27) {
                break;
            }
            frameCount++;
            System.out.println("have process" + frameCount);
        }
        System.exit(0);
    }
}
