package detectmotion.interestarea;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/11 21:21
 * @description：
 * @modified By：
 * @version: $
 */
public class NULLTransform implements PerspectiveConversion {
    private Rect transformAfter;//转换后的矩阵宽高
    private Size picSize;            //原图宽高
    private Size realSize;            //     截图区域现实对应的宽高,用实际的m表示

    public void setPicSize(Size picSize) {
        this.picSize = picSize;
    }

    public void setRealSize(Size realSize) {
        this.realSize = realSize;
    }

    public  NULLTransform(){
        picSize = new Size(640,480);
        realSize =  new Size(18,200);
    }

    public List<Point> transformPointList(List<Point> dst){
        return dst;
    }
    public boolean isInsidePicArea(Point p){
        return  true;
    }
    public double getXRatio(){
        // 实际大小             转换后的区域宽
        return      1 ;
    }
    public double getYRatio(){
        return   1;
    }

    public Size getPicSize() {
        return picSize;
    }
}
