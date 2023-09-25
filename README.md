# 远征星河扫描压缩工具



```
|
| - Const  工具包的一些常量枚举
|   |-Const.Suffix  枚举压缩包的类型和后缀   
|   |-Const.SystemEnums 规定了可执行文件的资源路径
| - CustomException 运行时异常
| - Result 运行结果的存放
| - Scan 
|  |- FileScan 扫描文件
|- Zip
|  |- Bean
|  |  |- FileListBean 压缩工具类唯一认可的 文件对象
|  |- SevenZip
|  |  |- Command
|  |  |  |- ZipCommand 压缩包指令集建造者类
|  |  |- PackageRunnable
|  |  |- Zip 有后缀和无后缀的Runnable对象
|  |       
|  |- ZipFourj Zip4j工具类的使用（当命令行过长报错发生时，将会调用此对象的方法进行压缩）
|  |- ZipExecutor 压缩线线程池
|- ZipArgument 压缩参数：扫描路径、压缩路径、是否加密、是否匿名压缩包名、密码、单个文件的大小
```


