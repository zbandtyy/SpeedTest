package spark.config;


import spark.PropertyFileReader;

import java.io.Serializable;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/14 21:45
 * @description：路径等相关配置
 *      FILE 表示要文件名
 *      PATH:表示要路径 路径下应该包含的文件 具体详情见注释
 * @modified By：
 * @version: $
 */
public class AppConfig implements Serializable {
    public static    String  KAFKA_CONFIG_FILE= "/home/user/Apache/App1/exe/stream-processor.properties";
   // public static  final  String  KAFKA_CONFIG_FILE= "E:/spark/speed-test/speedtest/src/main/resources/stream-processor.properties";

    public  static  String OPENCV_LIB_FILE="/home/user/Apache/opencv3.4.7-install/lib/libopencv_java347.so";
    //YOLO的模型文件等的路径 ，目录形式必须如下
    //String cfgPath = Path+"cfg/yolov3.cfg";
    //		String weightsPath =Path+ "yolov3.weights";
    //			String datacfg = Path+"cfg/coco.data";
    //		String labelpath = Path+"data/labels";
    public static     String YOLO_RESOURCE_PATH="/mnt/hgfs/shared/yoloTest/yolo";

    //YOLO的各种物体的名称文件(按行分割)  eg Car,Track
    public  static    String YOLO_LABEL_FILE="/mnt/hgfs/shared/yoloTest/data/coco.names";
    public  static    String YOLO_LIB_FILE="/home/user/Apache/yolo/DetectionAndLicenseRecongnition/Detection/libdetection.so";
    public static     String EASYPR_LABLE_PATH="/home/user/Apache/EasyPR-install/libeasyprjni.so";
    public static     String MYSQL_CONNECT_URL="jdbc:mysql://192.168.0.100:3306/track?user=root&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    public static     String MYSQL_USER_NAME="root";
    public static     String MYSQL_USER_PASSWD="123456";
    public  static    String MYSQL_JDBC_CLASSNAME="com.mysql.cj.jdbc.Driver";
    public  static    String IOTTRANSFORM_JSON_DIR="/home/user/Apache/App1/config/";
    public  static    String CASCADE_DETECTCAR_FILE="E:\\spark\\speed-test\\speedtest\\src\\main\\myhaar.xml";


    static{
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void init() throws Exception {
         if(KAFKA_CONFIG_FILE != null){
             String osName = System.getProperty("os.name");//获取指定键（即os.name）的系统属性,如：Windows 7。
             if (Pattern.matches("Windows.*", osName)) {
                 KAFKA_CONFIG_FILE = "E:/spark/speed-test/speedtest/src/main/resources/stream-processor.properties";
             }

             System.out.println(AppConfig.KAFKA_CONFIG_FILE);
             Properties prop = PropertyFileReader.readPropertyFile();
              if(prop.getProperty("yolo.label.file") != null){

                  YOLO_LABEL_FILE = prop.getProperty("yolo.label.file");
              }
             if(prop.getProperty("yolo.resource.file") != null ){
                 YOLO_RESOURCE_PATH = prop.getProperty("yolo.resource.file");
             }
             if(prop.getProperty("yolo.lib.file") != null ){
                 YOLO_LIB_FILE = prop.getProperty("yolo.lib.file");
             }
             if(prop.getProperty("easypr.label.path") != null ){
                 YOLO_RESOURCE_PATH = prop.getProperty("easypr.label.path");
             }
             if(prop.getProperty("easypr.label.path") != null ){
                 EASYPR_LABLE_PATH = prop.getProperty("easypr.label.path");
             }
             if(prop.getProperty("iottransform.json.dir") != null ){
                 IOTTRANSFORM_JSON_DIR = prop.getProperty("iottransform.json.dir");
             }
             if(prop.getProperty("cascade.detectcar.file") != null ){
                 CASCADE_DETECTCAR_FILE = prop.getProperty("cascade.detectcar.file");
             }

         }


    }

    public static void main(String[] args) {
        System.out.println(AppConfig.KAFKA_CONFIG_FILE);


        System.out.println(AppConfig.YOLO_RESOURCE_PATH);
        System.out.println(AppConfig.YOLO_LABEL_FILE);
        System.out.println(AppConfig.IOTTRANSFORM_JSON_DIR);
    }

}
