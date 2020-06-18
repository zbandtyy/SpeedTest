package testtracker;

/**
 * @author ：tyy
 * @date ：Created in 2020/4/27 16:46
 * @description：
 * @modified By：
 * @version: $
 */
/*
 * Copyright 2014 Luke Quinane
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * An OpenCV based car counter.
 */
public class OpencvCarCounter
{
    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    Mat image;
    Mat foregroundMask;
    Mat maskedImage;
    BackgroundSubtractorMOG2 backgroundSubtractor;

    public OpencvCarCounter()
    {
        image = new Mat();
        foregroundMask = new Mat();
        maskedImage = new Mat();
        backgroundSubtractor = Video.createBackgroundSubtractorMOG2();
    }


    public void processVideo(String video)
    {
       String carclassifierpath  = Thread.currentThread().getContextClassLoader().getResource("cars.xml").getPath();
       System.out.println(carclassifierpath);
       //传统目标检测算法之级联分类器Cascade:滑动窗口机制+级联分类器的方式；
        CascadeClassifier carDetector = new CascadeClassifier("E:\\spark\\speed-test\\speedtest\\src\\main\\resources\\cars.xml");

        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open(video);

        int index = 0;

        while (true)
        {
            if (!videoCapture.read(image))
            {
                break;
            }

            System.out.print(".");

         //   processFrame();

            MatOfRect carDetections = new MatOfRect();
            carDetector.detectMultiScale(image, carDetections);//image表示的是要检测的输入图像 carDetection表示检测到的人脸目标序列
            System.out.println(String.format("Detected %s cars", carDetections.toArray().length));

            // Draw a bounding box around each hit
            for (Rect rect : carDetections.toArray())
            {
                Imgproc.rectangle(image,new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
            }

            imshow("FG Mask", image);

            // get the input from the keyboard
            int keyboard = HighGui.waitKey(30);
            if (keyboard == 'q' || keyboard == 27) {
                break;
            }

        }

        HighGui.waitKey();
        System.exit(0);

    }

    protected void processFrame()
    {
        backgroundSubtractor.apply(image, foregroundMask, 0.1);

        //Imgproc.cvtColor(foregroundMask, foregroundMask, Imgproc.COLOR_, 4);
        //Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2GRAY, 4);
        //Imgproc.cvtColor(maskedImage, maskedImage, Imgproc.COLOR_RGBA2GRAY, 4);

        Core.bitwise_and(image, image, maskedImage, foregroundMask);
    }

    public static void main(String[] args) {
        String path = Thread.currentThread().getContextClassLoader().getResource("yol.mp4").getPath();//获取资源路径
        System.out.println(path);
        OpencvCarCounter s  = new OpencvCarCounter();
        s.processVideo(path);
    }
}
