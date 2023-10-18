package cn.deystar.BaiduPan.AppBar.Controller;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.deystar.BaiduPan.AppBar.Service.BarService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.deystar.Util.HttpServerlet.Response.ResponseData;
import cn.deystar.Util.HttpServerlet.Response.Success;

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
    FileSettingService settingService;
    @Resource
    UserRequestService userRequestService;

    @GetMapping()
    @ResponseBody
    public ResponseData barData() {
        TokenResponse tokenDetail = settingService.getToken();
        Map<String, Object> result = new HashMap<>();
        if (tokenDetail!= null && tokenDetail.isAllNotNull()) {
            UserMsg userMsg = (UserMsg) userRequestService.getBaiduUsInfo(tokenDetail.getAccessToken());
            result.put("user", userMsg);
        }
        result.put("bar", barService.loadBar());
        return new Success(result);
    }

}
