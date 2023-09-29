package cn.deystar.BaiduPan.Core.BaiduRequest.NetDiskPath;

import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.Util.SysConst;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Component
public class NetDiskPathService {

    @Resource
    private FileSettingService settingService;



    /**
     * 新建目录
     */

    public boolean postCreateNetDisk(String path) {
        FileSetting setting = settingService.getSetting();
        String url = "pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=";
        JSONObject responseBody = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", path);
        requestBody.put("size", SysConst.getDefaultDirSize());
        requestBody.put("isdir", SysConst.getIsDir());
        HttpResponse response = HttpRequest.post(url).body(requestBody.toString()).execute();
        String bodyStr = response.body();
        responseBody = JSON.parseObject(bodyStr);
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
        String url = "pan.baidu.com/rest/2.0/xpan/file?method=list&access_token=";
        String token = SysConst.getAccessToken();
        if (token == null) {

            token = SysConst.getAccessToken();
        }
        url += token;
        HttpResponse response = HttpRequest.get(url).execute();
        String bodyStr = response.body();
        JSONObject bodyJson = JSON.parseObject(bodyStr);
        JSONArray jsonArray = bodyJson.getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonItem = (JSONObject) jsonArray.get(i);
            if (path == jsonItem.getString("path") &&
                    jsonItem.getInteger("isdir").intValue() == SysConst.getIsDir().intValue()) {
                return true;
            }
        }
        return false;
    }

}
