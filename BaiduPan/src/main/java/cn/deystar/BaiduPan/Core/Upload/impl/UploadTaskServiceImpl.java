package cn.deystar.BaiduPan.Core.Upload.impl;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.Upload.UploadService;

import cn.deystar.BaiduPan.Core.Upload.UploadTaskService;
import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.CreateFileResponse;
import cn.deystar.Util.BaiduPanResponse.ReadyToUploadResponse;
import cn.deystar.Util.BaiduPanResponse.StepByUploadResponse;

import cn.deystar.Util.Const.VipTypeEnums;
import cn.deystar.Util.ScanAndZip.Const.CompressStatus;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.deystar.Util.SplitFile.SplitFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */

public class UploadTaskServiceImpl implements UploadTaskService {

    private static final AtomicInteger taskCount = new AtomicInteger(0);

    @Value("${baidu-netdisk.path}")
    private String originPath;

    @Resource
    private ThreadPoolTaskExecutor executor;

    @Resource
    private FileSettingService settingService;

    @Resource
    private SplitFileService splitFileService;
    @Resource
    private UploadService uploadService;


    private static final Map<String, FileListBean> uploadingBean = new HashMap<>();
    private static final Queue<FileListBean> queue = new LinkedBlockingQueue<>();
    private static final Queue<Future<FileListBean>> complateQueue = new LinkedBlockingQueue<>();

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
                        if (taskCount.get() + 1 > taskNum) {
                            synchronized (bean) {
                                try {
                                    bean.wait();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        Future<FileListBean> future = executor.submit(() -> this.uploadTodo(bean));
                        complateQueue.offer(future);
                    }

                }
            }
        });
        uploadWatcher.setDaemon(true);
        uploadWatcher.start();
    }


    private FileListBean uploadTodo(FileListBean bean) {
        String doingKey = bean.getZipName();
        bean.setUploadPercent(0); // setting upload`s progress is 0
        uploadingBean.put(doingKey, bean);
        bean.setStatus(CompressStatus.UPLOADING);

        if (bean.getTotalSize() <= BaiduConst.STEP_BY_UPLOAD_SIZE) {
            // one step to upload
            StepByUploadResponse response = uploadService.oneStepUpload(bean);
            this.removePackage(bean); // 删除压缩包
            if (response != null && response.getErrno() == 0L) {
                uploadingBean.remove(doingKey);
            }
        } else {
            try {
                String chunkOutPutPath = this.genChunkPath(bean);
                // create the storage path on origin
                String originPath = this.genOriginPath(bean.getSourceParent());
                if (originPath != null) {

                    //1. split the file. Baidu company`s programmer tell me use 4096 would be batter
                    ChunkBean chunkBean = splitFileService.splitFile(bean, chunkOutPutPath, VipTypeEnums.NORMAL.tempSize);

                    if (chunkBean == null) {
                        synchronized (bean) {
                            bean.notify();
                        }
                        return bean;
                    }
                    this.removePackage(bean); // 删除压缩包

                    chunkBean.setOriginPath(originPath);
                    // tell baidu I want to send file
                    ReadyToUploadResponse ready = uploadService.readyToUpload(chunkBean);

                    boolean isAllSuccess = true;
                    if (ready != null && ready.getErrno() == 0 && ready.getUploadId() != null) {
                        // upload the chunk now!
                        for (TempBean temp : chunkBean.getBeanList()) {
                            Integer index = chunkBean.getBeanList().indexOf(temp);
                            System.out.println("正在上传" + bean.getZipName() + "第" + (index + 1) + "个分片,一共" + chunkBean.getBeanList().size() + "个分片");
                            if (!uploadService.postSendFile(temp, chunkBean.getOriginFileName(), index, ready)) {
                                isAllSuccess = false;
                                System.out.println(bean.getZipName() + "上传失败，最后一个上传的分片是" + chunkBean.getPath() + "/" + index + ".tmp");
                                bean.setStatus(CompressStatus.UPLOAD_ERROR);
                                break;
                            } else {
                                bean.setUploadPercent(this.getUploadPercent(index, chunkBean.getSize()));
                                uploadingBean.put(doingKey, bean);
                            }
                            System.out.println(bean.getZipName() + "第" + (index + 1) + "个分片，还剩" + (chunkBean.getBeanList().size() - (index + 1) + "个分片"));
                        }
                        this.removeCache(chunkBean); //删除分片文件和文件夹
                        if (isAllSuccess) {
                            // upload was complete. create the file on origin now
                            CreateFileResponse createFileResponse = uploadService.createFile(chunkBean, ready);
                            System.out.println(createFileResponse);
                            if (createFileResponse != null && createFileResponse.getErrno() == 0) {
                                System.out.println(bean.getZipName() + "上传成功：" + chunkBean.getOriginFileName());

                                bean.setStatus(CompressStatus.UPLOAD_SUCCESS);

                                uploadingBean.remove(doingKey);
                            } else {
                                bean.setStatus(CompressStatus.UPLOAD_ERROR);
                                System.out.println(bean.getZipName() + "上传失败：" + chunkBean.getOriginFileName());
                            }
                        }
                    } else {
                        System.out.println("上传失败：预上传失败\n" + ready.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                this.removePackage(bean);
            }
        }
        uploadingBean.remove(doingKey);
        synchronized (bean) {
            bean.notify();
        }
        return bean;
    }


    private String genOriginPath(String onOriginPath) {
        String path = originPath + (onOriginPath.startsWith("/") || onOriginPath.startsWith("\\") ? onOriginPath.substring(1, onOriginPath.length() - 1) : onOriginPath);
        path = path.replace("\\", "/").replace(":", "");

        return path;
    }

    /**
     * 计算进度条
     */
    private Integer getUploadPercent(Integer index, Long totalSize) {
        Integer totalProgress = new BigDecimal("100")
                .divide(new BigDecimal(totalSize), RoundingMode.HALF_DOWN).intValue();
        return (index * totalProgress);
    }

    /**
     * 删除压缩文件
     *
     * @param bean
     * @return
     */
    private boolean removePackage(FileListBean bean) {
        if (bean == null || bean.getFileLit() == null) return false;
        File file = new File(bean.getZipName());
        return file.delete();
    }

    /**
     * 删除分片
     *
     * @param chunkBean
     * @return
     */
    private boolean removeCache(ChunkBean chunkBean) {
        if (chunkBean == null || chunkBean.getBeanList() == null) return false;
        for (TempBean tempBean : chunkBean.getBeanList()) {
            tempBean.getChunk().delete();
        }
        File directory = new File(chunkBean.getPath());
        return directory.delete();
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
    public boolean addTask(FileListBean bean) {

        return queue.offer(bean);

    }

    @Override
    public Future<FileListBean> getComplete() {

        return complateQueue.poll();


    }

    @Override
    public List<FileListBean> getTodo() {
        if (uploadingBean.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(uploadingBean.values());
    }

}
