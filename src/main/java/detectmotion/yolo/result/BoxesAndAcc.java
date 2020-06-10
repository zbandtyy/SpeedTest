package detectmotion.yolo.result;

public class BoxesAndAcc{
	public float acc;
	public Box boxes;
	public String names;
	public boolean isVaild;
	public int size;
	public  BoxesAndAcc(){

	}
	public BoxesAndAcc(float acc, Box boxes, String names, boolean isVaild) {
		this.acc = acc;
		this.boxes = boxes;
		this.names = names;
		this.isVaild = isVaild;
	}

	@Override
	public String toString() {
		return "BoxesAndAcc{" +
				"acc=" + acc +
				", boxes=" + boxes +
				", names='" + names + '\'' +
				", isVaild=" + isVaild +
				'}';
	}
}
