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
public class CarDes implements   Serializable {
    private static final long serialVersionUID = 1L;
    private Rect2d pos;//检测出来的车子的位置  当前的位置;  实时位置
    private Rect2d  previousPos = null;//更新一批数据的速度的第一次的位置  如果更新间隔为10帧，那么就是第一次10帧
    private Rect2d  netxPos = null;//更新一批数据的速度的第二次的位置   第二次10帧
    long count ;//对当前经过车辆的标记数
    double speed = 0;
    private Double carLength ;
    private transient Tracker tracker; //新数据的时候可以new处理
    int markedLost = 0;
    int phase = 0;// 0 tracker阶段, 1 检测阶段

    public  CarDes(){}

    public void setCount(long count) {
        this.count = count;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getMarkedLost() {
        return markedLost;
    }

    public void setMarkedLost(int markedLost) {
        this.markedLost = markedLost;
    }

    public Double getCarLength() {
        return carLength;
    }

    public void setCarLength(Double carLength) {
        this.carLength = carLength;
    }

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

    public void setMarkedDelete() {
        this.markedLost ++;
    }

    public int isMarkedDelete() {
        return markedLost;
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
                ",tracker=" + tracker +
                '}';
    }
}
