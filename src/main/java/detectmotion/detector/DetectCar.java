package detectmotion.detector;

import org.opencv.core.Mat;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/6/10 1:19
 * @description：
 * @modified By：
 * @version: $
 */
public interface DetectCar {
    List<Rect2d> detectObject(Mat frame);
     void  showBoundigBox(Mat frame) ;//在图片上显示bjects

}
