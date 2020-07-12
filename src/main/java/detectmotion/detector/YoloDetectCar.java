package detectmotion.detector;

import detection.Box;
import detection.BoxesAndAcc;
import detection.Detector;
import detectmotion.utils.RectCompute;
import detectmotion.yolo.ObjectDetector;
import detectmotion.yolo.classifier.YOLOClassifier;
import detectmotion.yolo.model.BoxPosition;
import detectmotion.yolo.model.Recognition;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import spark.config.AppConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;

/**
 * @author ：tyy
 * @date ：Created in 2020/6/10 1:33
 * @description：
 * @modified By：
 * @version: $
 */
public class YoloDetectCar implements DetectCar {
    Detector obj;
    List<Rect2d> detectedObjects;
    public YoloDetectCar(){
        obj = Detector.getYoloDetector(AppConfig.YOLO_RESOURCE_PATH);
    }
    @Override
    public List<Rect2d> detectObject(Mat frame) {

        MatOfByte bytemat = new MatOfByte();

        Imgcodecs.imencode(".jpg", frame, bytemat);

        byte[] bytes = bytemat.toArray();
        BoxesAndAcc[] res = obj.startYolo(bytes, frame.width(), frame.height(), frame.channels());
        ArrayList<Rect2d> last= new ArrayList<>(res.length);
        for (BoxesAndAcc re : res) {
            if(re.isVaild() == true && re.getNames().equals("car")) {
                Box bb = re.getBoxes();
                last.add(new Rect2d(bb.getX(), bb.getY(), bb.getW(), bb.getH()));
            }
        }
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


    public static void main(String[] args) {
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

}
