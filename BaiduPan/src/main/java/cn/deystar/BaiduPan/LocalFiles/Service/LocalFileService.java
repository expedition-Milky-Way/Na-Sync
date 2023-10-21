package cn.deystar.BaiduPan.LocalFiles.Service;

import cn.deystar.Util.Beans.LocalFiles.LocalFile;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface LocalFileService {

    List<List<LocalFile>> listAll(String path);
}
