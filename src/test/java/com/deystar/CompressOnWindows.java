package com.deystar;

import com.deystar.Const.BaiduConstEnums;
import com.deystar.Const.SystemEnums;
import com.deystar.Result.ResultState;
import com.deystar.Zip.SevenZip.Command.CommandBuilder;
import com.deystar.Zip.SevenZip.RunZipTask.ZipService;
import com.deystar.Zip.ZipExecutor;
import com.deystar.ZipArgument.ZipArgument;
import com.deystar.Zip.Entity.FileListBean;
import com.deystar.Scan.FileScan;
import com.deystar.Zip.SevenZip.RunZipTask.SuffixZip;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */

public class CompressOnWindows {


    public static void main(String[] args) {
        //1. 设置压缩的基本需求
        ZipArgument userTyper = new ZipArgument();

        userTyper.setEncryption(false);
        userTyper.setPathAnonymity(false);
        userTyper.setOriginPath("C:\\Users\\YeungLuhyun\\Desktop\\测试\\重要数据迁移");
        userTyper.setZipToPath("E:\\WorkSpace\\toZip");

        userTyper.setOneFileSize(BaiduConstEnums.SVIP.size);
        //2.扫描文件
        List<File> fileList = Arrays.asList(new File(userTyper.getOriginPath()).listFiles());
        FileScan scan = new FileScan(userTyper);
        scan.scan(fileList);
        //3. 准备线程池
        Queue<FileListBean> list = scan.getTasks();
        Integer taskSize = list.size();
        //4. 运行压缩任务，并输出excel表格
        Integer core = Math.min(taskSize, 20);
        Integer max = core + 5;
        Integer queueSize = taskSize - 5 > core ? taskSize - 5 : core;
        ZipExecutor zipExecutor = new ZipExecutor(core, max, queueSize);

        while(!list.isEmpty())

        {
            FileListBean zipTask = list.poll();
            String command = new CommandBuilder(SystemEnums.Windows)
                    .outPut(Runtime.getRuntime().availableProcessors(), zipTask.getZipName())
                    .password(userTyper.getPassword())
                    .append(zipTask.getFileLit()).build();
            ZipService zipService = new SuffixZip(userTyper, zipTask, command);
            zipExecutor.execute(zipService);
        }
        //输出excel

        //5.等待所有任务完成后关闭线程池
        while(!ResultState.isAllSuccess(taskSize)&&(ResultState.successNum()+ResultState.errorNum())!=taskSize)

        {
            String result = ResultState.getResult();
            if (result != null) {
                System.out.println(result);
            }
        }
        zipExecutor.shutdown();


    }

}
