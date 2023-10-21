package cn.deystar.BaiduPan.Core.BaiduRequest.NetDiskPath;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.Enums.FileManageType;
import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.Util.BaiduPanResponse.DeleteResponse;
import cn.deystar.Util.BaiduPanResponse.OriginFileResponse;
import cn.deystar.Setting.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import cn.deystar.Util.Util.FormByUrlEncodeUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * 百度网盘频率控制状态码
     */
    private static final int ERR_LIMIT = 31034;

    /**
     * 没有该文件或目录
     */
    private static final int NOT_EXCITES = 31066;

    /**
     * 新建目录
     */

    public boolean postCreateNetDisk(String path) {
        TokenResponse tokenResponse = settingService.getToken();
        String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=:token";


        url = url.replace(":token", tokenResponse.getAccessToken());

        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("path", URLEncoder.encode(path));
        requestBody.put("isdir", BaiduConst.IS_DIR_STR);


        HttpResponse response = HttpRequest.post(url)
                .body(FormByUrlEncodeUtil.wwwFormUrlEncode(requestBody))
                .execute();
        String bodyStr = response.body();
        JSONObject responseBody = JSON.parseObject(bodyStr);
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
        List<OriginFileResponse> originFileList = this.listAll(path);
        if (originFileList.isEmpty()) return false;
        for (OriginFileResponse fileBean : originFileList){
            if (fileBean.getIsDir()!= null && fileBean.getIsDir().equals(BaiduConst.IS_DIR_INT) &&
            fileBean.getPath()!= null && fileBean.getPath().equals(path)){
                return true;
            }
        }
        return false;
    }

    public List<OriginFileResponse> listAll(String path){
        String url = "http://pan.baidu.com/rest/2.0/xpan/multimedia?method=listall&access_token=:token&path=:path&web=1";
        TokenResponse tokenDetail = settingService.getToken();
        List<OriginFileResponse> result = new ArrayList<>();
        if (tokenDetail!= null && tokenDetail.isAllNotNull()){
            url = url.replace(":token",tokenDetail.getAccessToken());
            url = url.replace(":path",URLEncoder.encode(path));
            String respStr = HttpRequest.get(url).execute().body();
            JSONObject respJson = JSONObject.parseObject(respStr);
            System.out.println(respJson);
            if (respJson == null) return result;

            Integer errno = respJson.getInteger("errno");
            if (errno!= null){
                switch (errno){
                    case ERR_LIMIT:{
                        System.out.println("触发限流");
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        this.listAll(path);
                    }
                    case NOT_EXCITES:{
                        System.out.println("文件不存在");
                        return result;
                    }

                }
            }

            JSONArray array = respJson.getJSONArray("list");
            Integer index =0;
            while (index< array.size()){
                result.add(JSONObject.parseObject(((JSONObject)array.get(index)).toJSONString(), OriginFileResponse.class));
                index++;
            }

        }
        return result;
    }

    /**
     * 删除文件
     * @param path
     * @return
     */
    public DeleteResponse delete(String...path){
        TokenResponse tokenDetail = settingService.getToken();
        String url = fileManageUrl(tokenDetail.getAccessToken(),FileManageType.delete);
        Map<String,String> body = new HashMap<>();
        body.put("async","0");
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0;i < path.length;i++){
            builder.append("\"").append(URLEncoder.encode(path[i])).append("\"");
            if (i < path.length-1){
                builder.append(",");
            }
        }
        builder.append("]");
        body.put("filelist",builder.toString());
        String bodyString = HttpRequest.post(url)
                .body(FormByUrlEncodeUtil.wwwFormUrlEncode(body))
                .execute()
                .body();
        return JSONObject.parseObject(bodyString, DeleteResponse.class);
    }

    /**
     * 构建管理文件的url
     * @param token token
     * @param type FileManageType
     * @return url:string | null
     */
    private String fileManageUrl(String token, FileManageType type){
        String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=filemanager&access_token=:token&opera=:opera";
        if (token!= null && !token.trim().isEmpty() && type!= null){
            return url.replace(":token",token).replace(":opera",type.name());
        }
        return null;
    }

}
