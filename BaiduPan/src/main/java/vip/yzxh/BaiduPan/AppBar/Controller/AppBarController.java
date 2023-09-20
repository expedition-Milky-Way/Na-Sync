package vip.yzxh.BaiduPan.AppBar.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.yzxh.BaiduPan.AppBar.Service.BarService;
import vip.yzxh.Util.BaiduPanResponse.UserMsg;
import vip.yzxh.BaiduPan.NetDisk.RequestNetDiskService;
import vip.yzxh.Util.HttpServerlet.Response.ResponseData;
import vip.yzxh.Util.HttpServerlet.Response.Success;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * 软件侧边导航栏
 */
@Controller
@RequestMapping("/user")
public class AppBarController {

    @Resource
    BarService barService;

    @Resource
    RequestNetDiskService diskService;
    @GetMapping()
    @ResponseBody
    public ResponseData barData() {
        UserMsg userMsg = (UserMsg) diskService.getBaiduUsInfo();
        Map<String,Object> result = new HashMap<>();
        result.put("user",userMsg);
        result.put("bar",barService.loadBar());
        return new Success(result);
    }
}
