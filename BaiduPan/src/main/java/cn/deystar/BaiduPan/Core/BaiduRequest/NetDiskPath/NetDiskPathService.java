package cn.deystar.BaiduPan.Core.BaiduRequest.NetDiskPath;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.deystar.Util.Util.SysConst;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Component
public class NetDiskPathService {

    @Resource
    private FileSettingService settingService;

    @Resource
    private UserRequestService userRequestService;


    /**
     * 新建目录
     */

    public boolean postCreateNetDisk(String path) {
        FileSetting setting = settingService.getSetting();
        String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=:token";
        if (setting != null && setting.isAllNotNull()){
            UserMsg userDetail = userRequestService.getBaiduUsInfo(setting.getToken().getAccessToken());
            if (userDetail == null || !userDetail.isAllNotNull()){
                return false;
            }
        }else{
            System.out.println("创建路径失败:setting错误");
            return false;
        }
        url = url.replace(":token",setting.getToken().getAccessToken());

//        Map<String,Object> requestBody = new HashMap<>();
//
//        requestBody.put("path",  URLEncoder.encode(path));
//        requestBody.put("isdir", SysConst.IS_DIR);
        String requestBody ="path="+URLEncoder.encode(path)+"&isdir="+SysConst.IS_DIR;

        HttpResponse response = HttpRequest.post(url).body(requestBody).execute();
        String bodyStr = response.body();
        JSONObject responseBody =  JSON.parseObject(bodyStr);
        if (responseBody != null) {
            if (responseBody.getInteger("errno") > 0) {
                System.out.println("申请在网盘中创建目录异常：\n" + responseBody.getString("errmsg"));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }






    /**
     * 看看有没有和这个目录
     *
     * @param path
     * @return
     */

    public boolean hasDir(String path) {
        FileSetting setting = settingService.getSetting();
        if (setting != null && setting.isAllNotNull()){
            //查看token是否失效，
            UserMsg userDetail = userRequestService.getBaiduUsInfo(setting.getToken().getAccessToken());
            if (userDetail == null || !userDetail.isAllNotNull()){
                return false;
            }
            String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=list&access_token=";
            url += setting.getToken().getAccessToken();

            HttpResponse response = HttpRequest.get(url).execute();
            String bodyStr = response.body();
            JSONObject bodyJson = JSON.parseObject(bodyStr);
            JSONArray jsonArray = bodyJson.getJSONArray("list");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonItem = (JSONObject) jsonArray.get(i);
                if (path == jsonItem.getString("path") &&
                        jsonItem.getInteger("isdir").intValue() == Integer.valueOf(SysConst.IS_DIR)) {
                    return true;
                }
            }
        }

        return false;
    }

}
