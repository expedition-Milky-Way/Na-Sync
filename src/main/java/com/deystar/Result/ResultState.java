package com.deystar.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author YeungLuhyun
 * 线程池运行情况
 **/
public class ResultState {

    /**
     * 存放压缩包包名的list。只有线程结束才能add到这里
     */
    private volatile static List<String> resultList = new ArrayList<>();


    public static synchronized void success(String path) {

        if (!new File(path).exists()) {
            System.out.println(path + "没压缩完");
            return;
        }
        resultList.add(path);


    }


    public static synchronized Boolean isAllSuccess(Integer size) {
        return resultList.size() == size;
    }

    public static Integer successNum() {
        return resultList.size();
    }
}
