package vip.yzxh.Util.ThreadLocal;

import cn.hutool.extra.spring.SpringUtil;
import vip.yzxh.Util.Beans.LoginStatus.LoginStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class LoginCache {


    private final static Map<String, LoginStatus> local = new ConcurrentHashMap<>();

    private static Lock lock = SpringUtil.getBean("loginLock");

    public static void login(LoginStatus loginStatus) {

        local.put(loginStatus.getSessionId(), loginStatus);

    }

    /**
     * 获取登录状态
     *
     * @param sessionId
     * @return
     */
    public static LoginStatus getLogin(String sessionId) {
        LoginStatus status = local.get(sessionId);
        return status;
    }

    /**
     * 登出
     *
     * @param sessionId
     */
    public static boolean logout(String sessionId) {
        LoginStatus status = local.get(sessionId);
        if (status.getSessionId() != null && !status.getSessionId().trim().isEmpty()
                && sessionId != null && !sessionId.trim().isEmpty() && sessionId.equals(status.getSessionId())) {
            return local.remove(sessionId).equals(status);
        }
        return false;
    }


    public static Boolean hasPerson() {

        return local.size() > 0;

    }

}
