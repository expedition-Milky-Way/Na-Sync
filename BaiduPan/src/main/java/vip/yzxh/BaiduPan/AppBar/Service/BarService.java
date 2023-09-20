package vip.yzxh.BaiduPan.AppBar.Service;

import vip.yzxh.BaiduPan.AppBar.Entity.BarEntity;
import vip.yzxh.BaiduPan.AppBar.Entity.BarList;

import java.util.List;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public interface BarService {
    BarList loadBar();

    BarEntity getBar(String api);
}
