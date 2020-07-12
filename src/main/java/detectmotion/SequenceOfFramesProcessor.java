package detectmotion;

import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import spark.config.SpeedState;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import spark.config.AppConfig;
import spark.type.VideoEventData;

import java.io.Serializable;
import java.util.Base64;
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
    private static final Logger logger = Logger.getLogger(SequenceOfFramesProcessor.class);

    static {
        System.out.println("loal opencv");
     //   System.load(AppConfig.OPENCV_LIB_FILE);
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("loal opencv success");
    }
    int detectedFrameGap ;//设置检测的帧数间隔进行部分的识别，即识别的帧数
    int frameCount = 0;//处理的是序列中的第几帧
    private  final double VIDEO_FPS = 1.0/25;
    private long batchstarttime = new Date().getTime(); //处理开始时间
    private long batchendtime = 0;
    private double FPS = 20;
    public  static OpencvMultiTracker mtracker ;//所有的sequenceOfFrameProcessor都只拥有一个MultiTracker对象
    long firstframetime = 0;//第一帧实际时间
    long lastframetime = 0;//detectedFrameGap 的实际时间
    //一个cameraID中机器中应该只有一个这种对象
    public  SequenceOfFramesProcessor(){

    }

    public SequenceOfFramesProcessor(SpeedState state) {
        this.detectedFrameGap = state.getDetectedFrameGap();
        this.frameCount = state.getFrameCount();
       // this.mtracker = state.getMtracker();
    }



    public int getDetectedFrameGap() {
        return detectedFrameGap;
    }

    public void setDetectedFrameGap(int detectedFrameGap) {
        this.detectedFrameGap = detectedFrameGap;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public double getVIDEO_FPS() {
        return VIDEO_FPS;
    }

    public long getBatchstarttime() {
        return batchstarttime;
    }

    public void setBatchstarttime(long batchstarttime) {
        this.batchstarttime = batchstarttime;
    }

    public long getBatchendtime() {
        return batchendtime;
    }

    public void setBatchendtime(long batchendtime) {
        this.batchendtime = batchendtime;
    }

    public double getFPS() {
        return FPS;
    }

    public void setFPS(double FPS) {
        this.FPS = FPS;
    }

    public OpencvMultiTracker getMtracker() {
        return mtracker;
    }

    public void setMtracker(OpencvMultiTracker mtracker) {
        this.mtracker = mtracker;
    }

    public long getFirstframetime() {
        return firstframetime;
    }

    public void setFirstframetime(long firstframetime) {
        this.firstframetime = firstframetime;
    }

    public long getLastframetime() {
        return lastframetime;
    }

    public void setLastframetime(long lastframetime) {
        this.lastframetime = lastframetime;
    }

    public SequenceOfFramesProcessor (Integer detectedFrameGap, String iotTransformFileName) {
        this.detectedFrameGap = detectedFrameGap;
        mtracker = new OpencvMultiTracker(iotTransformFileName);
    }
    //每一批数据调用一次processFrames

    /***
     *
     * @param eventDatas :接受到的一组数据
     */
    public  SequenceOfFramesProcessor  processFrames(List<VideoEventData> eventDatas){
        MatOfByte mob = new MatOfByte();
        for (VideoEventData ev : eventDatas) {
            Mat frame1 = ev.getMat();
            Size size = mtracker.iot.getPicSize();
            Mat frame = new Mat(frame1.rows(),frame1.cols(),frame1.type());
            if(size.height != frame1.rows() || size.width != frame1.cols()){
                Imgproc.resize(frame1,frame,size);
            }
            //Imgcodecs.imwrite("/home/user/share/shared/spark-example/speedtest/"+frameCount+".jpg",frame);
            if(frame == null){
                logger.info("frame is null,what happen?");
                continue;
            }else {
                logger.info("this is "+ frameCount +  " \n\n"  );
            }
            if (frameCount % detectedFrameGap  == 0) {
                if(frameCount == 0) {
                    firstframetime =  lastframetime = ev.getTime();
                } else{
                    firstframetime = lastframetime;
                    lastframetime = ev.getTime();
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

           // Imgcodecs.imencode(".jpg", frame, mob);
           // Imgcodecs.imwrite("/home/user/share/shared/spark-example/speedtest/"+frameCount+"-result.jpg",frame);
            // convert the "matrix of bytes" into a byte array

            byte[] data = new byte[(int) (frame.total() * frame.channels())];
            frame.get(0, 0, data);
            Imgproc.resize(frame,frame,new Size(ev.getCols(),ev.getRows()));
            ev.setData( Base64.getEncoder().encodeToString(data));
//            imshow("processed",frame);
//            waitKey(10);
        }
        return  this;
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

    @Override
    public String toString() {
        return "SequenceOfFramesProcessor{" +
                "detectedFrameGap=" + detectedFrameGap +
                ", frameCount=" + frameCount +
                ", batchstarttime=" + batchstarttime +
                ", batchendtime=" + batchendtime +
                ", FPS=" + FPS +
                ", mtracker=" + mtracker +
                ", firstframetime=" + firstframetime +
                ", lastframetime=" + lastframetime +
                '}';
    }

    public static void main(String[] args) {
        String path = Thread.currentThread().getContextClassLoader().getResource("test1.mp4").getPath();//获取资源路径
        String xmlPath =  Thread.currentThread().getContextClassLoader().getResource("multitracker/calibrate_camera_scale.json" ).getPath();//获
        SequenceOfFramesProcessor opencv = new SequenceOfFramesProcessor(10,xmlPath);
        opencv.processVideo(path);
    }
}
