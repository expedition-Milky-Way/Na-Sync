package com.deystar;

import com.deystar.Const.BaiduConstEnums;
import com.deystar.Excel.Beans.FieldBean;
import com.deystar.Excel.Service.After.AfterZip;
import com.deystar.Excel.Service.ExcelService;
import com.deystar.Result.ResultState;
import com.deystar.SizeTransform.TransformUtil;
import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;
import com.deystar.Zip.FileScan;
import com.deystar.Zip.GoZip;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author YeungLuhyun
 **/
public class TryZip {


    ThreadPoolExecutor executor;
    Integer coorPoolSize = 20;

    Integer maxPoolSize = 25;

    @Test
    public void toZip() {
        //1. 设置压缩的基本需求
        UserTyper userTyper = new UserTyper();
        userTyper.setSevenZipPath("E:\\intalleds\\7-Zip\\7z.exe");
        userTyper.setIsEncryption(false);
        userTyper.setNeedEncryPath(false);
        userTyper.setOriginPath("E:\\WorkSpace\\testZipDat");
        userTyper.setZipToPath("E:\\WorkSpace\\toZip");
        userTyper.setExcelOutput(userTyper.getZipToPath() + "\\backup");
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
                new LinkedBlockingQueue<>(taskSize - coorPoolSize));
        //4. 运行压缩任务，并输出excel表格
        List<FieldBean> fieldBeans = new ArrayList<>();
        while (!list.isEmpty()) {
            FileListBean zipTask = list.poll();
            // 去setExcelList
            FieldBean bean = new FieldBean();
            bean.setZipName(zipTask.getZipName());
            bean.setSize(TransformUtil.formatFileSize(zipTask.getTotalSize()));
            bean.setOriginParent(zipTask.getOriginParent());
            bean.setPassword(userTyper.getPassword());
            fieldBeans.add(bean);
            GoZip zip = new GoZip(userTyper, zipTask);
            executor.execute(zip::start);
        }
        //输出excel
        ExcelService excelService = new AfterZip();
        excelService.todo(userTyper.getExcelOutput(), fieldBeans);
        //5.等待所有任务完成后关闭线程池
        while (!ResultState.isAllSuccess(taskSize)) {

        }
        executor.shutdown();

    }


}
