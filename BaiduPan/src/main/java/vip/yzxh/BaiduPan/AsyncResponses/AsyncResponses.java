package vip.yzxh.BaiduPan.AsyncResponses;

import vip.yzxh.BaiduPan.Admin.Controller.AccreditResponser;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Author YeungLuhyun
 **/
public class AsyncResponses {


    /**
     * 授权请求与回调
     * accredit`s request and result`s return
     */
    private static final Queue<AccreditResponser> accreditQueue = new LinkedBlockingDeque<>();


    public static AccreditResponser getAccreditQueue() {
        synchronized (accreditQueue) {
            if (!accreditQueue.isEmpty()) {
                return accreditQueue.poll();
            }
            return null;
        }

    }

    public static void setAccreditQueue(AccreditResponser accreditResponser) {
        synchronized (accreditQueue) {
            accreditQueue.offer(accreditResponser);
        }
    }


}
