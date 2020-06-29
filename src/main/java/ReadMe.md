## 一、目前所需环境
    opencv 3.4.7或者以上  + contrib
## 二、文件目录
    CarDes：车辆描述文件，对每一辆车的信息（跟踪编号、速度、检测到的位置）进行保存
    DetectCar:检测车辆的具体实现，主要函数为 【识别车辆并且返回车辆边框】
    TrackerList： 车辆跟踪的具体实现，包括跟踪算法的选择，对多组车辆信息的保存
    OpenCVMultiTracker：程序实现主要具体流程
    RectComput：辅助类，计算矩形相交的面积等
## 三、出错日常
    Encoder的作用是负责JVM类类型和Spark SQL内部表示之间的相互转换。
    通常大部分的基础类型都通过SparkSession的implicit import声明了，
    而自定义的类型需要特别指定对应的Encoder，而这也并不困难，通过kryo我们可以方便地创建出encoder来解决这个错误
  ### 1.
  1.Lost task 0.2 in stage 1.0 (TID 16) on 192.168.0.110, executor 0:
  java.util.NoSuchElementExc eption (head of empty list)
  出问题的地方：在mapGroupWithState的时候加载失败  
  解决方案：检查encoder是否能正确加载，即对所有类增加对应的setter和getter，对内部引用的内添加无参构造
  原因：？？？？
  ### 2.关于资源文件无法从内部读取的问题
  出问题的地方：在读内部包的资的时，无法读出具的内容，读取得内容为空  
  原因：？？？  
  解决方法：使用外部路径读取
