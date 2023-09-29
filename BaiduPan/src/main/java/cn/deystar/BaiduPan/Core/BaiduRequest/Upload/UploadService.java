package cn.deystar.BaiduPan.Core.BaiduRequest.Upload;

import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.Util.FileAndDigsted;
import cn.deystar.Util.Util.SysConst;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Component
public class UploadService {

    @Resource
    FileSettingService settingService;

    /**
     * 在网盘上创建这个文件
     *
     * @param path
     * @param size
     * @param isDir
     * @param blokList
     * @param uploadId
     * @return
     */

    public JSONObject upalodBefore(String path, Long size, Integer isDir,
                                     List<String> blokList, String uploadId) {
        FileSetting setting = settingService.getSetting();
        if (setting == null
                || setting.getToken() == null
                || setting.getToken().isAllNotNull())
        {
            return null;
        }
        String url = "pan.baidu.com/2.0/xpan/file?method=create&access_token=";
        url += setting.getToken();
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", path);
        requestBody.put("size", String.valueOf(size));
        requestBody.put("isdir", String.valueOf(isDir.intValue()));
        requestBody.put("block_list", blokList);
        requestBody.put("uploadid", uploadId);
        HttpResponse response = HttpRequest.post(url).body(requestBody.toString()).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }



    /**
     * 开始上传
     *
     * @param name            源文件名
     * @param parent          源文件父路径
     * @param size            源文件大小
     * @param fileAndDigested
     */
    public void goSend(String name, String parent, Long size, List<FileAndDigsted> fileAndDigested, String tempPath) {
        List<String> md5 = null;
        Long totalTempSize = 0L;
        for (FileAndDigsted item : fileAndDigested) {
            md5.add(item.getDigsted());
            totalTempSize += item.getSize();
        }
        System.out.println("文件：" + name + "大小为" + size + "缓存文件总大小" + totalTempSize);
        //预上传

        //3.发起预上传请求
        JSONObject responseJson = postNetDist(name, parent, md5, size.intValue());
        if (responseJson.getInteger("errno") != 0) {
            System.out.println("预上传错误：" + responseJson.toString());
        }

        String netDiskPath = responseJson.getString("path");
        String uploadid = responseJson.getString("uploadid");
        List<Integer> blokList = responseJson.getObject("block_list", ArrayList.class);
        //i. 发送分片文件
        for (Integer item : blokList) {
            FileAndDigsted tempMessage = fileAndDigested.get(item);
            File temFile = new File(tempMessage.getPath());
            JSONObject sendTempRes = postSendTemp(temFile, netDiskPath, uploadid);
            if (sendTempRes.getInteger("errno") != 0) {
                System.out.println("上传分片出现了问题:" + name);

            }
        }
        //i.在网盘上面创建这个文件，完成上传
        String netDiskDir = SysConst.getDefaultNetDiskDir();
        String filePath = netDiskDir + "/" + parent + "/" + name;
//        postCreateFile(filePath, size, SysConst.getIsNotDir(), md5, uploadid);
        //删除缓存文件，记录文件原名和改名后的文件名
        //i.删除缓存文件和目录l
        //目录
        File delDir = new File(tempPath);
        try {
            for (FileAndDigsted item : fileAndDigested) {
                File delFile = new File(item.getPath() + "/" + item.getName());
                boolean isDel = delFile.delete();
                System.out.println("上传任务结束,删除" + delFile.getName() + (isDel ? "成功" : "失败"));

            }
            //删除目录
            boolean isDel = delDir.delete();
            System.out.println("上传任务结束,删除" + delDir.getName() + (isDel ? "成功" : "失败"));

        } catch (SecurityException e) {
            System.out.println("上传任务结束,删除" + delDir.getName() + "删除失败，权限报错" + e.getMessage());

        }
    }

    /**
     * 预上传
     *
     * @return
     */
    public JSONObject postNetDist(String fileName, String parent, List<String> md5, Integer size) {
        //开始预上传
        String URL = "pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=";
        String token = SysConst.getAccessToken();

        URL += token;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", parent + "/" + fileName);
        requestBody.put("size", size);
        requestBody.put("isdir", SysConst.getIsNotDir());
        requestBody.put("block_list", md5);
        requestBody.put("autoinit", 1);
        HttpResponse response = HttpRequest.post(URL).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }

    /**
     * 发送切片文件
     *
     * @param file
     * @param path
     * @param uploadId
     */
    public JSONObject postSendTemp(File file, String path, String uploadId) {
        String url = "d.pcs.baidu.com/rest/2.0/pcs/superfile2?method=upload&access_token=";
        String token = SysConst.getAccessToken();
        if (token == null) {

            token = SysConst.getAccessToken();
        }
        url += token;
        url += "&type=tmpfile&path=" + path + "&uploadid=" + uploadId + "&partsq=0";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("file", file);
        HttpResponse response = HttpRequest.post(url).body(jsonObject.toString()).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }




}
