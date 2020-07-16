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
### 1.关于head of empty list
1.Lost task 0.2 in stage 1.0 (TID 16) on 192.168.0.110, executor 0:
java.util.NoSuchElementExc eption (head of empty list)
出问题的地方：在mapGroupWithState的时候加载失败  
解决方案：检查encoder是否能正确加载，即对所有类增加对应的setter和getter，对内部引用的内添加无参构造
原因：？？？？
### 2.关于资源文件无法从内部读取的问题
出问题的地方：在读内部包的资的时，无法读出具的内容，读取得内容为空  
原因：？？？  
解决方法：使用外部路径读取
## 四、需要 解决的 问题
### 1关于OpencvMultiTracker的创建
1.错误问题：
MultiTracker的创建使用的是static，那么所有类共享该对象，那么处理多分区时可能出现BUG，如果多
分组不同的使用，需要进行改善。（保证内容仅加载一次，并且每个分组拥有独立的对象，有且只有一个）  
2.可能的解决方案：  
（1）创建多例模式，以避免多次加载模型  
（2）需要把对应的内部参数接口暴露出来给外部函数进行状态的保存，创建另一份代码  
3.需要注意
直接拿到该对象进行状态保存是不行的，每次恢复的位置是不同的！！！！！！！！！！已测试n次，怀疑是每次都创建了新的对象，也可能是原来的对象销毁了，这里需要测试。

* iot中的mtx 和 inversemtx 为地址【如果不合适，可以自己重新设置新的】
  * detector的是地址【如果不合适，可以自己重新设置新的】
  * trackerlist中的trcker是地址 【如果不合适，可以自己重新设置新的】，
  * 目前存储的值是原来的地址，那么问题来了，在多个分组的时候，会改变吗？？？？
    2020-07-15 03:07:20 WARN TaskSetManager:66 - Lost task 120.0 in
    stage 3.0 (TID 322, 115.157.201.215, executor 0): CvException [org


