package com.deystar.Result;

import java.io.File;
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
    private static final List<String> resultList = new ArrayList<>();
    private static final List<String> errorList = new ArrayList<>();

    private static final Queue<String> resultPrint = new LinkedBlockingQueue<>();

    public static synchronized void success(String path) {
        resultPrint.add(path);
        resultList.add(path);
    }

    public static synchronized void error(String path) {
        resultPrint.add(path);
        errorList.add(path);
    }

    public static Integer errorNum() {
        return errorList.size();
    }

    public static List<String> allError() {
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
