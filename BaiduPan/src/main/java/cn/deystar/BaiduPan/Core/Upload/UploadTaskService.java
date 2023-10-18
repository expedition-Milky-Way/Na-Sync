package cn.deystar.BaiduPan.Core.Upload;

import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface UploadTaskService {


    /**
     * add upload Task
     *
     * @param bean
     */
    boolean addTask(FileListBean bean);


    Future<FileListBean> getComplete();

    List<FileListBean> getTodo();
}
