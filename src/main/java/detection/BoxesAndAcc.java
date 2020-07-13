package detection;

import org.opencv.core.Point;
import org.opencv.core.Rect;

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
    public Rect transfor( int w, int h ){

        BoxesAndAcc tmp = this;
        Box box = tmp.getBoxes();
        float left = (box.getX() - box.getW()/2) * w;//w*（box.x - box.w/2）
        float top =  (box.getY()  - box.getH()/2)*h;
        float bot   = (box.getY() + box.getH()/2)*h;
        float right = (box.getX() + box.getH()/2)*w;
        return  new Rect(new Point(left,top),new Point(right,bot));

    }
}
