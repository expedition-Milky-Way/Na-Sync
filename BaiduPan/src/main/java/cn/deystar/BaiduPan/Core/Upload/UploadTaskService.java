package cn.deystar.BaiduPan.Core.Upload;

import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface UploadTaskService {


    /**
     * add upload Task
     * @param bean
     */
    void addTask(FileListBean bean);
}
