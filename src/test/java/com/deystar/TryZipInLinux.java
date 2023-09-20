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
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class TryZipInLinux {
    ThreadPoolExecutor executor;
    Integer coorPoolSize = 20;

    Integer maxPoolSize = 25;

    @Test
    public void toZip() {

        UserTyper userTyper = new UserTyper();
        userTyper.setOneFileSize(BaiduConstEnums.SVIP.size);

        userTyper.setIsEncryption(false);
        userTyper.setNeedEncryPath(false);
        userTyper.setOriginPath("/usr/local/src/picture");
        userTyper.setZipToPath("/usr/local/src/toZip");
        userTyper.setExcelFile("backup.xlsx");
        userTyper.setExcelPath("/usr/local/src/toZip/");
        File dir = new File(userTyper.getOriginPath());
        if (!dir.canRead()) System.out.println("权限不足！");
        if (!dir.exists()) {
            System.out.println("找不到路径");
            return;
        }
        //2.扫描文件
        List<File> fileList = Arrays.asList(dir.listFiles());
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
            // 去setExcelList

            GoZip zip = new GoZip(userTyper, zipTask, SystemEnums.Linux);
            executor.execute(zip::start);
        }

        //5.等待所有任务完成后关闭线程池
        while (!ResultState.isAllSuccess(taskSize)) {

        }
        executor.shutdown();
    }


}
