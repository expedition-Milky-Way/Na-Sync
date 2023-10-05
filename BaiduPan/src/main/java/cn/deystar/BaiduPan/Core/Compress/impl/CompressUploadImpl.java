package cn.deystar.BaiduPan.Core.Compress.impl;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.Upload.UploadService;
import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.BaiduPan.Core.Compress.CompressUploadService;
import cn.deystar.BaiduPan.Core.OS.watchFile.Service.WatchFileService;
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
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.deystar.Util.SplitFile.SplitFileService;
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
    @Resource
    private UploadService uploadService;
    @Resource
    private SplitFileService splitFileService;

    private static final Object lock = new Object();
    private static Integer consumerStart = 0;
    private static Integer watchFileStart = 0;

    private static final Queue<ZipAbstract> zipQueue = new LinkedBlockingQueue<>();
    private static AtomicInteger taskCount = new AtomicInteger(0);

    private static final Map<String, FileListBean> compressing = new HashMap<>();

    private static final Map<String, FileListBean> uploading = new HashMap<>();

    private static final List<CompletableFuture<FileListBean>> todoList = new ArrayList<>();


    /**
     * 获取文件变动产生的压缩任务
     */
    @Override
    public void getFileOfChange() {
        if (watchFileStart == 0) {
            synchronized (watchFileStart) {
                if (watchFileStart == 0) {
                    watchFileStart++;
                }
            }
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

    }

    /**
     * 任务的消费者
     */
    @Override
    public void consumer() {
        if (consumerStart == 0) {
            synchronized (consumerStart) {
                if (consumerStart == 0) {
                    consumerStart++;
                }
            }
            while (true) {
                if (!zipQueue.isEmpty()) {
                    synchronized (lock) {
                        compressAndUpload(zipQueue.poll());
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 压缩并上传
     *
     * @param zipAbstract
     */
    private void compressAndUpload(ZipAbstract zipAbstract) {
        FileSetting setting = settingService.getSetting();

        Integer count = setting.getTaskNum();
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
        CompletableFuture<FileListBean> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    if (zipQueue.isEmpty()) return null;
                    ZipAbstract zipService = zipQueue.poll();
                    zipService.setStatus(CompressStatus.COMPRESSING);
                    FileListBean compressBean = zipService.getBean();
                    String key = compressBean.getZipName();
                    compressing.put(key, compressBean);
                    return zipService.call();
                }, executor) //压缩，
                .thenApply((bean) -> {
                    if (bean == null) return null;
                    String key = bean.getZipName();
                    bean.setStatus(CompressStatus.UPLOADING);
                    compressing.remove(key);
                    uploading.put(key, bean);

                    if (bean.getTotalSize() <= BaiduConst.STEP_BY_UPLOAD_SIZE) {
                        StepByUploadResponse response = uploadService.oneStepUpload(bean);
                        //记录日志
                        if (response != null && response.getErrno() == 0) {

                        }
                    } else {
                        //分片上传
                        TokenResponse token = setting.getToken();
                        UserMsg userMsg = userRequestService.getBaiduUsInfo(token.getAccessToken());
                        // 分片
                        ChunkBean chunkBean = splitFileService.splitFile(bean, null, userMsg.getVipTypeEnums().tempSize.intValue());
                        // 上传
                        ReadyToUploadResponse readyToUploadResponse = uploadService.readyToUpload(chunkBean);
                        if (readyToUploadResponse == null || readyToUploadResponse.getErrno() != 0) {

                        }
                        boolean allUploadSuccess = false;
                        for (TempBean item : chunkBean.getBeanList()) {
                            Integer index = chunkBean.getBeanList().indexOf(item);
                            index++;
                            boolean uploadSuccess = uploadService.postSendFile(item, index, readyToUploadResponse);
                            allUploadSuccess = uploadSuccess;
                            if (!uploadSuccess) {
                                break;
                            }
                            //进度条
                            bean.setUploadPercent(getUploadPercent(index, chunkBean.getSize()));
                            uploading.put(key, bean);
                        }
                        CreateFileResponse createFileResponse = null;
                        if (allUploadSuccess) {
                            createFileResponse = uploadService.createFile(chunkBean, readyToUploadResponse);
                        }
                        if (createFileResponse == null || createFileResponse.getErrno() != 0) {
                            //记录错误日志
                        }
                    }
                    //唤起等待中的线程
                    synchronized (zipAbstract) {
                        zipAbstract.notify();
                    }
                    taskCount.decrementAndGet();

                    return bean;
                });
        todoList.add(completableFuture);
    }

    @Override
    public List<FileListBean> getUploading() {
        List<FileListBean> beanList = null;
        if (!uploading.isEmpty()) {
            beanList = (List<FileListBean>) uploading.values();
        }
        return beanList;
    }

    @Override
    public List<FileListBean> getCompressing() {
        List<FileListBean> beanList = null;
        if (!compressing.isEmpty()) {
            beanList = (List<FileListBean>) compressing.values();
        }
        return beanList;
    }

    /**
     * 计算进度条
     */
    private Integer getUploadPercent(Integer index, Integer totalSize) {
        Integer totalProgress = 100 / totalSize;
        return (index * totalProgress);
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
    public FileListBean getTaskResult() {
        FileListBean result = null;
        if (!todoList.isEmpty()) {
            Future<FileListBean> future = todoList.remove(0);
            while (!future.isDone()) {

            }
            try {
                result = future.get(100, TimeUnit.MICROSECONDS);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
