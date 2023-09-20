package vip.yzxh.BaiduPan.AppBar.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import vip.yzxh.BaiduPan.AppBar.Entity.BarEntity;
import vip.yzxh.BaiduPan.AppBar.Entity.BarList;
import vip.yzxh.BaiduPan.AppBar.Service.BarService;
import vip.yzxh.Util.Util.ConfigFileTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Service
public class BarServiceImpl implements BarService {

    static String path = "";

    static {

        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/static/bar.json";
            if (path.contains("\\")) path = path.replace("\\", "/");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    volatile static List<BarEntity> barEntity;


    @Override
    public BarList loadBar() {

        if (barEntity == null) {
            BarList object = JSONObject.parseObject(ConfigFileTemplate.readFile(path), BarList.class);
            return object;
        }
        return null;
    }


    @Override
    public BarEntity getBar(String api) {
        BarList barList = this.loadBar();
        BarEntity resultBar = null;
        if (barList != null && barList.getBar() != null) {
            for (BarEntity bar : barList.getBar()) {
                if (bar.getUrl() == null || bar.getUrl().trim().isEmpty() || !bar.getUrl().equals(api)) {
                    if (bar.getIsRoot()) {
                        List<BarEntity> subBars = bar.getSubBar();
                        for (BarEntity subBar : subBars) {
                            if (subBar.getUrl().equals(api)) {
                                return subBar;
                            }
                        }
                    }
                } else {
                    resultBar = bar;
                    break;
                }
            }
        }
        return resultBar;

    }

}
