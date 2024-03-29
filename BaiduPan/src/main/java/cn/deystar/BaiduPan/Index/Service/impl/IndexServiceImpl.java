package cn.deystar.BaiduPan.Index.Service.impl;

import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.BaiduPan.Core.Upload.UploadTaskService;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import org.springframework.stereotype.Service;
import cn.deystar.BaiduPan.Index.Service.IndexService;
import cn.deystar.BaiduPan.WebSocket.WebSocketController;
import cn.deystar.Util.Beans.OSstatus.OSstatusBean;
import cn.deystar.Util.HttpServerlet.Response.Success;
import cn.deystar.BaiduPan.Core.Client.System.OperatingTemplate;

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
    UploadTaskService uploadTaskService;

    @Resource
    CompressService compressService;


    @Override
    public void sendMessage() {
        while (WebSocketController.hasOnline()) {
            try {
                Map<String, Object> data = new HashMap<>();
                //1. 获取系统运行情况
                OSstatusBean os = OperatingTemplate.getOsStatus();
                //2. 获取任务
                List<FileListBean> compressing = compressService.getCompressing();
                List<FileListBean> uploading = uploadTaskService.getTodo();

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
