package com.deystar.Result;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author YeungLuhyun
 * 线程池运行情况
 **/
public class ResultState {

    /**
     * 存放压缩包包名的list。只有线程结束才能add到这里
     */
    private static Set<String> resultList = new HashSet<>();


    public static synchronized void success(String path) {

        if (!new File(path).exists()) {
            return;
        }
        resultList.add(path);


    }


    public static Boolean isAllSuccess(Integer size) {
        return resultList.size() == size;
    }

    public static Integer successNum() {
        return resultList.size();
    }
}
