package cn.deystar.BaiduPan.Core.BaiduRequest.Upload;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.Setting.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.*;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.deystar.Util.Util.FileToChar;
import cn.deystar.Util.Util.FormByUrlEncodeUtil;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Component
public class UploadService {

    @Resource
    FileSettingService settingService;
    @Resource
    UserRequestService userRequestService;
    @Value("${baidu-netdisk.path}")
    private String baiduPath;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 预上传
     *
     * @param bean
     * @return
     */
    public ReadyToUploadResponse readyToUpload(ChunkBean bean) {
        TokenResponse tokenDetail = settingService.getToken();
        String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=:token";
        url = url.replace(":token", tokenDetail.getAccessToken());

        Map<String, String> body = new HashMap<>();
        body.put("path", URLEncoder.encode(bean.getOriginFileName()));
        body.put("isdir", BaiduConst.NOT_DIR_STR);
        body.put("size", bean.getSize() + "");
        body.put("autoinit", 1 + "");
        body.put("block_list", bean.getBlockListString());
        body.put("rtype", BaiduConst.RE_TYPE_COVER + ""); //只要冲突就覆盖
        String respStr = HttpRequest.post(url)
                .body(FormByUrlEncodeUtil.wwwFormUrlEncode(body))
                .execute()
                .body();
        return JSONObject.parseObject(respStr, ReadyToUploadResponse.class);


    }

    /**
     * 上传分片
     *
     * @param bean          分片对象
     * @param path          远程文件路径
     * @param readyResponse 预上传接口的回调
     * @return
     */
    public Boolean postSendFile(TempBean bean, String path, Integer beanIndex, ReadyToUploadResponse readyResponse) {
        String url = "https://d.pcs.baidu.com/rest/2.0/pcs/superfile2?method=upload&access_token=:token" +
                "&path=:path&type=tempfile&uploadid=:uploadId&partseq=:index";
        TokenResponse tokenResponse = settingService.getToken();
        if (tokenResponse == null) {
            System.out.println("token为空");
            return false;
        }
        //Token校验有效
        url = url.replace(":token", tokenResponse.getAccessToken())
                .replace(":path", URLEncoder.encode(path))
                .replace(":uploadId", readyResponse.getUploadId())
                .replace(":index", beanIndex + "");


        Map<String, Object> body = new HashMap<>();
        body.put("file", bean.getChunk());
        String responseString = null;
        try {
            responseString = HttpRequest.post(url).form(body).execute().body();
            TempUploadResponse response = JSONObject.parseObject(responseString, TempUploadResponse.class);
            return response != null;
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(responseString);
            return false;
        }

    }

    /**
     * when all chunk was upload complete. invoke this method can be created the file on netdisk
     *
     * @param chunkBean
     * @return
     */
    public CreateFileResponse createFile(ChunkBean chunkBean, ReadyToUploadResponse readyToUploadResponse) {
        if (readyToUploadResponse == null && chunkBean == null) return null;
        String url = "https://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=:token";
        TokenResponse tokenDetail = settingService.getToken();

        url = url.replace(":token", tokenDetail.getAccessToken());


        Map<String, String> body = new HashMap<>();
        body.put("path", URLEncoder.encode(chunkBean.getOriginFileName()));
        body.put("size", chunkBean.getSize() + "");
        body.put("isdir", BaiduConst.NOT_DIR_STR);
        body.put("block_list", chunkBean.getBlockListString());
        body.put("uploadid", readyToUploadResponse.getUploadId());
        body.put("rtype", BaiduConst.RE_TYPE_COVER + "");
        String httpResponse = null;
        try {
            httpResponse = HttpRequest.post(url)
                    .body(FormByUrlEncodeUtil.wwwFormUrlEncode(body))
                    .execute()
                    .body();
            return JSONObject.parseObject(httpResponse, CreateFileResponse.class);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(httpResponse);

            return null;
        }
    }

    /**
     * @param bean FileListBean
     * @return StepByUploadResponse`s instance
     */
    public StepByUploadResponse oneStepUpload(FileListBean bean) {
        String url = "http://d.pcs.baidu.com/rest/2.0/pcs/file?method=upload&access_token=:token" +
                "path=:path";
        StepByUploadResponse response = null;

        String netDiskPath = baiduPath;
        String[] parent = bean.getParent().split("/");
        netDiskPath += parent[parent.length - 1] + "/";
        TokenResponse tokenDetail = settingService.getToken();
        if (tokenDetail != null && tokenDetail.isAllNotNull()) {
            url = url.replace(":token", tokenDetail.getAccessToken());
            url = url.replace(":path", netDiskPath);
            //上传
            HttpResponse httpResponse = null;

            try {
                //将文件转化为字符串
                char[] file = FileToChar.readFileToCharArray(bean.getZipName());

                Map<String, Object> body = new HashMap<>();
                body.put("file", file);
                httpResponse = HttpRequest.post(url).body(body.toString()).execute();
                response = JSONObject.parseObject(httpResponse.body(), StepByUploadResponse.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }


}
