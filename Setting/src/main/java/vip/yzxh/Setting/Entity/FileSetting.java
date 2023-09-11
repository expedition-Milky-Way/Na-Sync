package vip.yzxh.Setting.Entity;


import com.alibaba.fastjson.JSON;

/**
 * @author 杨 名 (字 露煊)
 */


public class FileSetting {


    private String appId;

    private String secretKey;

    private String signKey;

    private String appKey;

    private String password;

    private String path;

    private String cachePath;

    private String dateTime;

    private Integer taskNum; //最大并行任务数量

    private Long oneFileSize;

    private Integer version;


    /**
     * 有空返回true 否则返回false
     *
     * @return
     */
    public Boolean isEmpty() {
        if (this.appId == null || this.secretKey == null
                || this.secretKey == null || this.signKey == null
                || this.appKey == null || this.password == null ||
                this.path == null || this.cachePath == null ||
                this.dateTime == null) {
            return true;
        } else {
            return false;
        }

    }

    public FileSetting() {

    }

    public FileSetting(String appId, String secretKey, String signKey,
                       String appKey, String password, String path, String cachePath,
                       String dateTime, Integer taskNum, Integer version) {

        this.appId = appId;
        this.secretKey = secretKey;
        this.signKey = signKey;
        this.appKey = appKey;
        this.password = password;
        this.path = path;
        this.cachePath = cachePath;
        this.dateTime = dateTime;
        this.taskNum = taskNum;
        this.version = version;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Long getOneFileSize() {
        return oneFileSize;
    }

    public void setOneFileSize(Long oneFileSize) {
        this.oneFileSize = oneFileSize;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
