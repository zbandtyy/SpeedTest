package detectmotion.interestarea;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/11 21:21
 * @description：
 * @modified By：整个区域都在指定的范围内  【整张图片的检测范围都有效】
 * @version: $
 */
public class NULLTransform implements PerspectiveConversion {
    private Size picSize;            //原图宽高
    private Size realSize;            //     截图区域现实对应的宽高,用实际的m表示
    public void setPicSize(Size picSize) {
        this.picSize = picSize;
    }

    public void setRealSize(Size realSize) {
        this.realSize = realSize;
    }

    public  NULLTransform(){

    }

    public  NULLTransform( Size picSize,Size realSize){

        this.picSize = picSize;
        this.realSize = realSize;
    }
    public List<Point> transformPointList(List<Point> dst){
        return dst;
    }
    public boolean isInsidePicArea(Point p){
        return  true;
    }
    public double getXRatio(){
        // 实际大小             转换后的区域宽
        return      realSize.width / picSize.width ;
    }
    public double getYRatio(){
        return   realSize.height / realSize.height;
    }

    public Size getPicSize() {
        return picSize;
    }

    public Size getRealSize() {
        return realSize;
    }

    @Override
    public String toString() {
        return "NULLTransform{" +
                "picSize=" + picSize +
                ", realSize=" + realSize +
                '}';
    }
}
