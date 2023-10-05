package cn.deystar.BaiduPan.Index.Service.impl;

import cn.deystar.BaiduPan.Core.Compress.CompressUploadService;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import org.springframework.stereotype.Service;
import cn.deystar.BaiduPan.Index.Service.IndexService;
import cn.deystar.BaiduPan.WebSocket.WebSocketController;
import cn.deystar.Util.Beans.OSstatus.OSstatusBean;
import cn.deystar.Util.HttpServerlet.Response.Success;
import cn.deystar.BaiduPan.Core.OS.OsService.OperatingTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    CompressUploadService compressUploadService;


    @Override
    public void sendMessage() {
        while (WebSocketController.hasOnline()) {
            try {
                Map<String, Object> data = new HashMap<>();
                //1. 获取系统运行情况
                OSstatusBean os = OperatingTemplate.getOsStatus();
                //2. 获取任务
                List<FileListBean> compressing = compressUploadService.getCompressing();
                List<FileListBean> uploading = compressUploadService.getUploading();
                WebSocketController controller = new WebSocketController();
                data.put("appearance", os);
                data.put("compressing", compressing);
                data.put("uploading", uploading);
                controller.sendToAll(new Success(data).toString());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
