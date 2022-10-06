package com.example.baidusync.Util.FileUtil;

import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.core.Bean.SysConst;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author 杨 名 (字 露煊)
 * 扫描文件
 */
public class ScanFileUtil extends ZipFileUtil {
    /**
     * 默认没有文件，大小为0
     */
    private static Long INIT_SIZE = 0L;
    /**
     * 存放即将执行的文件树
     */
    private static Map<String, List<File>> FILES_MAP = new HashMap<>();

    public static String PASSWORD = null;

    private String ZIP_PATH = null;
    @Resource
    private RequestNetDiskService requestNetDiskService;


    /**
     * 扫描文件树
     */
    public  void read(File[] files) {
        List list = Arrays.asList(files);
        Collections.sort(list, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile()){
                    return 1;
                }
                if (o1.isFile() && o2.isDirectory()){
                    return -1;
                }
                return 0;
            }
        });
        for (File item : files) {
            String key = item.getParent();
            if (!item.isDirectory()) {
                if (isMoreThen(item)) {
                    InsertFile(key, item);
                } else {
                    zip(); //先把这20G的文件压进队列。清空一下map
                    InsertFile(key, item);
                }
            } else {
                zip(); //先把这20G的文件压进队列。清空一下map
                read(item.listFiles());
            }
        }
    }


    /**
     * 增加文件到MAP中
     */
    private void InsertFile(String key, File file) {
        synchronized (FILES_MAP) {
            if (FILES_MAP.get(key) != null) {
                List<File> fileList = FILES_MAP.get(key);
                fileList.add(file);
                FILES_MAP.put(key, fileList);

            } else {
                List<File> files = new ArrayList<>();
                files.add(file);
                FILES_MAP.put(key, files);
            }
        }
    }

    /**
     * 判断文件是否超过了大小
     */
    private Boolean isMoreThen(File file) {
        synchronized (INIT_SIZE) {
            Long fileSize = FileUtils.sizeOf(file);
            INIT_SIZE += fileSize;
            if (INIT_SIZE <= SysConst.getMaxSize()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 调用进行压缩
     */
    private void zip() {
        synchronized (INIT_SIZE){
            INIT_SIZE = 0L;
        }
        Iterator<Map.Entry<String, List<File>>> iterator = FILES_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
                Map.Entry<String, List<File>> map = iterator.next();
                if (map != null) {
                    String path = map.getKey();
                    String pathGeneral = null;
                    if (path.contains("\\")) {
                        pathGeneral = path.replaceAll("\\\\", "/");
                    } else {
                        pathGeneral = path;
                    }
                    String[] dirs = pathGeneral.split("/");
                    String fileName = dirs[dirs.length - 1];
                    String zipName = ZIP_PATH + "/" + fileName;
                    try {
                        this.zipFile(zipName, map.getValue(), PASSWORD);
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }
            }

        }
        FILES_MAP.clear();

    }

    public ScanFileUtil(String zipPath, String password) {
        this.PASSWORD = password;
        File file = new File(zipPath);
        if (!file.exists()) {
            boolean isMkdir = file.mkdir();
            if (!isMkdir) {
                LogEntity log = new LogEntity("ScanFileUtil", "目录创建失败", LogEntity.LOG_TYPE_WARN);
                LogExecutor.addSysLogQueue(log);
            }
        } else {
            File[] dirFiles = file.listFiles();
            if (dirFiles.length > 0) {
                delteCachePath(dirFiles);
            }

        }
        this.ZIP_PATH = zipPath;

    }

    public void doSomething(String path) {
        File file = new File(path);
        read(file.listFiles());

    }

    /**
     * 清空缓存文件夹下的所有文件
     */
    private  void delteCachePath(File[] files) {
        for (File dirFile : files) {
            if (!dirFile.isDirectory()) {
                dirFile.delete();
            } else {
                delteCachePath(dirFile.listFiles());
            }

        }
    }


}
