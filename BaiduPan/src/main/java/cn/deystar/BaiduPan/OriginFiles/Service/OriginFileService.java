package cn.deystar.BaiduPan.OriginFiles.Service;

import cn.deystar.BaiduPan.OriginFiles.Bean.OriginFiles;
import cn.deystar.BaiduPan.OriginFiles.Enums.Sort;
import cn.deystar.Util.BaiduPanResponse.OriginFileResponse;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface OriginFileService {
    List<List<OriginFiles>> listAll(String path,Sort sort);
}
