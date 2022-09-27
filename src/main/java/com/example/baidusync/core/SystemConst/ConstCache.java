package com.example.baidusync.core.SystemConst;

import cn.hutool.core.util.ObjectUtil;

/**
 * @author 杨 名 (字 露煊)
 */
public class ConstCache {


    static final ThreadLocal<ConstCacheData> threadLocal = new ThreadLocal<>();

    public static ConstCacheData get(){
        return   threadLocal.get();
    }

    public static void set(ConstCacheData data){
        threadLocal.set(data);
    }

    public static boolean has(){
      return ObjectUtil.isNull(threadLocal.get());
    }
}
