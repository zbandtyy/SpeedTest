

package detectmotion.interestarea;

import detectmotion.CarDes;
import detectmotion.tuple.Tuple3;
import detectmotion.utils.PHASE;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;

import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
public class SpeedCalculator {

    PerspectiveConversion ioa = null;//计算的区域
    List<CarDes> cars ;//计算的数据

    public SpeedCalculator(PerspectiveConversion ioa, List<CarDes> cars) {
        this.ioa = ioa;
        this.cars = cars;
    }

    public  ArrayList<Tuple3<Rect2d,Double,Double>>   calulateSpeed(double time){
        ArrayList<Tuple3<Rect2d,Double,Double>> arr = new ArrayList<>(cars.size());
        for (int i = 0; i < cars.size(); i++) {
            CarDes car = cars.get(i);
            //上一次检测的第10帧位置
            Rect2d pre = car.getPreviousPos();
            //当前的位置
            Rect2d p = car.getNetxPos();
            if(pre == null || p == null)
                continue;
            if(car.getPhase() == PHASE.TRACKER && car.getSpeed() != 0){//如果在跟踪阶段&&已经产生速度 就进行显示，添加
                arr.add(new Tuple3<>(car.getPos(),car.getSpeed(),car.getCarLength()));
                continue;
            }
            if(car.getPhase() == PHASE.DETECTOR ) {
                //计算速度/////
                // 不在标定范围内不对速度进行更新,指定的范围是图片上接受处理的范围
                if (car.getSpeed() != 0 &&(!ioa.isInsidePicArea(getRectCenter(pre))
                        || !ioa.isInsidePicArea(getRectCenter(p)))) {
                    arr.add(new Tuple3<>(car.getPos(), car.getSpeed(),car.getCarLength()));
                }//如果上一次与这一次的坐标位置都在指定的区间内，真实的速度更新
                else if (ioa.isInsidePicArea(getRectCenter(pre))
                        && ioa.isInsidePicArea(getRectCenter(p))){
                    double s = ioa.calculateSpeed(p,pre,time);//计算的速度值
                    double carLength =  ioa.getDistance(p.tl(),p.br());//计算车辆的长度
                    // System.out.println(carLength);
                    car.setCarLength(carLength);
                    car.setSpeed(s);
                    if (car.getSpeed() == 0) {
                        car.setSpeed(s);
                    } else {
                        car.setSpeed((car.getSpeed()+  s) / 2);
                    }
                    arr.add(new Tuple3<>(car.getPos(), car.getSpeed(),car.getCarLength()));
                }
            }
        }
        return  arr;

    }

    private Point getRectCenter(Rect2d r){
        return  new Point(r.x + r.width/2,r.y + r.height /2);
    }




}
