package detectmotion;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import spark.type.VideoEventData;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/31 12:57
 * @description： 对连续帧的处理进行初始化,即对视频的处理进行初始化工作，并且完成整个流程
 * @modified By：
 * @version: $
 */
public class SequenceOfFramesProcessor implements Serializable {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    int detectedFrameGap ;//设置检测的帧数间隔进行部分的识别，即识别的帧数
    int frameCount = 0;//处理的是序列中的第几帧
    private static final double VIDEO_FPS = 1.0/25;
    private long batchstarttime = new Date().getTime(); //处理开始时间
    private long batchendtime = 0;
    private double FPS = 20;
    OpencvMultiTracker mtracker ;
    long firstframetime = 0;//第一帧实际时间
    long lastframetime = 0;//detectedFrameGap 的实际时间
    //一个cameraID中机器中应该只有一个这种对象
    public SequenceOfFramesProcessor (Integer detectedFrameGap, String iotTransformFileName) {
        this.detectedFrameGap = detectedFrameGap;
        mtracker = new OpencvMultiTracker(iotTransformFileName);
    }
    //每一批数据调用一次processFrames

    /***
     *
     * @param eventDatas :接受到的一组数据
     */
    public  void  processFrames(List<VideoEventData> eventDatas){
        MatOfByte mob = null;

        for (VideoEventData ev : eventDatas) {
            Mat frame = ev.getMat();
            if (frameCount % detectedFrameGap  == 0) {
                if(frameCount == 0) {
                    firstframetime =  lastframetime = ev.getTimestamp();
                } else{
                    firstframetime = lastframetime;
                    lastframetime = ev.getTimestamp();
                }
                FPS = updateFPS();
                mtracker.detectAndCorrectObjofFrame(frame);
            } else {
                mtracker.trackObjectsofFrame(frame);
            }
            long gaptime  = lastframetime - firstframetime;
            mtracker.drawStatistic(frame,FPS);
            mtracker.drawTrackerBox(frame,gaptime );//speed count
            frameCount++;
            Imgcodecs.imencode(".jpg", frame, mob);
            // convert the "matrix of bytes" into a byte array
            byte[] byteArray = mob.toArray();
            ev.setFrame(byteArray);
//            imshow("processed",frame);
//            waitKey(10);
        }
    }

    public void processVideo(String videoName){
        //1.打开摄像头读输入，
        VideoCapture videoCapture = new VideoCapture();
        boolean isopen = videoCapture.open(videoName);
        if(isopen == false) return;
        //2.对第一帧检测到的对象进行方框标记,并且对Tracker进行初始化
        Mat frame = new Mat();
        while (videoCapture.read(frame) ) {

            //1.实际检测的位置
            if (frameCount % detectedFrameGap  == 0) {
                FPS = updateFPS();
                mtracker.detectAndCorrectObjofFrame(frame);
 //               mtracker.drawTrackerBox(frame,10);
//                mtracker.drawBoundigBox(frame);
//
//                    imshow("pppp", frame);
//                    int key = waitKey(1000000);
//                    if (key == 16) {
//                        frameCount++;
//                        continue;
//
//                    }

            } else {
                mtracker.trackObjectsofFrame(frame);
            }
            mtracker.drawStatistic(frame,FPS);
            mtracker.drawTrackerBox(frame,detectedFrameGap * VIDEO_FPS);//speed count
            frameCount++;
            imshow("processed",frame);
            waitKey(10);

        }
    }

    public double updateFPS(){
        batchstarttime = batchendtime;
        batchendtime = new Date().getTime();
        double bacthFPS =  detectedFrameGap*1.0 / (batchendtime  - batchstarttime)*1000 ;
        return bacthFPS;
    }

    public static void main(String[] args) {
        String path = Thread.currentThread().getContextClassLoader().getResource("test1.mp4").getPath();//获取资源路径
        String xmlPath =  Thread.currentThread().getContextClassLoader().getResource("multitracker/calibrate_camera_scale.json" ).getPath();//获
        SequenceOfFramesProcessor opencv = new SequenceOfFramesProcessor(10,xmlPath);
        opencv.processVideo(path);
    }
}
