package detectmotion.interestarea;

import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Size;

import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/11 21:19
 * @description：
 * @modified By：
 * @version: $
 */
public interface PerspectiveConversion {

    public List<Point> transformPointList(List<Point> dst);
    public Size getPicSize();
    public boolean isInsidePicArea(Point p);
    /**
     *
     * @param pre 再图片中的计算位置,  第一次检测
     *  @param p 再图片中的计算位置，第二次检测
     * @return
     */
    public  double getDistance(Point pre,Point p);

    /**
     *
     * @param pre 上一次的位置
     * @param p 当前次的位置
     * @param time 时间
     * @return
     */
    public   double calculateSpeed(Rect2d pre, Rect2d p, double time);


}
