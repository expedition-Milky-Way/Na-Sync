package cn.deystar.BaiduPan.Core.BaiduRequest.Upload;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.*;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.deystar.Util.Util.FileAndDigsted;
import cn.deystar.Util.Util.FileToChar;
import cn.deystar.Util.Util.SysConst;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.chrono.MinguoDate;
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

    /**
     * 预上传
     *
     * @param bean
     * @return
     */
    public ReadyToUploadResponse readyToUpload(ChunkBean bean) {
        FileSetting setting = settingService.getSetting();
        ReadyToUploadResponse response = null;
        if (setting != null && !setting.isAllNotNull()) {
            String token = setting.getToken().getAccessToken();
            UserMsg userMsg = userRequestService.getBaiduUsInfo(token);
            if (userMsg != null) {
                String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=:token";
                Map<String, Object> body = new HashMap<>();
                body.put("size", bean.getSize());
                body.put("isdir", 0);
                String netDiskPath = baiduPath + bean.getPath() + bean.getFileName();
                body.put("path", netDiskPath);
                body.put("block_list", bean.getBlockList());
                body.put("autoint", 1);
                String respStr = HttpRequest.post(url).body(body.toString()).execute().body();
                response = JSONObject.parseObject(respStr, ReadyToUploadResponse.class);
            }
        }
        return response;
    }

    /**
     * 上传分片
     *
     * @param bean
     * @param readyResponse
     * @return
     */
    public Boolean postSendFile(TempBean bean, Integer beanIndex, ReadyToUploadResponse readyResponse) {
        String url = "http://d.pcs.baidu.com/rest/2.0/pcs/superfile2?method&upload&access_token=:token" +
                "&path=:path&type=tempfile&uploadid=:uploadId&partseq=:index";
        FileSetting setting = settingService.getSetting();
        if (setting != null && !setting.isAllNotNull()) return false;
        UserMsg userMsg = userRequestService.getBaiduUsInfo(setting.getToken().getAccessToken());
        if (userMsg == null || !userMsg.isAllNotNull()) return false;

        //Token校验有效
        url = url.replace(":token", setting.getToken().getAccessToken())
                .replace(":path", readyResponse.getPath())
                .replace(":uploadId", readyResponse.getUploadId() + "")
                .replace(":index", beanIndex + "");
        try {
            char[] file = FileToChar.readFileToCharArray(bean.getChunk().getPath());
            Map<String, Object> body = new HashMap<>();
            body.put("file", file);
            String responseString = HttpRequest.post(url).body(String.valueOf(body)).execute().body();
            TempUploadResponse response = JSONObject.parseObject(responseString, TempUploadResponse.class);
            if (response == null || response.getErrno() != 0) {
                System.out.println("上传任务出错");
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * when all chunk was upload complete. invoke this method can be created the file on netdisk
     *
     * @param chunkBean
     * @return
     */
    public CreateFileResponse createFile(ChunkBean chunkBean, ReadyToUploadResponse readyToUploadResponse) {
        if (readyToUploadResponse == null && chunkBean == null) return null;
        String url = "http://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=:token";
        FileSetting setting = settingService.getSetting();
        if (setting == null || !setting.isAllNotNull()) return null;
        url = url.replace(":token", setting.getToken().getAccessToken());
        Map<String, Object> body = new HashMap<>();
        body.put("path", readyToUploadResponse.getPath());
        body.put("size", chunkBean.getSize());
        body.put("isdir", BaiduConst.DIRECTORY_NO);
        body.put("block_list", chunkBean.getBlockList());
        body.put("uploadid", readyToUploadResponse.getUploadId());
        body.put("rtype", BaiduConst.RE_TYPE_DO_NOT_ANYTHING);

        String httpResponse = HttpRequest.post(url).execute().body();
        return JSONObject.parseObject(httpResponse, CreateFileResponse.class);
    }

    /**
     * @param bean FileListBean
     * @return StepByUploadResponse`s instance
     */
    public StepByUploadResponse oneStepUpload(FileListBean bean) {
        String url = "http://d.pcs.baidu.com/rest/2.0/pcs/file?method=upload&access_token=:token" +
                "path=:path";
        FileSetting setting = settingService.getSetting();
        if (setting == null || !setting.isAllNotNull()) return null;
        String netDiskPath = baiduPath;
        String[] parent = bean.getParent().split("/");
        netDiskPath += parent[parent.length - 1] + "/";
        String token = setting.getToken().getAccessToken();
        url = url.replace(":token", token);
        url = url.replace(":path", netDiskPath);

        //上传
        HttpResponse httpResponse = null;
        StepByUploadResponse response = null;
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
        return response;
    }


}
