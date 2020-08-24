package detectmotion.interestarea;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Size;

import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/11 21:21
 * @description：
 * @modified By：整个区域都在指定的范围内  【整张图片的检测范围都有效】
 * @version: $
 */
@Slf4j
public class NULLTransform implements PerspectiveConversion {
    @Getter
    private Size picSize;            //原图宽高,即感兴趣的图片区域位置，NULL为整个图片都感兴趣
    @Getter
    private Size realSize;            //     截图区域现实对应的宽高,用实际的m表示

    public static final double CARWIDTH = 2;
    public static final double CARHEIGHT = 5;
    private double scaleX = 0;
    private double scaleY = 0;

    public void setPicSize(Size picSize) {
        this.picSize = picSize;
    }

    public void setRealSize(Size realSize) {
        this.realSize = realSize;
    }

    private Point getRectCenter(Rect2d r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    public NULLTransform() {

    }

    public NULLTransform(Size picSize, Size realSize) {

        this.picSize = picSize;
        this.realSize = realSize;
        // 实际大小        /     转换后的区域宽
        scaleX = realSize.width / picSize.width;
        scaleY = realSize.height / realSize.height;
    }

    public List<Point> transformPointList(List<Point> dst) {
        return dst;
    }

    //判断是否在感兴趣的区域，升级版本
    public boolean isInsidePicArea(Point p) {
        return true;

    }

    public double getXRatio() {

        return scaleX;
    }

    public double getYRatio() {
        return scaleY;
    }

    @Override
    public double getDistance(Point previousPos, Point netxPos) {

        if (previousPos != null && netxPos != null) {
            double pcx = previousPos.x;
            double pcy = previousPos.y;
            double ccx = netxPos.x;
            double ccy = netxPos.y;
            if (pcx < 0 || pcy < 0 || ccx < 0 || ccy < 0) {
                System.out.println("previouspos = ===============================================" + previousPos);
                System.out.println("nextpos" + netxPos);
            }
            log.warn("leave: previous" + previousPos + "next" + netxPos);
            return Math.sqrt(((pcx - ccx) * (pcx - ccx) * scaleX * scaleX) +
                    ((pcy - ccy) * (pcy - ccy) * scaleY * scaleY));
        }
        return -1;
    }

    @Override
    public double calculateSpeed(Rect2d pre, Rect2d p, double time) {

        scaleX = (((pre.width / CARWIDTH) + (p.width / CARWIDTH))) / 2;
        scaleY = (((pre.height / CARHEIGHT) + (p.height / CARHEIGHT))) / 2;
        Point precenter = getRectCenter(pre);
        Point pcenter = getRectCenter(p);
        double dis = getDistance(precenter, pcenter);
        log.warn("distance"+ dis + "speed == " + (dis / time) * 1000);
        return (dis / time) * 1000;// m / s

    }


    @Override
    public String toString() {
        return "NULLTransform{" +
                "picSize=" + picSize +
                ", realSize=" + realSize +
                '}';
    }
}
