## 一、目前所需环境
    opencv 3.4.7或者以上  + contrib
## 二、文件目录
    CarDes：车辆描述文件，对每一辆车的信息（跟踪编号、速度、检测到的位置）进行保存
    DetectCar:检测车辆的具体实现，主要函数为 【识别车辆并且返回车辆边框】
    TrackerList： 车辆跟踪的具体实现，包括跟踪算法的选择，对多组车辆信息的保存
    OpenCVMultiTracker：程序实现主要具体流程
    RectComput：辅助类，计算矩形相交的面积等


