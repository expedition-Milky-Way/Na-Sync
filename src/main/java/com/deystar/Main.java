package com.deystar;

import com.deystar.Const.BaiduConstEnums;
import com.deystar.Const.SystemEnums;

import com.deystar.Result.ResultState;

import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;
import com.deystar.Zip.FileScan;
import com.deystar.Zip.GoZip;

import java.io.File;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class Main {

    static ThreadPoolExecutor executor;
    static Integer coorPoolSize = 20;

    static Integer maxPoolSize = 25;

    public static void main(String[] args) {
        //1. 设置压缩的基本需求
        UserTyper userTyper = new UserTyper();

        userTyper.setIsEncryption(false);
        userTyper.setNeedEncryPath(false);
        userTyper.setOriginPath("C:\\Users\\YeungLuhyun\\Desktop\\测试\\重要数据迁移");
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
        System.out.println(taskSize);
        executor = new ThreadPoolExecutor(Math.min(taskSize, coorPoolSize),
                maxPoolSize + 4,
                1,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(taskSize - 4));
        //4. 运行压缩任务，并输出excel表格

        while (!list.isEmpty()) {
            FileListBean zipTask = list.poll();

            // 去setExcelList
            GoZip zip = new GoZip(userTyper, zipTask, SystemEnums.Windows);
            executor.execute(zip::start);
//            System.out.println(zipTask.getZipName());
        }
        //输出excel

        //5.等待所有任务完成后关闭线程池
        while (!ResultState.isAllSuccess(taskSize) && (ResultState.successNum()+ ResultState.errorNum()) != taskSize) {
            String result = ResultState.getResult();
            if (result != null){
                System.out.println(result);
            }
        }
        executor.shutdown();
        for (String item :ResultState.allError()){
            System.out.println(item);
        }

    }
}
