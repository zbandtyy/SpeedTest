package detectmotion.utils;

import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.apache.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import sun.awt.SunHints;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.merge;
import static org.opencv.core.CvType.*;

/**
 * @author ：tyy
 * @date ：Created in 2020/5/31 6:43
 * @description：各种类型转换成mat类型
 * @modified By：
 * @version: $
 */

public class MatWrapper {
    private static final Logger logger = Logger.getLogger(MatWrapper.class);
    public  static  Mat doubleArrayToMat(List<? extends List<?>> mtxlist,int type){
        logger.debug("doubleArray tranform to  Float Channel = 1 MAT CV_32F");
        int getRows = mtxlist.size();
        if(getRows <= 0)
            return  null;
        int cols = mtxlist.get(0).size();
        Object value = mtxlist.get(0).get(0);
        Mat mat ;
        if( type == CV_64F) {
            mat = new Mat(getRows, cols, CvType.CV_64F);
        }else if(type == CV_32S){
            mat = new Mat(getRows, cols, CV_32S);
        }else{
            mat = new Mat(getRows, cols, CV_32F);
        }

        for(int i = 0; i < mtxlist.size() ; i++){
            List<?> row = mtxlist.get(i);
            for (int j = 0; j < row.size(); j++) {

                if( type == CV_64F) {
                    Double o = (Double) row.get(j);
                    mat.put(i, j, o);
                }else if(type == CV_32S){
                    Integer o = (Integer) row.get(j);
                    mat.put(i, j, o);
                }else if(type == CV_32F){
                    Float o =  (Float)row.get(j);
                    mat.put(i, j, o);
                }
            }
        }
        return mat;
    }

    public static Mat SingleTo2Channels( Mat binImg)
    {
        Mat two_channel = Mat.zeros(binImg.rows(),binImg.cols(),CV_32FC2);
        for(int i = 0; i < binImg.rows() ; i++){
            for(int k =0; k < binImg.channels() ;k++) {
                double[] array = new double[]{binImg.get(i,0)[0],binImg.get(i,1)[0]};
                two_channel.put(0,i,array) ;
            }

        }
        return two_channel;
    }
    public  static String MatToString(Mat s){

        StringBuilder str = new StringBuilder();
        str.append("\n[");
        for(int i = 0; i < s.rows() ; i++){
            str.append("[");
            for(int j =0 ; j < s.cols(); j++){
                str.append("[");
                for(int k =0; k < s.channels() ;k++) {
                    String s1 = String.valueOf(s.get(i,j)[k]);
                    str.append(s1 + " ");
                }
                str.append("]\n");
            }
            str.append("]\n");
        }

        return  str.toString();
    }
}
