package spark.config;

import java.io.Serializable;

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

    public static  final  String  KAFKA_CONFIG_FILE= "./stream-processor.properties";
    public  static  final  String OPENCV_LIB_FILE="/home/user/Apache/opencv3.4.7-install/lib/libopencv_java347.so";

    //YOLO的模型文件等的路径 ，目录形式必须如下
    //String cfgPath = Path+"cfg/yolov3.cfg";
    //		String weightsPath =Path+ "yolov3.weights";
    //			String datacfg = Path+"cfg/coco.data";
    //		String labelpath = Path+"data/labels";
    public  static  final  String YOLO_RESOURCE_PATH="/mnt/hgfs/shared/yoloTest/yolo";

    //YOLO的各种物体的名称文件(按行分割)  eg Car,Track
    public  static  final  String YOLO_LABEL_FILE="/mnt/hgfs/shared/yoloTest/data/coco.names";
    public  static  final  String EASYPR_LABLE_PATH="/home/user/Apache/EasyPR-install/libeasyprjni.so";
    public  static  final  String MYSQL_CONNECT_URL="jdbc:mysql://192.168.0.100:3306/track?user=root&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    public  static  final  String MYSQL_USER_NAME="root";
    public  static  final  String MYSQL_USER_PASSWD="123456";
    public  static  final  String MYSQL_JDBC_CLASSNAME="com.mysql.cj.jdbc.Driver";
    public  static  final  String IOTTRANSFORM_JSON_FILE="multitracker/calibrate_camera.json";


}
