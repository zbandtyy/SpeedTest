package spark;

import detectmotion.SequenceOfFramesProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.FlatMapGroupsWithStateFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.GroupStateTimeout;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.opencv.core.Size;
import scala.Tuple2;
import spark.config.AppConfig;
import spark.config.SpeedState;
import spark.type.VideoEventData;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

@Slf4j
public class ReadPhoto {

    public static void main(String[] args) throws Exception {

        //Read properties
        Properties prop = PropertyFileReader.readPropertyFile();

        //SparkSesion 配置spark环境  Spark SQL 的入口。
        //使用 Dataset 或者 Datafram 编写 Spark SQL 应用的时候，第一个要创建的对象就是 SparkSession。
        SparkSession spark = SparkSession

                .builder()//使用builer创建sparkSession的实例
                .appName("app1")
                .master(prop.getProperty("spark.master.url"))//设置主要的spark 环境  spark://mynode1:7077
                .getOrCreate();	//获取或者创建一个新的sparksession
        System.out.println("=============================");
        //directory to save image files with motion detected 有什么用？
        final String processedImageDir = prop.getProperty("processed.output.dir");//  /home/user/Apache/project
        log.warn("Output directory for saving processed images is set to "+processedImageDir+". This is configured in processed.output.dir key of property file.");

        //create schema for json message 配置能够取得的数据格式
        StructType schema =  DataTypes.createStructType(new StructField[] {
                DataTypes.createStructField("cameraId", DataTypes.StringType, false),
                DataTypes.createStructField("timestamp", DataTypes.TimestampType, false),
                DataTypes.createStructField("generateFrameTime", DataTypes.StringType, false),
                DataTypes.createStructField("rows", DataTypes.IntegerType, false),
                DataTypes.createStructField("cols", DataTypes.IntegerType, false),
                DataTypes.createStructField("type", DataTypes.IntegerType, false),
                DataTypes.createStructField("data", DataTypes.StringType, false),
                DataTypes.createStructField("jpgImageBytes", DataTypes.BinaryType, true),//当使用的是VideoEvent数据对象而不是json对象进行的发送数据
        });
        log.warn(prop.getProperty("kafka.bootstrap.servers"));
        //Create DataSet from stream messages from kafka  配置kafka的数据格式
        //// Subscribe to 1 topic defaults to the earliest and latest offsets

        //consumer.assign(Arrays.asList(p));
        Dataset<Row> ds1 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", prop.getProperty("kafka.bootstrap.servers"))//创建并且订阅了几个kafka主题
                .option("subscribe", prop.getProperty("kafka.topic"))
               // .option("assign","{\"app1-input\":[0]}")
                .option("failOnDataLoss",false)
               // .option("startingOffsets", "{\"video-kafka-large\":{\"1\":100,\"0\":100}}")//必须指定全部
                .option("kafka.max.partition.fetch.bytes", prop.getProperty("kafka.max.partition.fetch.bytes"))
                .option("kafka.max.poll.records", prop.getProperty("kafka.max.poll.records"))
                .option("maxOffsetsPerTrigger","40")//开了最多的200个Task处理全部的历史数据，groupby的时候shuffle存储空间不够，应该限制接受的一批 数据大小
                .option("startingOffsets", "earliest")
                //.option("endingOffsets", "{\"video-kafka-large\":{\"0\":50,\"1\":-1}")
                .load();

        Dataset<VideoEventData> ds= ds1.selectExpr("CAST(value AS STRING) as message")//读取数据
                .select(functions.from_json(functions.col("message"),schema).as("json"))//选择数据的格式，在后面的query中使用
                .select("json.*")
                .as(Encoders.bean(VideoEventData.class));//将所有数据转换为 Dataset<VideoEventData> 不分开产生问题》怀疑历史数据被他一批读走了，限制
        log.warn("topic" +  prop.getProperty("kafka.topic"));


       // key-value pair of cameraId-VideoEventData 窗口进行分组的时候是针对聚合实践进行处理
        KeyValueGroupedDataset<String, VideoEventData> kvDataset = ds.groupByKey(new MapFunction<VideoEventData, String>() {
            @Override
            public String call(VideoEventData value) throws Exception {
                return value.getCameraId();
            }
        }, Encoders.STRING());


        ///////////////// //1. YOLO识别测试///////////////////////////////////////////
//        对每组数据应用给定的函数，同时维护用户定义的每组状态。结果数据集将表示函数返回的对象。
//         对于静态批处理数据集,每个组调用该函数一次。
        Dataset<VideoEventData> dfTrack = kvDataset.flatMapGroupsWithState(
                (FlatMapGroupsWithStateFunction<String, VideoEventData, SpeedState,VideoEventData >)
                        (key, values, state) -> {
                    log.info("====================start  batch========================");
                    SequenceOfFramesProcessor opencv = null;
                    //1.加载数据
                    ArrayList<VideoEventData> sortedList = new ArrayList<VideoEventData>();
                    Size picsize = null;
                    while (values.hasNext()){
                        VideoEventData tmp = values.next();
                        picsize = new Size(tmp.getCols(),tmp.getRows());
                        sortedList.add(tmp);
                    }
                    sortedList.sort((d1,d2)-> (int) (d1.getTime() - d2.getTime()));
                    log.warn("cameraId=" + key + "processed total frames=" + sortedList.size() +"actual size" );
                    //2.进行速度处理
                    SpeedState s= null;
                    if(!state.exists()){
                        log.warn("new processor" + key);
                        String iotTransformFileName = new StringBuilder()
                                .append(AppConfig.IOTTRANSFORM_JSON_DIR )
                                .append("calibrate_camera_scale_")
                                .append(key)
                                .append(".json").toString();
                        log.info("iottransform read file name = " + iotTransformFileName);

                        File file = new File(iotTransformFileName);
                        if(!file.exists()){
                            opencv = new SequenceOfFramesProcessor(10,picsize, new Size(8,30));
                        }else {
                            opencv = new SequenceOfFramesProcessor(10,iotTransformFileName);
                        }
                    }else {
                        log.info("use raw state");
                        s = state.get();
                        log.info("get state\n" + s );
                        opencv = new SequenceOfFramesProcessor(s);
                        log.info("get opencv\n" + opencv );
                    }
                    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                    log.info(key+"PID: " + runtimeMXBean.getName());
                    opencv.processFrames(sortedList);//处理后的结果存储在sortedList'中
                    SpeedState after =  new SpeedState(opencv);
                    state.update(after);
                    log.info("after opencv\n" + after );
                    log.info("after opencv\n" + opencv );
                    log.info("====================end a batch ===================");
                    return sortedList.iterator();
        }, OutputMode.Append(),  Encoders.bean(SpeedState.class),
                Encoders.bean(VideoEventData.class) , GroupStateTimeout.NoTimeout());// OutputMode.Update(),Encoders.bean(String.class),, GroupStateTimeout.NoTimeout());



        Dataset<Row> djson = dfTrack.flatMap(new FlatMapFunction<VideoEventData, Tuple2<String, String>>() {
            @Override
            public Iterator<Tuple2<String, String>> call(VideoEventData videoEventData) throws Exception {
                ArrayList<Tuple2<String, String>> pro = new ArrayList<>();
                pro.add(new Tuple2<>(videoEventData.getCameraId(), videoEventData.toJson()));
                return pro.iterator();
            }

        }, Encoders.tuple(Encoders.STRING(), Encoders.STRING())).toDF("key", "value");


        StreamingQuery query = djson
                .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)") //如果Kafka数据记录中的字符存的是UTF8字符串，我们可以用cast将二进制转换成正确类型
                .writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",prop.getProperty("kafka.bootstrap.servers"))
                .option("topic", prop.getProperty("kafka.result.topic"))
                .option("kafka.max.request.size", prop.getProperty("kafka.result.max.request.size"))
                .option("kafka.batch.size",prop.getProperty("kafka.result.batch.size"))
                .option("kafka.compression.type","gzip")
                .option("checkpointLocation", prop.getProperty("checkpoint.dir"))
                .start();//启动实时流计
        query.awaitTermination();

    }
}
