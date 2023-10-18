package cn.deystar.BaiduPan.Core.OS.watchFile.Service.impl;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;


import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.deystar.Util.ScanAndZip.Const.SystemEnums;
import cn.deystar.Util.ScanAndZip.Scan.FileScan;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.Command.ZipCommand.CommandBuilder;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.Suffix.SuffixZip;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.ZipArgument.ZipArgument;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 监控文件变化
 */

public class WatchFileServiceImpl extends FileAlterationListenerAdaptor  {


    private final FileSettingService settingService = SpringUtil.getBean(FileSettingService.class);
    private final CompressService compressService = SpringUtil.getBean(CompressService.class);
    private final UserRequestService userRequestService = SpringUtil.getBean(UserRequestService.class);


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
        changeBefore(directory);
        System.out.println("新建：" + directory.getAbsolutePath());

    }

    @Override
    public void onDirectoryChange(File directory) {
        changeBefore(directory);
        System.out.println("修改：" + directory.getAbsolutePath());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        changeBefore(directory);
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

        if (!file.exists()) return;
        //1. 构造扫描文件需要的参数
        String parent = file.getParent();
        FileSetting setting = settingService.getSetting();
        ZipArgument zipArgument = new ZipArgument();
        zipArgument.setOriginPath(parent);
        zipArgument.setZipToPath(setting.getCachePath());
        zipArgument.setEncryption(setting.getPassword() != null && !setting.getPassword().trim().isEmpty());
        zipArgument.setPathAnonymity(setting.getPathEncryption() != null ? setting.getPathEncryption() : false);

        TokenResponse tokenDetail = settingService.getToken();

        if (tokenDetail!= null && tokenDetail.isAllNotNull()){
            UserMsg userMsg = userRequestService.getBaiduUsInfo(tokenDetail.getAccessToken());
            zipArgument.setOneFileSize(userMsg.getVipTypeEnums().fileSize);
            FileScan scan = new FileScan(zipArgument);

            File parentFile = new File(parent);
            if (!parentFile.exists()) return;
            //扫描
            scan.scan(Arrays.asList(Objects.requireNonNull(parentFile.listFiles())));
            SystemEnums systemEnums = setting.getSystemEnums();
            if (!scan.getList().isEmpty()) {
                scan.getList().forEach(item -> {
                    String command = new CommandBuilder(systemEnums)
                            .outPut(setting.getCompressThread(), item.getZipName())
                            .password(setting.getPassword())
                            .append(item.getFileLit())
                            .build();
                    //压缩
                    ZipAbstract zipService = new SuffixZip(zipArgument, item, command);
                    compressService.addTask(zipService);
                });
            }
        }

    }







}
