package cn.deystar.BaiduPan.Core.Compress;

import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface CompressService {




    void addTask(ZipAbstract zipService);

    List<FileListBean> getCompressing();
}
