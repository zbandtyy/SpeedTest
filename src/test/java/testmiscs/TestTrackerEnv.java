package testmiscs;

import org.opencv.tracking.Tracker;
import org.opencv.tracking.TrackerMOSSE;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/14 0:28
 * @description：
 * @modified By：
 * @version: $
 */
public class TestTrackerEnv {
    public static void main(String[] args) {
        System.load("/home/user/Apache/opencv3.4.7-install/lib/libopencv_java347.so");
        TrackerMOSSE.create();
    }
}
