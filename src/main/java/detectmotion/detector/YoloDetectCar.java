package detectmotion.detector;

import detection.Box;
import detection.BoxesAndAcc;
import detection.Detector;
import detectmotion.utils.RectCompute;
import detectmotion.yolo.ObjectDetector;
import detectmotion.yolo.classifier.YOLOClassifier;
import detectmotion.yolo.model.BoxPosition;
import detectmotion.yolo.model.Recognition;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import scala.App;
import spark.ReadPhoto;
import spark.config.AppConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * @author ：tyy
 * @date ：Created in 2020/6/10 1:33
 * @description：
 * @modified By：
 * @version: $
 */
public class YoloDetectCar implements DetectCar, Serializable {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(YoloDetectCar.class);
    Detector obj;
    List<Rect2d> detectedObjects;
    public YoloDetectCar(){//每批会进行创建
        obj = Detector.getYoloDetector(AppConfig.YOLO_RESOURCE_PATH);
    }
    @Override
    public List<Rect2d> detectObject(Mat m) {

        long size = m.total() * m.elemSize();
        byte bytes[] = new byte[(int)size];  // you will have to delete[] that later
        m.get(0,0,bytes);
        System.out.println( m.width() +"  " +m.height()  +" "+ m.channels());
        BoxesAndAcc[] res = obj.startYolo(bytes, m.width(), m.height(), m.channels());
        String name = Thread.currentThread().getName();
        System.out.println(name + "end yolo" );
        if(res.length  <= 0){
            logger.warn("detector number < 0" + res.length);
            return null;
        }
        ArrayList<Rect2d> last= new ArrayList<>();
        for (BoxesAndAcc re : res) {
            if( re.isVaild() == true && re.getNames().equals("car") ) {
                logger.info("recognize object" + re);
                if(re.getBoxes().getX() <0||re.getBoxes().getY() < 0||
                        re.getBoxes().getH() < 0 || re.getBoxes().getW() < 0){
                    continue;
                }
                Rect tmp  = re.transfor(m.width(),m.height());
               last.add(new Rect2d(tmp.x,tmp.y,tmp.width,tmp.height));
                logger.info(tmp);
            }
        }
        logger.info("car length is:" + last.size());
        return  last;
   }
   public void removeOverlapped(){
        if(detectedObjects == null)
            return;
       ArrayList<Rect2d> deleted = new ArrayList<>();
       for (int i = 0; i < detectedObjects.size(); i++) {
           for (int i1 = 0; i1 < detectedObjects.size() ; i1++) {
               boolean rc =  RectCompute.isFullContain(detectedObjects.get(i),detectedObjects.get(i1));//i1 在 i的里面,那么删除小的
               if(rc == true) {
                   deleted.add(detectedObjects.get(i1));
               }
           }

       }
       detectedObjects.removeAll(deleted);
   }

    @Override
    public void showBoundigBox(Mat frame) {
        for (int i = 0; i < detectedObjects.size(); i++) {
            Imgproc.rectangle(frame,detectedObjects.get(i).tl(), detectedObjects.get(i).br(), new Scalar(0, 255, 0));
        }
    }

    public  void testVideo(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture();
        String path = Thread.currentThread().getContextClassLoader().getResource("test1.mp4").getPath();//获取资源路径
        System.out.println(path);
        DetectCar dc = new YoloDetectCar();
        Mat image = new Mat();
        int frameCount = -1;
        videoCapture.open(path);
        List<Rect2d> listAll = new ArrayList<>();
        while (true) {

            if (!videoCapture.read(image)) {
                break;
            }
            if(frameCount % 20 == 0) {
                long start = new Date().getTime();
                List<Rect2d> l = dc.detectObject(image);
                long end = new Date().getTime();
                dc.showBoundigBox(image);
                imshow("process Image", image);
                double fps = 1000.0 / (end - start);
                System.out.println(end - start);
                Imgproc.putText(image, "FPS:" + Double.toString(fps), new Point(20, 20), Core.FONT_HERSHEY_SIMPLEX, 0.75, new Scalar(255, 11, 0), 2);
                int keyboard = HighGui.waitKey(30);
                if (keyboard == 'q' || keyboard == 27) {
                    break;
                }
                System.out.println("have process" + frameCount);
                System.out.println("================================");

            }
            frameCount++;
        }
        System.exit(0);
    }
    public static void main(String[] args) {
        System.load(AppConfig.OPENCV_LIB_FILE);
        System.load(AppConfig.YOLO_LIB_FILE);
        Mat m = imread("/home/user/Apache/App1/exe/0.jpg", 1);
        ///////////////YOLO识别//////////////////
        YoloDetectCar dc = new YoloDetectCar();
        dc.detectObject(m);

    }

}
