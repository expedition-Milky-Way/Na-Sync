package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.MonitorSerivce;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;


import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service.TimingUpload;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service.UploadModeService;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service.WatchUpload;
import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;
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

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 监控文件变化
 */

public class WatchFileServiceImpl extends FileAlterationListenerAdaptor {

    private final FileSettingService settingService = SpringUtil.getBean(FileSettingService.class);
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
        changeBefore(directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("修改文件目录：" + directory.getAbsolutePath());
        changeBefore(directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("删除目录：" + directory.getAbsolutePath());
        changeBefore(directory);
    }

    @Override
    public void onFileCreate(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("新建文件：" + compressedPath);
        if (file.canRead()) {
            String path = file.getParent();
            File directory = new File(path);
            changeBefore(directory);
        }
    }

    @Override
    public void onFileChange(File file) {
        String compressedPath = file.getAbsolutePath();
        System.out.println("修改文件：" + compressedPath);

        String path = file.getParent();
        File directory = new File(path);
        changeBefore(directory);
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("删除文件：" + file.getAbsolutePath());
        String path = file.getParent();
        File directory = new File(path);
        changeBefore(directory);

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
        FileSetting setting = settingService.getSetting();
        if (setting != null && setting.getIsListen() != null) {
            UploadModeService mode = (UploadModeService) invokedMap.get(setting.getIsListen());
//            mode.add(file.getAbsolutePath());
        }
    }


}
