package cn.deystar.BaiduPan.Core.Compress.impl;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.BaiduPan.Core.Upload.UploadTaskService;
import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;
import cn.deystar.Util.ScanAndZip.Const.CompressStatus;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.hutool.extra.spring.SpringUtil;

import javax.annotation.Resource;
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

            while (true) {
                if (zipQueue.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {

                    while (setting == null || setting.getTaskNum() == null){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    Integer taskNum = setting.getTaskNum();
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
                    if (zipService != null) {
                        executor.execute(() -> {
                            taskCount.incrementAndGet();
                            FileListBean bean = this.compressAndUpload(zipService);
                            if (bean != null) {
                                synchronized (compressing){
                                    compressing.remove(bean.getZipName());
                                }

                                uploadTaskService.addTask(bean);
                            }
                            taskCount.decrementAndGet();
                            synchronized (zipService) {
                                zipService.notify();
                            }
                        });
                    }
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
        FileListBean bean = null;
        if (todoSet.add(zipAbstract.getBean().getZipName())) {

            zipAbstract.setStatus(CompressStatus.COMPRESSING);
            synchronized (compressing){
                compressing.put(zipAbstract.getBean().getZipName(), zipAbstract.getBean());
            }

            bean = zipAbstract.call();
            synchronized (compressing){
                compressing.put(zipAbstract.getBean().getZipName(), zipAbstract.getBean());
            }


        }
        return bean;
    }


    @Override
    public void addTask(ZipAbstract zipService) {
        synchronized (lock) {
            zipQueue.offer(zipService);
        }
    }

    @Override
    public List<FileListBean> getCompressing() {

        if (compressing.isEmpty()) return new ArrayList<>();
        synchronized (compressing){
            return new ArrayList<>(compressing.values());
        }

    }
}
