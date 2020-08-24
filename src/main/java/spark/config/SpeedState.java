package spark.config;

import detection.Detector;
import detectmotion.OpencvMultiTracker;
import detectmotion.SequenceOfFramesProcessor;
import detectmotion.TrackerList;
import detectmotion.detector.DetectCar;
import lombok.Getter;
import lombok.Setter;
import org.opencv.tracking.Tracker;
import scala.Serializable;

import java.util.Date;

/**
 * @author ：tyy
 * @date ：Created in 2020/6/24 2:17
 * @description：用于保存视频处理的状态，针对detectmotion.SequenceOfFramesProcessor.processFrames设计
 * @modified By：
 * @version: $
 */
public class SpeedState implements Serializable {
    //如果增加，不能使用原生类型int long 等
    private  static  final  long serialVersionUID = 16L;
    public Integer detectedFrameGap ;//设置检测的帧数间隔进行部分的识别，即识别的帧数
    @Getter @Setter
    public  Long firstFrameTime = 0l;
    @Getter @Setter
    public  Long lastFrameTime = 0l;
    public Integer frameCount = 0;//处理的是序列中的第几帧
    public String  multiTracker = "" ;

    public   SpeedState(SequenceOfFramesProcessor sep){
        this.detectedFrameGap = sep.getDetectedFrameGap();
        this.frameCount = sep.getFrameCount();
        multiTracker = sep.getMtracker().toJson();
        this.firstFrameTime = sep.getFirstFrameTime();
        this.lastFrameTime = sep.getLastFrameTime();
    }


    public void setDetectedFrameGap(int detectedFrameGap) {
        this.detectedFrameGap = detectedFrameGap;
    }

    public SpeedState() {

    }



    public void processFrame(){
        this.frameCount ++;
    }

    public int getDetectedFrameGap() {
        return detectedFrameGap;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public String getMultiTracker() {
        return multiTracker;
    }

    public void setMultiTracker(String multiTracker) {
        this.multiTracker = multiTracker;
    }

    @Override
    public String toString() {
        return "SpeedState{" +
                "detectedFrameGap=" + detectedFrameGap +
                ", frameCount=" + frameCount +
                ", multiTracker=" + multiTracker +
                '}';
    }
}
