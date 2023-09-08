package vip.yzxh.Util;


/**
 * @author 杨名 （字 露煊） YeungLuhyun
 * 系统常量
 **/
public class SysConst {

    /**
     * token
     */
    private static String ACCESS_TOKEN ;

    private static String REFRESH_TOKEN ;

    private static Long EXPIRE_TIME ;

    private static String DEVICE_CODE ;

    private static String CACHE_PATH ;

    private static String PATH;

    private static String TEMP_PATH = "/temp/";

    private static Long MIN_SIZE = 4194304L;

    private static Long MAX_SIZE ;

    private static Long MAX_TEMP_SIZE ;

    //百度网盘默认上传的文件夹
    private static String DEFAULT_NET_DISK_DIR = "/nasBackUpByYZXH";
    //是文件目录
    private static Integer IS_DIR = 1;
    //不是文件目录
    private static Integer IS_NOT_DIR = 0;
    //默认文件夹大小
    private static Integer DEFAULT_DIR_SIZE = 0;




    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static void setAccessToken(String token) {

        ACCESS_TOKEN = token;


    }

    public static String getRefreshToken() {
        return REFRESH_TOKEN;
    }

    public static void setRefreshToken(String refreshToken) {

        REFRESH_TOKEN = refreshToken;


    }

    public static Long getExpireTime() {
        return EXPIRE_TIME;
    }

    public static void setExpireTime(Long expireTime) {

        EXPIRE_TIME = expireTime;


    }

    public static String getDeviceCode() {
        return DEVICE_CODE;
    }

    public static void setDeviceCode(String deviceCode) {
        DEVICE_CODE = deviceCode;
    }

    public static String getCachePath() {
        return CACHE_PATH;
    }

    public static void setCachePath(String cachePath) {

        CACHE_PATH = cachePath;


    }

    public static String getPATH() {
        return PATH;
    }

    public static void setPATH(String path) {

        PATH = path;

    }

    public static String getTempPath() {
        return TEMP_PATH;
    }


    public static Long getMinSize() {
        return MIN_SIZE;
    }

    public static void setMinSize(Long minSize) {
        MIN_SIZE = minSize;
    }

    public static Long getMaxSize() {
        return MAX_SIZE;
    }

    public static void setMaxSize(Long maxSize) {
        MAX_SIZE = maxSize;
    }

    public static Long getMaxTempSize() {
        return MAX_TEMP_SIZE;
    }

    public static void setMaxTempSize(Long maxTempSize) {
        MAX_TEMP_SIZE = maxTempSize;
    }

    public static Integer getIsDir() {
        return IS_DIR;
    }

    public static void setIsDir(Integer isDir) {
        IS_DIR = isDir;
    }

    public static Integer getIsNotDir() {
        return IS_NOT_DIR;
    }

    public static void setIsNotDir(Integer isNotDir) {
        IS_NOT_DIR = isNotDir;
    }

    public static Integer getDefaultDirSize() {
        return DEFAULT_DIR_SIZE;
    }

    public static void setDefaultDirSize(Integer defaultDirSize) {
        DEFAULT_DIR_SIZE = defaultDirSize;
    }

    public static String getDefaultNetDiskDir() {
        return DEFAULT_NET_DISK_DIR;
    }


}
