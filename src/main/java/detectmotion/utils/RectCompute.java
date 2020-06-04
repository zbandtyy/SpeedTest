package detectmotion.utils;


import org.opencv.core.Mat;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;

import java.awt.*;
import java.util.function.Predicate;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/2 3:24
 * @description：
 * @modified By：
 * @version: $
 */
public class RectCompute  {

    private Rect2d rect1;
    private Rect2d rect2;

    public RectCompute(Rect2d r1, Rect2d r2) {
        this.rect1 = r1;
        this.rect2 = r2;
    }

    public double centerDistance(){
        double c1_x = (rect1.x + rect1.width / 2);
        double c1_y = (rect1.y + rect1.height / 2);

        double c2_x = (rect2.x + rect2.width / 2);
        double c2_y = (rect2.y + rect2.height / 2);

        return   Math.sqrt((c1_x - c2_x) * (c1_x - c2_x)  + (c2_y - c1_y)*(c2_y - c1_y));

    }

    public static  double getOverlappedArea( Rect2d rect1,Rect2d rect2){
        if (rect1 == null || rect2 == null) {
            return -1;
        }

        if (rect1 == null || rect2 == null) {
            return -1;
        }
        double p1_x = rect1.x, p1_y = rect1.y;
        double p2_x = p1_x + rect1.width, p2_y = p1_y + rect1.height;
        double p3_x = rect2.x, p3_y = rect2.y;
        double p4_x = p3_x + rect2.width, p4_y = p3_y + rect2.height;
        if (p1_x > p4_x || p2_x < p3_x || p1_y > p4_y || p2_y < p3_y) {
            return 0;
        }
        double Len = Math.min(p2_x, p4_x) - Math.max(p1_x, p3_x);
        double Wid = Math.min(p2_y, p4_y) - Math.max(p1_y, p3_y);
        return Len * Wid;

    }
    public boolean isFullContain(){
        if (rect1 == null || rect2 == null) {
            return  false;
        }


        if(rect2.tl().inside(new Rect(rect1.tl(),rect1.br())) &&  rect2.br().inside(new Rect(rect1.tl(),rect1.br()))){
            return  true;
        }
        return false;

    }
    public  double containRatio(){

        double minArea = Math.min(rect1.area(),rect2.area());

        return  minArea;
    }
    public static void main(String[] args) {
        Rect2d r1 = new Rect2d(10,10,20,20);


        Rect2d r2 = new Rect2d(11,11,21,15);
        System.out.println(getOverlappedArea(r1,r2));
     //   System.out.println(new RectCompute(r1,r2).isFullContain());

    }



}
