package detectmotion.test;
import detectmotion.DetectCar;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.*;
import org.opencv.videoio.VideoCapture;

import java.util.Date;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

/**
 * @author ：tyy
 * @date ：Created in 2020/4/29 1:07
 * @description：使用KCF作为跟踪器进行跟踪
 * @modified By：
 * @version: $
 */
public class KCFTracker {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    DetectCar detector = null;
    private  static MultiTracker tracker = MultiTracker.create();
    public KCFTracker(){
        detector = new DetectCar();
    }

    public void processVideo(String videoName){
        //1.打开摄像头读输入
        VideoCapture videoCapture = new VideoCapture();
        boolean isopen = videoCapture.open(videoName);
        if(isopen == false) return;
        //2.对第一帧检测到的对象进行方框标记,并且对Tracker进行初始化
        Mat firstImage = new Mat();
        videoCapture.read(firstImage);
        List<Rect2d> imageBoundingBox = detector.detectObject(firstImage);
        for(Rect2d rect: imageBoundingBox){
            Imgproc.rectangle(firstImage,rect.tl(), rect.br(), new Scalar(0, 255, 0));
            tracker.add(TrackerMOSSE.create(),firstImage,rect);
        }
      //  imshow("first frame",firstImage);
        //waitKey(1000*2);
        //3.对其余的视频帧进行处理
        Mat frame = new Mat();
        MatOfRect2d objects = new MatOfRect2d();
        while (videoCapture.read(frame) ) {

            long start_time = new Date().getTime();
            tracker.update(frame,objects);
            long end_time = new Date().getTime();
            System.out.println(end_time - start_time);

            List<Rect2d> predictObjects = objects.toList();
            for (Rect2d obj:predictObjects) {
               Imgproc.rectangle(frame, obj.tl(), obj.br(), new Scalar(0, 255, 0));
            }

            imshow("process Image",frame);
            int keyboard = HighGui.waitKey(10);
            if (keyboard == 'q' || keyboard == 27) {
                break;
            }

        }


    }




    public static void main(String[] args) {
        String path = Thread.currentThread().getContextClassLoader().getResource("tese.mp4").getPath();//获取资源路径
        new KCFTracker().processVideo(path);
    }


}
