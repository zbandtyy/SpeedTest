package spark.config;

import detectmotion.OpencvMultiTracker;
import detectmotion.SequenceOfFramesProcessor;
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
    public Integer detectedFrameGap ;//设置检测的帧数间隔进行部分的识别，即识别的帧数
    public Integer frameCount = 0;//处理的是序列中的第几帧
    public OpencvMultiTracker mtracker ;


    public SpeedState(Integer detectedFrameGap, Integer frameCount,OpencvMultiTracker mtracker) {
        this.detectedFrameGap = detectedFrameGap;
        this.frameCount = frameCount;

        this.mtracker =  mtracker ;
    }

    public   SpeedState(SequenceOfFramesProcessor sep){
        this.detectedFrameGap = sep.getDetectedFrameGap();
        this.frameCount = sep.getFrameCount();
        this.mtracker = sep.getMtracker();
    }

    public OpencvMultiTracker getMtracker() {
        return mtracker;
    }

    public void setMtracker(OpencvMultiTracker mtracker) {
        this.mtracker = mtracker;
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




    @Override
    public String toString() {
        return "SpeedState{" +
                "detectedFrameGap=" + detectedFrameGap +
                ", frameCount=" + frameCount +
                ", mtracker=" + mtracker +
                '}';
    }
}
