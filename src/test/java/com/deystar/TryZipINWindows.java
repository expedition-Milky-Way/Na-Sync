package com.deystar;

import com.deystar.Const.BaiduConstEnums;
import com.deystar.Const.SystemEnums;

import com.deystar.Result.ResultState;
import com.deystar.SizeTransform.TransformUtil;
import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;
import com.deystar.Zip.FileScan;
import com.deystar.Zip.GoZip;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author YeungLuhyun
 **/
public class TryZipINWindows {


    ThreadPoolExecutor executor;
    Integer coorPoolSize = 20;

    Integer maxPoolSize = 25;

    @Test
    public void toZip() {
        //1. 设置压缩的基本需求
        UserTyper userTyper = new UserTyper();

        userTyper.setIsEncryption(false);
        userTyper.setNeedEncryPath(false);
        userTyper.setOriginPath("E:\\WorkSpace\\zipData");
        userTyper.setZipToPath("E:\\WorkSpace\\toZip");
        userTyper.setExcelPath(userTyper.getZipToPath() + "\\");
        userTyper.setExcelFile("backup.xlsx");
        userTyper.setOneFileSize(BaiduConstEnums.SVIP.size);
        //2.扫描文件
        List<File> fileList = Arrays.asList(new File(userTyper.getOriginPath()).listFiles());
        FileScan scan = new FileScan(userTyper);
        scan.scan(fileList);
        //3. 准备线程池
        Queue<FileListBean> list = scan.getTasks();
        Integer taskSize = list.size();

        executor = new ThreadPoolExecutor(Math.min(taskSize, coorPoolSize),
                maxPoolSize,
                1,
                TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(taskSize));
        //4. 运行压缩任务，并输出excel表格

        while (!list.isEmpty()) {
            FileListBean zipTask = list.poll();

            GoZip zip = new GoZip(userTyper, zipTask, SystemEnums.Windows);
            executor.execute(zip::start);
        }
        //输出excel

        //5.等待所有任务完成后关闭线程池
        while (!ResultState.isAllSuccess(taskSize)) {

        }
        executor.shutdown();

    }

    @Test
    public void newBuilder(){

    }


}
