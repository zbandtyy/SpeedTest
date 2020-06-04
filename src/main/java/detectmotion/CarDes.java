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
    Rect2d pos;//检测出来的车子的位置  当前的位;
    Rect2d  previousPos = null;//检测出来的车子速度,更新一批数据的速度的第一次的位置
    Rect2d  netxPos = null;//检测出来的车子速度,更新一批数据的速度的第二次的位置
    double distance = -1;
    int CARWIDTH = 1800;//以mm为单位 实际车身宽度 小车长度都在3800mm到4300mm之间、宽度在1600mm到1800mm之间、高度在1400mm到1600mm之间
    long count ;//对当前经过车辆的标记数
    double speed = 0;
    Scalar color  = new Scalar(0,0,255);
    Tracker tracker;
    public  double getDistance(){
        if(previousPos != null && netxPos != null){
            double pcx = previousPos.x + previousPos.width/2;
            double pcy = previousPos.y + previousPos.height/2;
            double ccx = netxPos.x + netxPos.width/2;
            double ccy = netxPos.y + netxPos.height/2;
            return distance =  Math.sqrt((pcx - ccx) * (pcx -ccx)  + (pcy - ccy)*(pcy - ccy));
        }
        return distance = -1;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }


    public CarDes(Rect2d pos, long count,Tracker t) {
        this.pos = pos;
        this.tracker = t;
        this.count = count;
    }
    public CarDes(Rect2d pos, Integer count) {
        this.pos = pos;
        this.count = count;
    }


    boolean markedDelete = true;
    public void setMarkedDelete(boolean markedDelete) {
        this.markedDelete = markedDelete;
    }

    public boolean isMarkedDelete() {
        return markedDelete;
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

    double meter = 0;
    public double getMeter() {
        return meter;
    }
    double ratio = 0;

    public double getRatio() {
        return  CARWIDTH*1.0 / pos.width;
    }
    public double calculateSpeed(double timeSecondGap) {
        //获取车身的宽度
        distance = getDistance();
         meter = (distance * getRatio()) /1000;  //m
        this.speed = (meter/timeSecondGap)*3.6 * 1920 /1080; //mm /ms
        return  speed;
    }
    public double getSpeed() {
        return speed;
    }
    public double  getCarLength(){
        return pos.height * getRatio();
    }
}
