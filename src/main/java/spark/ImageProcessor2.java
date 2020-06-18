package spark;


import detectmotion.OpencvMultiTracker;
import detectmotion.SequenceOfFramesProcessor;
import org.apache.log4j.Logger;


import scala.App;
import spark.config.AppConfig;
import spark.type.VideoEventData;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.opencv.imgproc.Imgproc.resize;


/**
 * Class to extract frames from video using OpenCV library and process using TensorFlow.
 *
 * @author abaghel
 */
//在spark中处理所有的对象都应该是序列化的 也就是写在 spark内部函数的所有对象 最好经过序列化
public class  ImageProcessor2 implements Serializable {
    private static final Logger logger = Logger.getLogger(ImageProcessor2.class);

    //load native lib
    /***
     * 处理的是同一摄像头的帧
     * @param camId  Key
     * @param frames Value
     * @return 返回封装的数据
     * @throws Exception
     */
    //
    public static ArrayList<VideoEventData> process(String camId, Iterator<VideoEventData> frames) throws Exception {
        //Add frames to list
        //2.一批数据处理的图片
        ArrayList<VideoEventData> sortedList = new ArrayList<VideoEventData>();
        sortedList.sort((d1,d2)-> (int) (d1.getTimestamp() - d2.getTimestamp()));
        logger.warn("cameraId=" + camId + "processed total frames=" + sortedList.size() +"actual size" );
        String jsonPath =  Thread.currentThread().getContextClassLoader().getResource(AppConfig.IOTTRANSFORM_JSON_FILE).getPath();//获
        //3.处理视频，理想的情况是每一个ID对应一个 该对象，暂不知如何实现？？根据分组来创建对象，该分组已经存在，那么不须创建
        //必须要清楚process的调用过程,6-12
        SequenceOfFramesProcessor opencv = new SequenceOfFramesProcessor(10,jsonPath);
        opencv.processFrames(sortedList);
        logger.warn("a batch recognize success");
        return sortedList;
    }





}

