<body>

<div style="display: grid">
    <h1 style="text-align: center">凝聚星河</h1>
    <h3 style="text-align: center">基于7-Zip和zip4J开发的压缩工具类</h3>
</div>
</body>

## 简介
凝聚星河，是由远征星河团队带来的：拥有7zip命令建造、多种压缩方法并加密、扫描路径下文件等能力的工具类。

## 分支介绍
- v.-master 最稳定分支。该分支上的代码是凝聚星河的最新版本
- v.-staging 激进分支。该分支上的代码凝聚星河的预览版。所有新功能将会在这个分支进行修改、审查、测试。确保没有问题后将会推上master分支并发布到maven
- v.-develop 开发分支。日常开发代码的分支

## 如何贡献
- 开一个Issues，说明你遇到的问题或想要做出来的想法。
- fork代码到你的仓库。新建一个由issues号命名的分支，例如（fix#1)。经过你的测试，所有功能没有问题之后，提交改代*码到dev分支

## 源代码目录展示
```
| - src
|   | - main
|       |-java
|         | - cn.deystar             
|            | - Const  工具包的一些常量枚举
|               |   |-Const.Suffix  枚举压缩包的类型和后缀   
|               |   |-Const.SystemEnums 规定了可执行文件的资源路径
|               | - CustomException 运行时异常
|               | - Scan. 
|               |  |- FileScan 扫描文件
|               |- Compress.
|               |  |- Bean.  扫描获得的文件对象
|               |  |  |- FileListBean 
|               |  |- SevenZip  7Zip相关
|               |  |  |- Command
|               |  |  |  |- ZipCommand 压缩包指令集建造者类
|               |  |  |- PackageRunnable
|               |  |  |- Zip 有后缀和无后缀的Runnable对象
|               |  |       
|               |  |- ZipFourj Zip4j工具类的使用（当命令行过长报错发生时，将会调用此对象的方法进行压缩）
|               |  |- ZipExecutor 压缩线线程池
|               |- CompressArgument 压缩参数：扫描路径、压缩路径、是否加密、是否匿名压缩包名、密码、单个文件的大小
|         |- resources
|         |  | - Windows
|         |  |  | - 7z.exe
|         |  |  | - 7z.dll
|         |  | - Linux
|         |  |  | - 7zz
          
```

# 使用



## 7zip压缩命令行
<table>
<thead>
<tr>
<th>类名</th>
<th>包路径</th>
<th>压缩类型</th>
<th>使用</th>
<th>注意</th>
</tr>
</thead>
<tbody>
<tr>
<td>ZipCommandBuilder</td>
<td>cn.deystar.Compress.Command.ZipCommand.ZipCommandBuilder</td>
<td>zip</td>
<td>这个类将会返回String 类型的命令。需要准备参数
SystemEnums枚举，password(如果不需要加密，可以填入参数null),设备的超线程数数量，压缩包输出路径，文件（绝对路径）或文件列表</td>
<td>如果命令过长，将会转为zip4j压缩。zip4j压缩出来的压缩包，压缩包相比源文件大小相差+-10</td>
</tr>
<tr>
</tr>
</tbody>
</table>

## 扫描路径使用
<table>
<thead>
<tr>
<th>类名</th>
<th>包路径</th>
<th>说明</th>

</tr>
</thead>
<tbody>
<tr>
<td>FileScan</td>
<td>cn.deystar.ScanUtil</td>
<td>通过实例化传递一个ZipArgument，扫描路径。通过getList（）方法可以获取压缩任务(一个FileListBean)就是一个待压缩对象</td>
</tr>
<tr>
</tr>
</tbody>
</table>

## 文件对象 FileListBean
一个FileListBean对象，就是一个由多个文件组成的对象
- 可以选择自己创建FileListBean对象
- 可以继承该对象并使用命令构造类
- 所有属性不允许为空


## 特别鸣谢
<a href="https://dromara.org">dromara</a>带来的工具类<a href="https://hutool.cn/">hutool</a>。在这个项目中用于对压缩包进行匿名操作

<a href="https://www.7-zip.org/">7-zip</a>带来的高性能压缩工具</a>

## License
Apache License 2.0

