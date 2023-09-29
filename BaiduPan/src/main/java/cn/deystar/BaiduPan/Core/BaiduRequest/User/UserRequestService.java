package cn.deystar.BaiduPan.Core.BaiduRequest.User;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Component
public class UserRequestService {




    /**
     * 获取用户信息
     */

    public UserMsg getBaiduUsInfo(String accessToken) {
        String URL = "pan.baidu.com/rest/2.0/xpan/nas?method=uinfo" +
                "&access_token=:token";

        if (accessToken == null || accessToken.trim().isEmpty()) {
            return null;
        }
        URL = URL.replace(":token", accessToken);
        HttpResponse response = HttpRequest.get(URL).execute();
        String bodyStr = response.body();
        JSONObject body = JSON.parseObject(bodyStr);

        if (body != null && body.getInteger(BaiduConst.RESP_ERROR_NO) == 0) {
            return JSONObject.parseObject(body.toJSONString(), UserMsg.class);
        }
        return null;
    }
}
