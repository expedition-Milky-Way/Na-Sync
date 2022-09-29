package com.example.baidusync.core;

import com.example.baidusync.Util.FileAndDigsted;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 杨 名 (字 露煊)
 */
public class SystemCache {

   private  static  Executor executor = Executors.newFixedThreadPool(1);

    private static LinkedTransferQueue<Map<String,Object>> queue = new LinkedTransferQueue<>();


    public static void set(Map<String,Object> digsteds){
        synchronized (queue){
            queue.offer(digsteds);
        }
    }

    public static Map<String, Object> get(){
        synchronized (queue){
            return queue.poll();
        }
    }

    public static boolean isEmpty(){
        return queue.size()==0;
    }

    
}
