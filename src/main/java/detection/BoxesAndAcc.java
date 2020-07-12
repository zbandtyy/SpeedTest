package detection;

public class BoxesAndAcc{
    float acc;
    Box boxes;
    String names;
    boolean isVaild;
    int size;

    public float getAcc() {
        return acc;
    }

    public void setAcc(float acc) {
        this.acc = acc;
    }

    public Box getBoxes() {
        return boxes;
    }

    public void setBoxes(Box boxes) {
        this.boxes = boxes;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public boolean isVaild() {
        return isVaild;
    }

    public void setVaild(boolean vaild) {
        isVaild = vaild;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
