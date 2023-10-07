package cn.deystar.BaiduPan.Core.Compress.impl;

import cn.deystar.BaiduPan.Core.BaiduRequest.Upload.UploadService;
import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.BaiduPan.Core.Upload.UploadTaskService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.*;
import cn.deystar.Util.ScanAndZip.Const.CompressStatus;
import cn.deystar.Util.ScanAndZip.Scan.FileScan;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.Command.ZipCommand.CommandBuilder;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.Suffix.SuffixZip;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.ZipArgument.ZipArgument;
import cn.deystar.Util.SplitFile.SplitFileService;
import cn.hutool.extra.spring.SpringUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 压缩并上传
 * 所有压缩任务在这里发生。所以上传任务在这里执行
 */

public class CompressServiceImpl implements CompressService {

    /**
     * 压缩最高并行度
     */
    private static final Integer MAX_COMPRESS_TASK = 10;

    private static final Executor executor = Executors.newFixedThreadPool(MAX_COMPRESS_TASK);

    private FileSettingService settingService = SpringUtil.getBean(FileSettingService.class);
    @Resource
    private UserRequestService userRequestService;

    @Resource
    UploadTaskService uploadTaskService;


    private static final Object lock = new Object();

    private static final Queue<ZipAbstract> zipQueue = new LinkedBlockingQueue<>();
    private static AtomicInteger taskCount = new AtomicInteger(0);

    private static final Map<String, FileListBean> compressing = new HashMap<>();
    private static final Set<String> todoSet = new HashSet<>();


    {
        Thread consumer = new Thread(() -> {
            FileSetting setting = settingService.getSetting();
            Integer taskNum = setting.getTaskNum();
            while (true) {
                if (zipQueue.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ZipAbstract zipService = zipQueue.poll();
                    if (taskCount.get() > taskNum && taskCount.get() > MAX_COMPRESS_TASK) {
                        synchronized (zipService) {
                            try {
                                zipService.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    executor.execute(() -> {
                        if (zipService != null) {
                            taskCount.incrementAndGet();
                            FileListBean bean = this.compressAndUpload(zipService);
                            if (bean != null) {
                                uploadTaskService.addTask(bean);
                            }
                            taskCount.decrementAndGet();
                            synchronized (zipService){
                                zipService.notify();
                            }
                        }
                    });
                }
            }
        });
        consumer.setDaemon(true);
        consumer.start();
    }

    /**
     * 压缩并上传
     *
     * @param zipAbstract
     */
    private FileListBean compressAndUpload(ZipAbstract zipAbstract) {

        if (todoSet.add(zipAbstract.getBean().getZipName())) {

            zipAbstract.setStatus(CompressStatus.COMPRESSING);

            compressing.put(zipAbstract.getBean().getZipName(), zipAbstract.getBean());
            todoSet.remove(zipAbstract.getBean().getZipName());
            return zipAbstract.call();

        }
        return null;
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
                synchronized (lock) {
                    result = zipQueue.offer(zipService);
                }
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }


    @Override
    public void addTask(ZipAbstract zipService) {
        synchronized (lock) {
            zipQueue.offer(zipService);
        }
    }
}
