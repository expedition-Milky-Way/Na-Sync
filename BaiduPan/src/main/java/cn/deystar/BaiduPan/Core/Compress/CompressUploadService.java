package cn.deystar.BaiduPan.Core.Compress;

import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface CompressUploadService {

    void getFileOfChange();

    void consumer();

    /**
     * 正在执行压缩和上传任务的
     * @return
     */
    List<FileListBean> getUploading();

    List<FileListBean> getCompressing();

    /**
     * 新增一个任务进行同步
     * @param parentPath
     * @return
     */
    Boolean addUploadTask(String parentPath);

    FileListBean getTaskResult()
}
