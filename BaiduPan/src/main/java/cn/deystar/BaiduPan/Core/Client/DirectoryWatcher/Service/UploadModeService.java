package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface UploadModeService {
    boolean add(String path);

    boolean remove(String path);
}
