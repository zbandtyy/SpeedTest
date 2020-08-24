package testIOT;

import detectmotion.interestarea.IOTTransform;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;

import java.io.IOException;
import java.nio.charset.CoderResult;
import java.util.Arrays;
import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/8/20 20:26
 * @description：
 * @modified By：
 * @version: $
 */
@Slf4j
public class TestIOT {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    private  static double getDistance(Point previousPos, Point netxPos, double xratio, double yratio){

        if(previousPos != null && netxPos != null){
            double pcx = previousPos.x ;
            double pcy = previousPos.y ;
            double ccx = netxPos.x;
            double ccy = netxPos.y ;

            if(pcx < 0 || pcy <0 || ccx < 0 || ccy < 0){
                log.warn(  " has position out of size");
            }
            return  Math.sqrt( ((pcx - ccx) *(pcx -ccx)*xratio*xratio)  +
                    ((pcy - ccy)*(pcy - ccy)*yratio*yratio)   );
        }
        return -1;
    }
    public static void main(String[] args) throws IOException {
        IOTTransform iot = new IOTTransform("E:\\spark\\speed-test\\speedtest\\src\\main\\resources" +
                "\\multitracker\\calibrate_camera_scale.json");
        //  真实值/实际值
        double xratio = iot.getXRatio();
        double yratio = iot.getYRatio();
        Point precenter = new Point(897.0,398);
        Point pcenter =  new Point(897,474);
        List<Point> list = Arrays.asList(precenter, pcenter);
        List<Point> res = iot.transformPointList(list);
        log.warn("xratio=" + xratio + "yratio=" + yratio);
        log.warn("rawx" + precenter + "rawy" + pcenter);
        double dis = getDistance(res.get(0),res  .get(1), xratio, yratio);
        log.warn("time == "  + "distance" + dis + "res[0]" + res.get(0) + "res[1]" + res.get(1));
        return ;//

    }
}
