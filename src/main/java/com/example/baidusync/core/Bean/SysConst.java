package com.example.baidusync.core.Bean;

/**
 * @author 杨名 （字 露煊） YeungLuhyun
 * 系统常量
 **/
public class SysConst {

    /**
     * token
     */
    private static String ACCESS_TOKEN = null;

    private static String REFRESH_TOKEN = null;

    private static String EXPIRE_TIME = null;

    private static String DEVICE_CODE = null;

    private static String CACHE_PATH = null;

    private static String PATH = null;

    private static String TEMP_PATH = "/temp/";


    public static String getAccessT0ken() {
        return ACCESS_TOKEN;
    }

    public static void setAccessTOken(String token) {
        synchronized (ACCESS_TOKEN) {
            ACCESS_TOKEN = token;
        }

    }

    public static String getRefreshToken() {
        return REFRESH_TOKEN;
    }

    public static void setRefreshToken(String refreshToken) {
        synchronized (REFRESH_TOKEN) {
            REFRESH_TOKEN = refreshToken;
        }

    }

    public static String getExpireTime() {
        return EXPIRE_TIME;
    }

    public static void setExpireTime(String expireTime) {
        synchronized (EXPIRE_TIME) {
            EXPIRE_TIME = expireTime;
        }

    }

    public static String getDeviceCode() {
        return DEVICE_CODE;
    }

    public static void setDeviceCode(String deviceCode) {
        synchronized (DEVICE_CODE) {
            DEVICE_CODE = deviceCode;
        }

    }

    public static String getCachePath() {
        return CACHE_PATH;
    }

    public static void setCachePath(String cachePath) {
        synchronized (CACHE_PATH) {
            CACHE_PATH = cachePath;
        }

    }

    public static String getPATH() {
        return PATH;
    }

    public static void setPATH(String path) {
        synchronized (PATH) {
            PATH = path;
        }
    }

    public static String getTempPath() {
        return TEMP_PATH;
    }

    public static void setTempPath(String tempPath) {
        synchronized (TEMP_PATH){
            TEMP_PATH = tempPath;
        }

    }
}
