package com.example.baidusync.core;

import com.example.baidusync.Util.FileAndDigsted;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 杨 名 (字 露煊)
 */
public class SystemCache {

   private  static  Executor executor = Executors.newFixedThreadPool(1);

    private static Map<String, List<FileAndDigsted>> map = new HashMap<>();


    public static void set(String key,List<FileAndDigsted> digsteds){
        synchronized (map){
            if (map.get(key) != null){
                List<FileAndDigsted> mapValue = map.get(key);
                digsteds.forEach(mapValue::add);
            }
            map.put(key,digsteds);
        }
    }

    public static List<FileAndDigsted> get(String key){
        if (map.get(key)!= null){
            return map.get(key);
        }else {
            return null;
        }
    }


    public static List<FileAndDigsted> getAndRemove(String key){
        if (map.get(key) != null){
            synchronized (map){
                List<FileAndDigsted> value = map.get(key);
                executor.execute(()->map.remove(key));
                return value;
            }
        }
        return null;
    }
    
}
