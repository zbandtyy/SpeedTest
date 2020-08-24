package spark;

import java.io.Serializable;

/**
 * @author ：tyy
 * @date ：Created in 2020/6/29 10:57
 * @description：
 * @modified By：
 * @version: $
 */
public class StateTest implements Serializable {
    public String id ;
    public int framcount;
    public int batch;

    public StateTest(String id, int framcount, int batch) {
        this.id = id;
        this.framcount = framcount;
        this.batch = batch;
    }
    public StateTest(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFramcount() {
        return framcount;
    }

    public void setFramcount(int framcount) {
        this.framcount = framcount;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    @Override
    public String toString() {
        return "StateTest{" +
                "id='" + id + '\'' +
                ", framcount=" + framcount +
                ", batch=" + batch +
                '}';
    }
}
