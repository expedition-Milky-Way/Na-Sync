package cn.deystar.BaiduPan.Core.OS.watchFile.Service;

import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface WatchFileService {

    /**
     * 获取等待压缩
     * @return
     */
    ZipAbstract getWaitOfCompress();

    Boolean isEmpty();
}
