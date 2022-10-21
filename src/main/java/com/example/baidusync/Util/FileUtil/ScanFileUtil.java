package com.example.baidusync.Util.FileUtil;

import com.example.baidusync.Util.FileLog.FileLogEntity;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SysUtil;
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
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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


    /**
     * 扫描文件树
     */
    public void read(File[] files) {
        List list = Arrays.asList(files);
        Collections.sort(list, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile()) {
                    return 1;
                }
                if (o1.isFile() && o2.isDirectory()) {
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
                if (FILES_MAP.size() > 0) {
                    zip(); //先把这20G的文件压进队列。清空一下map
                }
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

        Long fileSize = FileUtils.sizeOf(file);
        INIT_SIZE += fileSize;
        return INIT_SIZE <= SysConst.getMaxSize();
    }

    /**
     * 调用进行压缩
     */
    private void zip() {
        INIT_SIZE = 0L;
        Iterator<Map.Entry<String, List<File>>> iterator = FILES_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<File>> map = iterator.next();
            if (map != null) {
                FileLogEntity fileLog = new FileLogEntity();

                String path = map.getKey();
                String pathGeneral = SysUtil.reNamePath(path);
                String[] dirs = pathGeneral.split("/");
                String fileName = dirs[dirs.length - 1];
                String parent = dirs[dirs.length - 2];
                String zipName = ZIP_PATH + fileName;

                fileLog.setCreateTime(new Date());
                fileLog.setOriginalFileName(fileName);
                fileLog.setOriginalPathName(pathGeneral);
                fileLog.setOriginalParentName(parent);
                //调整一下网盘上的文件名和目录名，防止被和谐
                String oneLineName = SysUtil.onlineName(fileName);
                String onLineParent = SysUtil.onlineParent(parent);
                fileLog.setFileName(oneLineName);
                fileLog.setParent(onLineParent);
                //插入FileLog
                Integer id = this.fileLogService.add(fileLog);
                fileLog.setId(id);

                        try {
                            this.zipFile(fileLog, zipName, map.getValue(), PASSWORD);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
            }

        }
        FILES_MAP.clear();

    }

    public ScanFileUtil(String zipPath, String password) {
        PASSWORD = password;
        File file = new File(zipPath);
        if (!file.exists()) {
            boolean isMkdir = file.mkdirs();
            if (!isMkdir) {
                LogEntity log = new LogEntity("ScanFileUtil", "目录创建失败", LogEntity.LOG_TYPE_WARN);
                LogExecutor.addSysLogQueue(log);
            }
        } else {
            File[] dirFiles = file.listFiles();
            if (dirFiles.length > 0) {
                deleteCacheFile(dirFiles);//清空缓存文件
                dirFiles = file.listFiles(); //重新加载文件目录
                deleteCachePath(dirFiles);//清空缓存文件夹
            }
        }
        zipPath = SysUtil.reNamePath(zipPath);
        if (!zipPath.endsWith("/")) {
            zipPath += "/";
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
    private void deleteCacheFile(File[] files) {
        for (File dirFile : files) {
            if (!dirFile.isDirectory()) {
                dirFile.delete();
            } else {
                deleteCacheFile(dirFile.listFiles());
            }
        }
    }

    /**
     * 清空缓存文件夹下所有目录
     */
    private void deleteCachePath(File[] files) {

        for (File item : files) {
            item.delete();
        }
    }


}
