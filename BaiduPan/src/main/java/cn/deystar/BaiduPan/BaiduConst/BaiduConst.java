package cn.deystar.BaiduPan.BaiduConst;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.util.ResourceUtils;
import cn.deystar.Util.BaiduPanResponse.UserMsg;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Author YeungLuhyun
 * 有关于百度网盘的所有常量
 * all const value about of baidu net storage
 **/
public class BaiduConst {

    private static UserMsg userMsg;

    /**
     * 符合单步上传的文件大小
     */
    public static final Long STEP_BY_UPLOAD_SIZE = 4194304L;

    public static final String RESP_DEVICE_CODE = "device_code";

    public static final String RESP_USER_CODE = "user_code";

    public static final String RESP_ERROR_NO = "errno";

    public static final String RESP_INTERVAL = "interval";

    public static final String RESP_BAIDU_COOKIE = "BAIDUID";

    public static final Integer RE_TYPE_COVER = 3;

    public static final Integer RE_TYPE_COVER_BY_PATH = 2;

    public static final Integer RE_TYPE_RENAME = 1;

    public static final Integer RE_TYPE_DO_NOT_ANYTHING = 0;

    public static final Integer DIRECTORY_NO = 0;

    public static final Integer DIRECTORY_YES = 1;

    public static String RESP_BAIDU_HTML() {
        String path = "";
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/BaiduResp/";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public static synchronized void setUserMsg(UserMsg val) {
        userMsg = val;
    }

    public static synchronized UserMsg getUserMsg() {
        return userMsg;
    }


}
