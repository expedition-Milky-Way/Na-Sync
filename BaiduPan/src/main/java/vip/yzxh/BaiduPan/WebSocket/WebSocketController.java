package vip.yzxh.BaiduPan.WebSocket;

import org.springframework.stereotype.Component;


import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Component
@ServerEndpoint("/taskstatus/{sid}")
public class WebSocketController {


    private final static AtomicInteger onlineCount = new AtomicInteger(0);

    private static final CopyOnWriteArraySet<WebSocketController> webSocketControllers = new CopyOnWriteArraySet<>();


    private Session session;

    private String sid = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketControllers.add(this);
        onlineCount.incrementAndGet();
        System.out.println(sid + "已订阅，当前在线人数：" + onlineCount.get());
        this.sid = sid;

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketControllers.remove(this);
        onlineCount.decrementAndGet();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + onlineCount.get());

    }

    public void onClose(String sid) {
        if (sid == null || sid.trim().isEmpty()) return;
        for (WebSocketController socket : webSocketControllers) {
            //从set中删除
            if (socket.sid.equals(sid)) {
                webSocketControllers.remove(socket);
                onlineCount.decrementAndGet();           //在线数减1
                System.out.println("有一连接关闭！当前在线人数为" + onlineCount.get());
            }
        }
    }

    /**
     * 接收心跳信息
     */
    @OnMessage
    public void heartbeat(String message, Session session) {

    }

    public void sendToAll(String message){
        for (WebSocketController c : webSocketControllers){
            try {
                c.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static Boolean hasOnline() {
        return onlineCount.get() > 0;
    }

}
