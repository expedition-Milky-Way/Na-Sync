package cn.deystar.BaiduPan.Core.Compress.impl;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.BaiduPan.Core.Compress.CompressUploadService;
import cn.deystar.BaiduPan.Core.OS.watchFile.Service.WatchFileService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.deystar.Util.ScanAndZip.Util.Scan.FileScan;
import cn.deystar.Util.ScanAndZip.Util.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.Command.ZipCommand.CommandBuilder;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.Suffix.SuffixZip;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.Util.ZipArgument.ZipArgument;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 压缩并上传
 * 所有压缩任务在这里发生。所以上传任务在这里执行
 */
@Service
public class CompressUploadImpl implements CompressUploadService {
    @Resource
    private ThreadPoolTaskExecutor executor;
    @Resource
    private FileSettingService settingService;
    @Resource
    private UserRequestService userRequestService;
    @Resource
    WatchFileService watchFileService;

    private static final Object lock = new Object();
    private static volatile Integer threadStart = 0;

    private static final Queue<ZipAbstract> zipQueue = new LinkedBlockingQueue<>();
    private static AtomicInteger taskCount = new AtomicInteger(0);

    //正在进行任务的列表
    private static final List<Object> todoList = new ArrayList<>();

    /**
     * 分配任务
     */
    private void setThreadStart() {
        if (threadStart == 0) {
            synchronized (lock) {
                if (threadStart == 0) {
                    //任务生产
                    executor.execute(this::getFileOfChange);
                    //任务消费者
                    executor.execute(this::consumer);
                    threadStart++;
                }
            }
        }
    }

    /**
     * 获取文件变动产生的压缩任务
     */
    private void getFileOfChange() {
        while (true) {
            if (watchFileService.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                synchronized (lock) {
                    zipQueue.add(watchFileService.getWaitOfCompress());
                }
            }
        }
    }

    /**
     * 任务的消费者
     */
    private void consumer() {
        while (true) {
            if (!zipQueue.isEmpty()) {
                compressAndUpload(zipQueue.poll());
            }
        }

    }


    private void compressAndUpload(ZipAbstract zipAbstract) {
        Integer count = settingService.getSetting().getTaskNum();
        //如果超过了同步数量就执行等待操作
        if (taskCount.get() + 1 > count) {
            synchronized (zipAbstract) {
                try {
                    zipAbstract.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        taskCount.incrementAndGet();
        // 如果没有则按照正常的压缩和上传执行：CompletableFuture
        //TODO

        if (taskCount.get() < count)
            synchronized (zipAbstract) {
                zipAbstract.notify();
            }
    }

    @Override
    public List<Object> getTodo() {
        return todoList;
    }


    @Override
    public Boolean addUploadTask(String parentPath) {
        File directory = new File(parentPath);
        Boolean result = false;
        if (!directory.exists() && directory.listFiles().length < 1) {
            return result;
        }
        FileSetting fileSetting = settingService.getSetting();
        if (fileSetting == null || !fileSetting.isAllNotNull()) {
            return result;
        }
        UserMsg userMsg = userRequestService.getBaiduUsInfo(fileSetting.getToken().getAccessToken());
        // 1. 进行文件扫描
        ZipArgument zipArgument = new ZipArgument();
        zipArgument.setEncryption(fileSetting.getPassword() != null && !fileSetting.getPassword().trim().isEmpty());
        zipArgument.setOneFileSize(userMsg.getVipTypeEnums().fileSize);
        zipArgument.setOriginPath(parentPath);
        zipArgument.setPassword(fileSetting.getPassword());
        zipArgument.setZipToPath(fileSetting.getCachePath());
        zipArgument.setPathAnonymity(fileSetting.getPathEncryption());

        FileScan scanInstance = new FileScan(zipArgument);
        List<FileListBean> waitOfCompress = scanInstance.getList();

        if (waitOfCompress != null && !waitOfCompress.isEmpty()) {
            for (FileListBean bean : waitOfCompress) {

                String command = new CommandBuilder(fileSetting.getSystemEnums())
                        .outPut(fileSetting.getCompressThread(), bean.getZipName())
                        .password(fileSetting.getPassword())
                        .append(bean.getFileLit())
                        .build();
                ZipAbstract zipService = new SuffixZip(zipArgument, bean, command);
                synchronized (lock){
                    result = zipQueue.add(zipService);
                }
                if (!result){
                    break;
                }
            }
        }
        return result;
    }
}
