package detectmotion.interestarea;

import org.opencv.core.Point;
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
    public double getXRatio();
    public double getYRatio();


}
