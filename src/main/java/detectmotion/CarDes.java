package detectmotion;

import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.tracking.Tracker;

import java.io.*;


/**
 * @author ：tyy
 * @date ：Created in 2020/5/1 22:29
 * @description：用于对车子进行保存，主要用于对帧的信息进行更新
 * @modified By：
 * @version: $
 */
public class CarDes implements  Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private Rect2d pos;//检测出来的车子的位置  当前的位置;  实时位置
    private Rect2d  previousPos = null;//更新一批数据的速度的第一次的位置  如果更新间隔为10帧，那么就是第一次10帧
    private Rect2d  netxPos = null;//更新一批数据的速度的第二次的位置   第二次10帧
    long count ;//对当前经过车辆的标记数
    double speed = 0;
    Scalar color  = new Scalar(0,0,255);
    private Double carLength ;
    private Tracker tracker;

    public Double getCarLength() {
        return carLength;
    }

    public void setCarLength(Double carLength) {
        this.carLength = carLength;
    }


    int phase = 0;// 0 tracker阶段, 1 检测阶段

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getPhase() {
        return phase;
    }

    public void setPos(Rect2d pos) {
        this.pos = pos;
    }

    public CarDes(Rect2d pos, long count, Tracker t, double speed) {
        this.pos = pos;
        this.tracker = t;
        this.count = count;
    }


    int markedLost = 0;
    public void setMarkedDelete() {
        this.markedLost ++;
    }

    public int isMarkedDelete() {
        return markedLost;
    }

    public Scalar getColor() {
        return color;
    }

    public void setColor(Scalar color) {
        this.color = color;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public Rect2d getPos() {
        return pos;
    }

    public long getCount() {
        return count;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Rect2d getPreviousPos() {
        return previousPos;
    }
    public void setPreviousPos(Rect2d previousPos) {
        this.previousPos = previousPos;
    }

    public Rect2d getNetxPos() {
        return netxPos;
    }
    public void setNetxPos(Rect2d netxPos) {
        this.netxPos = netxPos;
    }


    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "CarDes{" +
                "pos=" + pos +
                ", count=" + count +
                ", speed=" + speed +
                '}';
    }
}
