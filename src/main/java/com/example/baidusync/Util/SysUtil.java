package com.example.baidusync.Util;

import cn.hutool.core.lang.UUID;

/**
 * @author 杨名 （字 露煊） YeungLuhyun
 * 常用工具
 **/
public class SysUtil {


    /**
     * 重命名路径名
     * @param path
     * @return
     */
    public static String reNamePath(String path){
        if (path.contains("\\")){
            path = path.replace("\\","/");
        }
        return path;
    }


    /**
     * 获取打乱后的文件名，不然可能会被和谐
     */
    public static String onlineName(String fileName){
        String baiduName = UUID.nameUUIDFromBytes(
                fileName.getBytes()).toString();
        return baiduName;
    }

    /**
     * 获取打乱后的文件父目录，不然可能会被和谐
     */
    public static String onlineParent(String parent){
        String baiduParent =  UUID.nameUUIDFromBytes(
                parent.getBytes()).toString();
        return baiduParent;
    }
}
