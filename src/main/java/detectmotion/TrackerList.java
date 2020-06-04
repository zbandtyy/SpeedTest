package detectmotion;
import detectmotion.tuple.Tuple;
import detectmotion.tuple.Tuple3;
import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.*;
import  java.util.*;
/**
 * @author ：tyy
 * @date ：Created in 2020/5/1 16:24
 * @description： 创建一个保存已经识别的车辆信息的List，提供基本的增删改查，即涉及单个结点的管理维护，该类会修改TrackerList的值
 * @modified By：
 * @version: $
 */
public class TrackerList  {
    private static final Logger logger = Logger.getLogger(IOTTransform.class);
    private String[] trackerTypes = {"BOOSTING", "MIL", "KCF", "TLD", "MEDIANFLOW", "GOTURN", "MOSSE", "CSRT"};
    IOTTransform iot ;
    Tracker createTrackerByName(String trackerType) {
        Tracker tracker = null;
        if (trackerType == trackerTypes[0])
            tracker = TrackerBoosting.create();
        else if (trackerType == trackerTypes[1])
            tracker = TrackerMIL.create();
        else if (trackerType == trackerTypes[2])
            tracker = TrackerKCF.create();
        else if (trackerType == trackerTypes[3])
            tracker = TrackerTLD.create();
        else if (trackerType == trackerTypes[6])
            tracker = TrackerMOSSE.create();
        else {
            System.out.println("Incorrect tracker name");
            System.out.println("Available trackers are: ");
            Integer str;
            for (int i = 0; i < trackerType.length(); i++) {
                System.out.print(trackerTypes[i] + " ");
            }
        }
        return tracker;
    }//可以灵活应对新增加或者修改的tracker (Factory,反摄）E
    String selectedType = "MOSSE";
  //对车辆进行维护，在增加一个tracker就相当于增加一辆车，与trackers完全同步
    private  List<CarDes> trackers = new LinkedList<>();

    public long getCurTrackersCount() {
        return startCount;
    }

    long startCount = 0;//计数

    /***
     *
     * @param frame 更新的帧的信息
     * @param cars 要更新的位置
     * NOTE：会删除内部的所有tracker，所以调用的时候需要全部检查完成
     */
    public void updateTrackPos (Mat frame,  Map<Integer,Rect2d> cars ){
        if(cars == null || cars.size() <= 0){
            return;
        }
        ArrayList<CarDes> needModify = new ArrayList<>();
        logger.debug("tracker size ===" + trackers.size());
        cars.forEach((index,pos)->{


            CarDes car= trackers.get(index);
            logger.debug("原来预测到的位置" + car.pos);
            logger.debug("现在检测到的位置" + pos);
            car.pos.x = (car.pos.x + pos.x) /2;
            car.pos.y = (car.pos.y + pos.y) /2;
            car.pos.width = pos.width;
            car.pos.height = pos.height;
            needModify.add(car);
        } );
        updateTrackPos(frame,needModify);

    }
    //原来的车辆，更新某车辆内部的信息，例如位置等
    public void updateTrackPos (Mat frame,  List<CarDes> cars ){
        for(CarDes car: cars){
            Tracker t = createTrackerByName(selectedType);
            logger.debug("count = " + car.count);
            t.init(frame,car.pos);
            car.setTracker(t);
            car.setPreviousPos(car.getNetxPos());
            car.setNetxPos(car.pos);
        }
    }
    //发现新的车辆
    public void createNewTrackersByArea(Mat frame,  List<Rect2d> cars ){
        for(Rect2d carpos: cars){
            Tracker t = createTrackerByName(selectedType);
            t.init(frame,carpos);
            CarDes c = new CarDes(carpos,startCount++,t);// 与上面的区别 是否创建新的车辆
            c.setPreviousPos(c.getNetxPos());
            c.setNetxPos(c.pos);
            trackers.add(c);//对count进行设置
        }
    }

    //iotArea表示感兴趣的区域，如果更新之后不再感兴趣的区域内就删掉
    public void update(Mat frame){
        int length = trackers.size();
        List<CarDes> deleted = new LinkedList<>();
        for(int i = 0 ; i < length; i ++ ){
            //保存更新结果
            Rect2d newPos = new Rect2d();
            //目标没有丢失
            Tracker  t = trackers.get(i).getTracker();
            boolean res = t.update(frame,newPos) ;
            if(res != true ){
               deleted.add(trackers.get(i));
           }else {
               trackers.get(i).pos = newPos;
           }
        }
        trackers.removeAll(deleted);
        deleted.clear();
        //删除所有超过范围的追踪器
        if(this.iot != null){
            MatOfPoint2f area = new MatOfPoint2f(iot.getIntrestAreaInPic());
            for (int i = 0; i < trackers.size(); i++) {
                Rect2d pos = trackers.get(i).pos;
                Point p = new Point (pos.x + 0.5 * (pos.width),pos.y + 0.5 * (pos.height));
                double res = Imgproc.pointPolygonTest(area,p,false);
                if(res < 0){
                    deleted.add(trackers.get(i));
                }

            }

        }
        trackers.removeAll(deleted);
        return  ;

    }

    public  TrackerList setTrackerType(String selectedType) {
        this.selectedType = selectedType;
        return  this;
    }
    public TrackerList setIOT(IOTTransform ioa){
        this.iot  = ioa;
        return  this;
    }
    public  TrackerList(){

    }
    //获取列表进行操作

    public  ArrayList<Rect2d> getTrackedCarsPos(){
        ArrayList< Rect2d> poses = new ArrayList<>();
        for (CarDes tracker : trackers) {

            poses.add(tracker.getPos());
        }
        return poses;

    }
    public  ArrayList<Tuple3<Rect2d,Long,Double>> getTrackedCarsInfos(String ... info){
        int s = info.length;
        ArrayList<Tuple3<Rect2d,Long,Double>> arr = new ArrayList<>(trackers.size());
        for (int i = 0; i < trackers.size(); i++) {
            CarDes car = trackers.get(i);
            arr.add(new Tuple3<Rect2d,Long,Double>(car.getPos(),car.getCount(),car.getSpeed()));
        }
        return  arr;
    }
}
