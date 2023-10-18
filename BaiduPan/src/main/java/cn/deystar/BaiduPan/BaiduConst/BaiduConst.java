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

    /**
     * 覆盖，需要与预上传precreate接口中的rtype保持一致
     */
    public static final Integer RE_TYPE_COVER = 3;

    /**
     * path冲突且block_list不同才重命名
     */
    public static final Integer RE_TYPE_COVER_BY_PATH = 2;

    /**
     * 只要path冲突即重命名
     */
    public static final Integer RE_TYPE_RENAME = 1;

    /**
     * 返回文件冲突错误
     */
    public static final Integer RE_TYPE_DO_NOT_ANYTHING = 0;



    //是文件目录
    public static final String IS_DIR_STR = "1";
    public static final Integer IS_DIR_INT = 1;
    //不是文件目录
    public static String NOT_DIR_STR = "0";

    public static Integer NOT_DIR_INT = 0;



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
