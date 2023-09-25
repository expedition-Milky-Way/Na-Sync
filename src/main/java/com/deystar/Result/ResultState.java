package com.deystar.Result;

import com.deystar.Zip.Bean.FileListBean;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author YeungLuhyun
 * 线程池运行情况
 **/
public class ResultState {

    /**
     * 存放压缩包包名的list。只有线程结束才能add到这里
     */
    private static final List<FileListBean> resultList = new ArrayList<>();
    private static final List<FileListBean> errorList = new ArrayList<>();

    private static final Queue<String> resultPrint = new LinkedBlockingQueue<>();

    public static synchronized void success(FileListBean bean) {
        resultPrint.add("success:"+bean.getParent()+" -> "+bean.getZipName());
        resultList.add(bean);
    }

    public static synchronized void error(FileListBean bean) {
        resultPrint.add("error:"+bean.getParent()+" -> "+bean.getZipName());
        errorList.add(bean);
    }

    public static Integer errorNum() {
        return errorList.size();
    }

    public static List<FileListBean> allError() {
        return errorList;
    }

    public static String getResult(){
        String result = null;
        if (!resultPrint.isEmpty()){
            result = resultPrint.poll();
        }
        return result;
    }


    public static synchronized Boolean isAllSuccess(Integer size) {
        return resultList.size() == size;
    }

    public static Integer successNum() {
        return resultList.size();
    }
}
