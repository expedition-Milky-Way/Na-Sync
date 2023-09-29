package cn.deystar.BaiduPan.Core.OS.watchFile.Service.impl;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;


import cn.deystar.BaiduPan.Core.OS.watchFile.Service.WatchFileService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.ScanAndZip.Util.Const.SystemEnums;
import cn.deystar.Util.ScanAndZip.Util.Scan.FileScan;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.Command.ZipCommand.CommandBuilder;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.Suffix.SuffixZip;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.Util.ZipArgument.ZipArgument;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 监控文件变化
 */
@Service
public class WatchFileServiceImpl extends FileAlterationListenerAdaptor implements WatchFileService {

    @Resource
    private FileSettingService settingService;
    @Resource
    private UserRequestService service;


    private static final List<ZipAbstract> taskQueue = new ArrayList<>();

    //索引反查集
    private static final Map<String, Integer> indexMap = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("新建：" + directory.getAbsolutePath());

    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("修改：" + directory.getAbsolutePath());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("删除：" + directory.getAbsolutePath());

    }

    @Override
    public void onFileCreate(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("新建：" + compressedPath);
        if (file.canRead()) {
            changeBefore(file);
        }
    }

    @Override
    public void onFileChange(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("修改：" + compressedPath);
        changeBefore(file);
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("删除：" + file.getAbsolutePath());
        changeBefore(file);

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
    private void changeBefore(File file) {

        synchronized (file) {
            try {
                file.wait(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!file.exists()) return;
        //1. 构造扫描文件需要的参数
        String parent = file.getParent();
        FileSetting setting = settingService.getSetting();
        ZipArgument zipArgument = new ZipArgument();
        zipArgument.setOriginPath(parent);
        zipArgument.setEncryption(setting.getPassword() != null && !setting.getPassword().trim().isEmpty());
        zipArgument.setPathAnonymity(setting.getPathEncryption() != null ? setting.getPathEncryption() : false);
        zipArgument.setOneFileSize(service.getBaiduUsInfo(setting.getToken().getAccessToken()).getVipTypeEnums().fileSize);
        FileScan scan = new FileScan(zipArgument);

        File parentFile = new File(parent);
        if (!parentFile.exists()) return;
        //扫描
        scan.scan(Arrays.asList(Objects.requireNonNull(parentFile.listFiles())));
        if (!scan.getList().isEmpty()) {
            scan.getList().forEach(item -> {
                String command = new CommandBuilder(SystemEnums.Linux)
                        .outPut(setting.getCompressThread(), setting.getCachePath())
                        .password(setting.getPassword())
                        .append(item.getFileLit())
                        .build();
                //压缩
                ZipAbstract zipService = new SuffixZip(zipArgument, item, command);
                synchronized (lock) {
                    if (indexMap.containsKey(parent)) {
                        Integer queueIndex = indexMap.get(file.getParent());
                        if (taskQueue.size() > queueIndex && taskQueue.get(queueIndex) != null) {
                            taskQueue.set(queueIndex, zipService);

                        }
                    } else {
                        taskQueue.add(zipService);
                        indexMap.put(parent, taskQueue.size() - 1);
                    }
                }
            });
        }
    }


    @Override
    public ZipAbstract getWaitOfCompress(){
        synchronized (lock){
            ZipAbstract task = taskQueue.remove(0);
            for (String i: indexMap.keySet()){
                Integer index = indexMap.get(i);
                if (index-- > -1){
                    indexMap.put(i,index);
                }
            }
            return task;
        }
    }

    @Override
    public Boolean isEmpty(){
        return taskQueue.isEmpty();
    }


}
