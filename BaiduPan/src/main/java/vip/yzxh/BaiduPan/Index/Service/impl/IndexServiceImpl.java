package vip.yzxh.BaiduPan.Index.Service.impl;

import org.springframework.stereotype.Service;
import vip.yzxh.BaiduPan.Index.Service.IndexService;
import vip.yzxh.BaiduPan.WebSocket.WebSocketController;
import vip.yzxh.Util.Beans.OSstatus.OSstatusBean;
import vip.yzxh.Util.HttpServerlet.Response.Success;
import vip.yzxh.Util.OS.OperatingTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Service
public class IndexServiceImpl implements IndexService {



    @Override
    public Object sendMessage() {
        while (WebSocketController.hasOnline()) {
            try {
                Map<String, Object> data = new HashMap<>();
                //1. 获取系统运行情况
                OSstatusBean os = OperatingTemplate.getOsStatus();
                WebSocketController controller = new WebSocketController();
                data.put("appearance", os);

                controller.sendToAll(new Success(data).toString());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
