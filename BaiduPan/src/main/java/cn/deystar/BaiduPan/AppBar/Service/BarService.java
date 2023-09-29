package cn.deystar.BaiduPan.AppBar.Service;

import cn.deystar.BaiduPan.AppBar.Entity.BarEntity;
import cn.deystar.BaiduPan.AppBar.Entity.BarList;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public interface BarService {
    BarList loadBar();

    BarEntity getBar(String api);
}
