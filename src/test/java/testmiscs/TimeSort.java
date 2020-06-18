package testmiscs;

import spark.type.VideoEventData;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author ：tyy
 * @date ：Created in 2020/6/12 21:03
 * @description：
 * @modified By：
 * @version: $
 */
public class TimeSort {

    public static void main(String[] args) {
        Timestamp t1 = new Timestamp(new Date().getTime());
        Timestamp t2 = new Timestamp(new Date().getTime() + 10);
        VideoEventData data = new VideoEventData("12",t1,2,2,1,"hello");
        VideoEventData data2 = new VideoEventData("13",t2,2,2,1,"hello");
        ArrayList<VideoEventData> sortedList = new ArrayList<VideoEventData>();
        sortedList.add(data);
        sortedList.add(data2);



        sortedList.sort((d1,d2)-> (int) (d1.getTimestamp() - d2.getTimestamp()));
        System.out.println(sortedList);
    }
}
