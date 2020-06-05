package detectmotion;
import detectmotion.tuple.Tuple;
import detectmotion.tuple.Tuple2;
import detectmotion.tuple.Tuple3;
import detectmotion.utils.RectCompute;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.text.DecimalFormat;
import java.util.*;
import static org.opencv.core.Core.FONT_HERSHEY_SIMPLEX;

/**
 * @author ：tyy
 * @date ：Created in 2020/4/29 1:07
 * @description：使用KCF作为跟踪器进行跟踪，维护一个TrackerList 并且对总的统计数据进行管理更新,只要该类会修改TrackerList的值,或者获取该类的值
 * @modified By：
 * @version: $
 */
public class OpencvMultiTracker {
    IOTTransform iot ;

    private static final Logger logger = Logger.getLogger(OpencvMultiTracker.class);

    private   TrackerList trackers; ;
    DetectCar detector = null;
    public OpencvMultiTracker(String jsonName){
        detector = new DetectCar();
        iot = new IOTTransform(jsonName);
        trackers= new TrackerList()
                  .setIOT(iot);
    }



    /***
     *
     * @param predictedObjtects  预测的目标
     * @param dectedObjects  检测到的目标
     * @param MatchedPreictObjects 已经与检测对象匹配的跟踪对象，是(tracker的编号，trackr的位置)组成的键值对
     * @return  预测的目标中  需要在原有tracker中进行更新的对象
     */
    private Map<Integer, Rect2d> findSameObjectByArea(List<Rect2d> predictedObjtects, List<Rect2d> dectedObjects, Map<Integer, Rect2d> MatchedPreictObjects){

        if(predictedObjtects.size() <= 0 || dectedObjects.size() <= 0){
            return  null;
        }
        if(MatchedPreictObjects == null) {
            MatchedPreictObjects = new HashMap<Integer, Rect2d>();       //数据采用的哈希表结构
        }
        logger.debug("一开始检测到的对象" + dectedObjects.size());
        logger.debug("预测的对象" + predictedObjtects.size());

        //  3.在预测的位置中 找一个与  检测的结果中心点最近的一个，表明是统一车辆
        ArrayList<Rect2d> unew = new ArrayList<>();
        for (Rect2d pRect:dectedObjects) {//以检测的结果为准
            Integer maxIndex = -1;
            double maxArea = -1;
            for (int i = 0; i < predictedObjtects.size(); i++) {
                double area = RectCompute.getOverlappedArea(pRect,predictedObjtects.get(i));
                //策略1  找重合面积最大的
                if( area > 10 && maxArea < area  ){//预测位置与现有的位置重合位置  最大
                    maxArea = area;
                    maxIndex = i;
                }
            }
            //确定是与上一帧目标相同的patch ,
            if (maxArea > 0 && MatchedPreictObjects.get(maxIndex)  == null ) {
                logger.debug("detected" + pRect);
                logger.debug("MaxArea" + maxArea);
                logger.debug("prect" + predictedObjtects.get(maxIndex));
                unew.add(pRect);
                MatchedPreictObjects.put(maxIndex,pRect);
            }
        }
        logger.debug("重合的对象" + unew.size());
        dectedObjects.removeAll(unew);
        logger.debug("剩余的预测对象" + predictedObjtects.size());
        logger.debug("剩余的检测对象" + dectedObjects.size());
        return  MatchedPreictObjects;
    }

        /***
         *用检测数据修正
         * @param dectedObjects  实际检测的目标位置
         */
    public void correctBounding(Mat frame, List<Rect2d> dectedObjects){
        ArrayList<Rect2d> trackedcarposes = trackers.getTrackedCarsPos();
        if( dectedObjects == null )
            return;
        else  if(trackedcarposes == null || trackedcarposes.size() == 0){
             createTrackerInList(frame,dectedObjects);
            return;
        }else if(dectedObjects.size() <= 0  ){//检测不出来对象 就使用预测对象替代
            return;
        }
        //1.根据重合范围进行第一次更新
        Map<Integer, Rect2d> needAlterTrackerPos = findSameObjectByArea(trackedcarposes, dectedObjects, null);
        trackers.updateTrackPos(frame,needAlterTrackerPos);//对原来的进行位置跟踪更新，重新创建跟踪器进行跟踪【会删除之前维护的所有对象，重新创建】
        createTrackerInList(frame,dectedObjects);//表示为新检测到的新物体,创建新的tracker

    }
    public  void saveDetectorStat(){
        trackers.updatePhaseAndnexPoses();
    }
    public  void cleanLost(int losttime){

        trackers.cleanLostedTrackers(30);
    }
    public void createTrackerInList(Mat frame,List<Rect2d> willAddCar){
        if(iot != null) {
            ArrayList<Rect2d> deletedRect = new ArrayList<>();
            for (int i = 0; i < willAddCar.size(); i++) {
                Rect2d r = willAddCar.get(i);
                if (!iot.isInsidePicArea(r.tl()) && !iot.isInsidePicArea(r.br())){
                    deletedRect.add(r);
                }
            }
            willAddCar.removeAll(deletedRect);
        }
        trackers.createNewTrackersByArea(frame,willAddCar);
    }

    public void trackObjectsofFrame(Mat frame){
        //1.预测位置
      trackers.update(frame);//更新位置
      if(iot != null) {
          trackers.deletedNotInArea(iot);//删除不在指定范围内的车辆tracker
      }
    }
    public void detectAndCorrectObjofFrame(Mat frame){

        List<Rect2d> dectedObjects = detector.detectObject(frame);
        correctBounding(frame, dectedObjects);
        cleanLost(30);//清理丢失的目标，消失的帧数  时间 进行清理
        saveDetectorStat();//更新状态为 detector阶段
        return  ;
    }
    public void drawCarsBoundingBoxAndCount(Mat frame){

        ArrayList<Tuple2<Rect2d,Long>> manyCarsInfo = trackers.getPosAndCount();
        for (Tuple carInfos : manyCarsInfo) {
            Optional<Rect2d> carpos = carInfos._1();
            Rect2d pos = carpos.get();
            Imgproc.rectangle(frame, pos.tl(), pos.br(), new Scalar(0,255,0),2);
            Optional<Long> count = carInfos._2();
            Imgproc.putText(frame, "" +count.get(),
                    new Point(pos.tl().x + pos.width / 2, pos.y + 10)
                    , FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0,255,0), 2);//显示标识
        }


    }

    public void drawCarsSpeed(double time,Mat frame){
        ArrayList<Tuple2<Rect2d, Double>> speedandpos = trackers.getSpeed(time, iot);
        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < speedandpos.size(); i++) {
            Optional<Rect2d> carpos = speedandpos.get(i)._1();
            Rect2d pos = carpos.get();
          //  Imgproc.rectangle(frame, pos.tl(), pos.br(), new Scalar(0,0,255),2);
            Optional<Double> count = speedandpos.get(i)._2();//m/s
            String str = df.format(count.get()*3.6);
            Imgproc.putText(frame, str +"km/h",
                    new Point(pos.tl().x + pos.width / 2, pos.y + 10)
                    , FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0,0,255), 2);//显示标识
        }

    }

    public  void  drawTrackerBox(Mat frame,double time) {
        drawCarsBoundingBoxAndCount( frame);
        drawCarsSpeed(time,frame);
    }



    public  void  drawStatistic(Mat frame,double batchFPS) {
        Imgproc.putText(frame, "FPS:" + String.format("%.2f", batchFPS),
                new Point(10, 50), FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(frame,"count:" + trackers.getCurTrackersCount(),
                new Point(10,110), FONT_HERSHEY_SIMPLEX, 1, new Scalar(255,0,0), 2);
    }
    //for test
    public  void  drawBoundigBox(Mat frame ) {
        List<Rect2d> objects = detector.detectObject(frame);
        for (int i = 0; i < objects.size(); i++) {
            Imgproc.rectangle(frame,objects.get(i).tl() , objects.get(i).br(), new Scalar(255, 255, 0),2);
        }
    }



}

