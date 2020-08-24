package spark.type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.status.api.v1.SimpleDateParam;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imdecode;

/**
 * Java Bean to hold JSON message
 * 输入数据 和 车辆跟踪的结果数据
 * @author abaghel
 *
 */
public class VideoEventData implements Serializable{
	@Setter @Getter
	private String cameraId;

	public VideoEventData(String cameraId, Timestamp timestamp, int rows, int cols, int type, String data) {
		this.cameraId = cameraId;
		this.timestamp = timestamp;
		this.rows = rows;
		this.cols = cols;
		this.type = type;
		this.data = data;
	}
	public VideoEventData(){}
	@Getter @Setter
	private Timestamp timestamp;
	@Setter @Getter
	private int rows;
	@Setter @Getter
	private int cols;
	@Setter @Getter
	private int type;
	@Setter @Getter
	private String data;
	@Getter @Setter
	private String generateFrameTime = null;//帧再原视频的时间（播放的时间）
	@Getter @Setter
	private byte[] jpgImageBytes = null;
	public long getGenerateTime(){

		DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
		LocalDateTime parse = LocalDateTime.parse(generateFrameTime, dateTimeFormatter1);
		return parse.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


	}

	/**
	 *
	 * @return  返回jpg的图片
	 */
	public byte[] getImagebytes() {
		if(this.getData() == null){
			return  jpgImageBytes;
		}
		//部分数据传送的原始的像素数组
		byte[] pic = Base64.getDecoder().decode(this.getData());
		//	System.out.println(pic.length);
		if(pic.length >= this.getRows()*this.getCols() * 3) {

			Mat frame = new Mat(this.getRows(), this.getCols(), this.getType());
			frame.put(0, 0, pic);
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".jpg", frame, mob);
			return mob.toArray();
		}else {
			//有的数据传送的是经过jpg压缩的数据

			return pic;
		}
	}
	public  Mat getMat( )  {
		byte[] imagebytes = this.getImagebytes();
		MatOfByte matOfByte = new MatOfByte(imagebytes);
		Mat imdecode = imdecode(matOfByte, CV_LOAD_IMAGE_COLOR);
		return imdecode;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestamp(String timestamp) {
		System.out.println("=================");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		Date parse = null;
		try {
			 parse = format.parse(String.valueOf(format));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.timestamp = new Timestamp(parse.getTime());
	}



	public  long getTime(){
		return timestamp.getTime();
	}

	public void setFrame(byte[]  frameBytes) {
		this.data = Base64.getEncoder().encodeToString(frameBytes);
	}

	public String toJson(){

		Gson gson = new Gson();
		/**
		 * String toJson(Object src)
		 * 将对象转为 json，如 基本数据、POJO 对象、以及 Map、List 等
		 * 注意：如果 POJO 对象某个属性的值为 null，则 toJson(Object src) 默认不会对它进行转化
		 * 结果字符串中不会出现此属性
		 */
		String json = gson.toJson(this);
		return  json;
	}
	public VideoEventData fromJson(String data){
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		Gson gson = builder.create();
		/**
		 *  <T> T fromJson(String json, Class<T> classOfT)
		 *  json：被解析的 json 字符串
		 *  classOfT：解析结果的类型，可以是基本类型，也可以是 POJO 对象类型，gson 会自动转换
		 */
		VideoEventData p = gson.fromJson(data, VideoEventData.class);
		return p;
	}

	public static void main(String[] args) {
		VideoEventData data = new VideoEventData("vid",new Timestamp(12345),3,4,3,"data");
		String s = data.toJson();
		System.out.println(s);
		VideoEventData d = data.fromJson(s);
		System.out.println(d);
	}

	@Override
	public String toString() {
		return "VideoEventData{" +
				"cameraId='" + cameraId + '\'' +
				", timestamp=" + timestamp +
				", rows=" + rows +
				", cols=" + cols +
				", type=" + type +
				", data== null" + Boolean.toString(data == null )+ '\'' +
				",generateFrameTime=" +(generateFrameTime) +
				'}';
	}
}


