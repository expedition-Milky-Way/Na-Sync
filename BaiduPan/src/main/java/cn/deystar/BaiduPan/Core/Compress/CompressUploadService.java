package cn.deystar.BaiduPan.Core.Compress;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface CompressUploadService {

    /**
     * 正在执行压缩和上传任务的
     * @return
     */
    List<Object> getTodo();

    /**
     * 新增一个任务进行同步
     * @param parentPath
     * @return
     */
    Boolean addUploadTask(String parentPath);
}
