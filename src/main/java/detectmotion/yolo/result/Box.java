package detectmotion.yolo.result;

public class Box {
	public float x;
	public float y;
	public float w;
	public float h;

	public Box() {

	}

	public Box(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public String toString() {
		return "Box{" +
				"x=" + x +
				", y=" + y +
				", w=" + w +
				", h=" + h +
				'}';
	}
}
