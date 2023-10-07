package cn.deystar.BaiduPan.Core.Upload.impl;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.NetDiskPath.NetDiskPathService;
import cn.deystar.BaiduPan.Core.BaiduRequest.Upload.UploadService;
import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.BaiduPan.Core.Upload.UploadTaskService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.CreateFileResponse;
import cn.deystar.Util.BaiduPanResponse.ReadyToUploadResponse;
import cn.deystar.Util.BaiduPanResponse.StepByUploadResponse;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.deystar.Util.SplitFile.SplitFileService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */

public class UploadTaskServiceImpl implements UploadTaskService {

    private static final AtomicInteger taskCount = new AtomicInteger(0);

    @Value("${baidu-netdisk.path}")
    private String originPath;
    /**
     * 上传最高并行度
     */
    private static final Integer MAX_UPLOAD_TASK = 10;
    private static final Executor executor = Executors.newFixedThreadPool(MAX_UPLOAD_TASK);
    @Resource
    private FileSettingService settingService;

    @Resource
    private SplitFileService splitFileService;
    @Resource
    private UploadService uploadService;

    @Resource
    private UserRequestService userRequestService;

    @Resource
    private NetDiskPathService netDiskPathService;

    private static final Map<String, FileListBean> uploadingBean = new HashMap<>();
    private static final Queue<FileListBean> queue = new LinkedBlockingQueue<>();


    {
        Thread uploadWatcher = new Thread(() -> {
            while (true) {
                if (queue.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    FileSetting setting = settingService.getSetting();
                    Integer taskNum = setting.getTaskNum();
                    FileListBean bean = queue.poll();
                    if (bean != null) {
                        if (taskCount.get() + 1 > taskNum && taskCount.get() + 1 > MAX_UPLOAD_TASK) {
                            synchronized (bean) {
                                try {
                                    bean.wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        executor.execute(() -> this.uploadTodo(bean));
                    }

                }
            }
        });
        uploadWatcher.setDaemon(true);
        uploadWatcher.start();
    }


    private FileListBean uploadTodo(FileListBean bean) {
        FileSetting setting = settingService.getSetting();
        String doingKey = bean.getZipName();
        bean.setUploadPercent(0); // setting upload`s progress is 0
        uploadingBean.put(doingKey, bean);

        if (bean.getTotalSize() <= BaiduConst.STEP_BY_UPLOAD_SIZE) {

            StepByUploadResponse response = uploadService.oneStepUpload(bean);
            if (response != null && response.getErrno() == 0L) {
                bean.setUploadPercent(100);
                uploadingBean.put(doingKey, bean);
                synchronized (bean) {
                    bean.notify();
                }
                return bean;
            }
        } else {
            //1. obtain the account`s detail
            UserMsg userMsg = userRequestService.getBaiduUsInfo(setting.getToken().getAccessToken());
            try {
                String chunkOutPutPath = this.genChunkPath(bean);
                // create the storage path on origin
                if (this.genOriginPath(bean.getOriginParent())) {

                    //1. split the file
                    ChunkBean chunkBean = splitFileService.splitFile(bean, chunkOutPutPath, userMsg.getVipTypeEnums().tempSize);

                    // tell baidu I want to send file
                    ReadyToUploadResponse ready = uploadService.readyToUpload(chunkBean);
                    boolean isAllSuccess = true;
                    if (ready != null && ready.getErrno() == 0) {
                        // upload the chunk now!
                        for (TempBean temp : chunkBean.getBeanList()) {
                            Integer index = chunkBean.getBeanList().indexOf(temp);
                            if (!uploadService.postSendFile(temp, index, ready)) {
                                isAllSuccess = false;
                                break;
                            } else {
                                bean.setUploadPercent(this.getUploadPercent(index, chunkBean.getSize()));
                                uploadingBean.put(doingKey, bean);
                            }
                        }
                        if (isAllSuccess) {
                            // upload was complete. create the file on origin now
                            CreateFileResponse createFileResponse = uploadService.createFile(chunkBean, ready);
                            if (createFileResponse != null && createFileResponse.getErrno() == 0) {
                                synchronized (bean) {
                                    bean.notify();
                                }
                                return bean;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        synchronized (bean) {
            bean.notify();
        }
        return null;
    }


    private boolean genOriginPath(String onOriginPath) {
        String path = originPath + (onOriginPath.startsWith("/") || onOriginPath.startsWith("\\") ? onOriginPath.substring(1, onOriginPath.length() - 1) : onOriginPath);
        path = path.replace("\\", "/").replace(":", "");
        if (!netDiskPathService.hasDir(path)) {
            return netDiskPathService.postCreateNetDisk(path);
        }
        return true;
    }

    /**
     * 计算进度条
     */
    private Integer getUploadPercent(Integer index, Long totalSize) {
        Integer totalProgress = new BigDecimal("100").divide(new BigDecimal(totalSize), RoundingMode.HALF_DOWN).intValue();
        return (index * totalProgress);
    }


    /**
     * Chunk Path Generator
     *
     * @param bean
     * @return
     */
    private String genChunkPath(FileListBean bean) {
        String path = bean.getZipName();
        if (path.contains(".")) path = path.split("\\.")[0];
        path = path.replace("\\", "/") + "/";

        return path;
    }

    @Override
    public void addTask(FileListBean bean) {

        synchronized (queue) {
            queue.offer(bean);
        }
    }

}
