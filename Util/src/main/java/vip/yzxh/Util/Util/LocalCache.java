package vip.yzxh.Util.Util;

import cn.hutool.core.lang.Dict;

public class LocalCache<T> {


    private static final Dict SYS_DICT = Dict.create();


    public static Object getContext(String key){
        return SYS_DICT.get(key);
    }

    public static void putContext(String key,Object value){
        SYS_DICT.put(key,value);
    }
}
