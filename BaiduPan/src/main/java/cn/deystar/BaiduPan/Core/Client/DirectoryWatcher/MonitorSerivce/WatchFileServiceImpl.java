package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.MonitorSerivce;


import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Enums.FileCRUD;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service.TimingUpload;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service.UploadModeService;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service.WatchUpload;

import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;


import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 监控文件变化
 */

public class WatchFileServiceImpl extends FileAlterationListenerAdaptor {

    private final ExecutorService executors = Executors.newFixedThreadPool(6);
    private final FileSettingService settingService = SpringUtil.getBean(FileSettingService.class);
    private static final Map<String, Integer> record = new HashMap<>(); // 反查集
    private static final List<String> pathList = new ArrayList<>();
    static Map<Integer, Object> invokedMap = new HashMap<>();

    static {
        invokedMap.put(0, SpringUtil.getBean(WatchUpload.class));
        invokedMap.put(1, SpringUtil.getBean(TimingUpload.class));
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("新建目录：" + directory.getAbsolutePath());

    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("修改文件目录：" + directory.getAbsolutePath());
//        changeBefore(directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("删除目录：" + directory.getAbsolutePath());

    }

    @Override
    public void onFileCreate(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("新建文件：" + compressedPath);
        executors.execute(() -> changeBefore(file.getParentFile(), FileCRUD.CREATE));
    }

    @Override
    public void onFileChange(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("修改文件：" + compressedPath);
        executors.execute(() -> changeBefore(file.getParentFile(), FileCRUD.CHANGE));
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("删除文件：" + file.getAbsolutePath());
        executors.execute(() -> changeBefore(file.getParentFile(), FileCRUD.DELETE));

    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

    /**
     * 文件发生变化后处理事件
     *
     * @param file
     */
    private void changeBefore(File file, FileCRUD method) {
        if (!file.exists()) return;
        if (method == null) return;
        if (method.equals(FileCRUD.DELETE)) {
            String path = file.getPath();
            synchronized (file) {

                pathList.add(path);
                record.put(path, pathList.size() - 1);
                try {
                    file.wait(2000L);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("反查集内"+record.containsKey(path));
            if (!record.containsKey(path)) {
                return;
            }
        }
        if (method.equals(FileCRUD.CREATE)) {
            for (int i = 0 ; i< pathList.size() ; i++) {
                String item = pathList.get(i);
                System.out.println(item);
                if (item.equals(file.getPath()) || item.contains(file.getPath()) || file.getPath().contains(item)) {
                    if (item.length() >= file.getPath().length()) {
                        pathList.remove(item);
                        record.remove(item);
                    }else{
                        synchronized (file) {
                            file.notifyAll();
                        }
                        return; //强制线程退出
                    }
                }
            }
            synchronized (file) {
                file.notifyAll();
            }
        }
        //1. 构造扫描文件需要的参数
        FileSetting setting = settingService.getSetting();
        if (setting != null && setting.getIsListen() != null) {
            UploadModeService mode = (UploadModeService) invokedMap.get(setting.getIsListen());
            mode.add(file.getAbsolutePath());
        }
    }


}
