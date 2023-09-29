package cn.deystar.Setting.Entity;


import cn.deystar.Util.ScanAndZip.Util.Const.SystemEnums;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;

import java.util.Objects;

/**
 * @author 杨 名 (字 露煊)
 */


public class FileSetting {

    @JSONField(serialize = false)
    public static final Integer LISTEN_PATH = 0; //监控路径是否变动
    @JSONField(serialize = false)
    public static final Integer TIMING = 1; //定时


    private String appId;

    private String secretKey;

    private String signKey;

    private String appKey;

    private String password;

    private String path; //需要被压缩的路径

    private String cachePath; //缓存路径

    private String dateTime; // 进行上传任务的开始时间

    private Integer taskNum; //上传最大并行任务数量

    private Long oneFileSize; // 单个文件最大大小

    private Integer version; //版本号

    private String uri; //回调url

    private TokenResponse token;
    private Integer compressThread;

    private Integer isListen;

    private Boolean pathEncryption;

    private Integer system;

    @JSONField(serialize = false)
    private SystemEnums systemEnums;

    public FileSetting() {

    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public FileSetting(String appId, String secretKey,
                       String signKey, String appKey, String password,
                       String path, String cachePath,
                       Integer taskNum, Long oneFileSize, Integer version,
                       String uri, TokenResponse token, Integer compressThread,
                       Integer isListen, SystemEnums systemEnums) {
        this.appId = appId;
        this.secretKey = secretKey;
        this.signKey = signKey;
        this.appKey = appKey;
        this.password = password;
        this.path = path;
        this.cachePath = cachePath;
        this.taskNum = taskNum;
        this.oneFileSize = oneFileSize;
        this.version = version;
        this.uri = uri;
        this.token = token;
        this.compressThread = compressThread;
        this.isListen = isListen;
        this.systemEnums = systemEnums;
        this.system = systemEnums.index;
    }

    @JSONField(serialize = false)
    public Boolean isAllNotNull() {
        return this.appId != null && !this.appId.trim().isEmpty() &&
                this.secretKey != null && !this.secretKey.trim().isEmpty() &&
                this.signKey != null && !this.signKey.trim().isEmpty() &&
                this.appKey != null && !this.appKey.trim().isEmpty() &&
                this.password != null && !this.password.trim().isEmpty() &&
                this.path != null && !this.path.trim().isEmpty() &&
                this.cachePath != null && !this.cachePath.trim().isEmpty() &&
                this.oneFileSize != null && this.oneFileSize > 0L &&
                this.version != null && this.version > 0L &&
                this.uri != null && !this.uri.trim().isEmpty() &&
                this.compressThread != null && this.compressThread > 0 &&
                this.isListen != null && this.isListen > -1 &&
                this.token != null && this.token.isAllNotNull() &&
                this.system != null && this.systemEnums != null;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public Integer getCompressThread() {
        return compressThread;
    }

    public void setCompressThread(Integer compressThread) {
        this.compressThread = compressThread;
    }

    public Integer getIsListen() {
        return isListen;
    }

    /**
     * FileSetting.LISTEN 监听文件 FileSetting.TIMING 定时
     *
     * @param isListen
     */
    public void setIsListen(Integer isListen) {
        this.isListen = isListen;
    }

    public TokenResponse getToken() {
        return token;
    }

    public void setToken(TokenResponse token) {
        this.token = token;
    }


    public Boolean getPathEncryption() {
        return pathEncryption;
    }

    public void setPathEncryption(Boolean pathEncryption) {
        this.pathEncryption = pathEncryption;
    }


    public Integer getSystem() {
        return system;
    }

    public void setSystem(Integer system) {
        SystemEnums[] enums = SystemEnums.values();
        for (SystemEnums item : enums) {
            if (Objects.equals(item.index, system)) {
                this.systemEnums = item;
                this.system = system;
                break;
            }
        }
    }

    public SystemEnums getSystemEnums() {
        return systemEnums;
    }

    public void setSystemEnums(SystemEnums system) {
        this.system = system.index;
        this.systemEnums = system;
    }
}
