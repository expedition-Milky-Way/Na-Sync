package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author YeungLuhyun
 * 设备码模式授权回调
 **/
public class DeviceCodeResponse {


    @JSONField(name = "device_code")
    private String deviceCode;

    @JSONField(name = "user_code")
    private String userCode;

    /**
     * userCode授权url
     */
    @JSONField(name = "verification_url")
    private String verificationUrl;

    @JSONField(name = "qrcode_url")
    private String qrcode;

    /**
     * 授权码过期时间
     */
    @JSONField(name = "expires_in")
    private Integer expires;

    /**
     * 轮训获取token的间隔时间
     */
    @JSONField(name = "interval")
    private Integer interval;

    public DeviceCodeResponse() {

    }

    public DeviceCodeResponse(String deviceCode, String userCode, String verificationUrl, String qrcode, Integer expires, Integer interval) {

        this.deviceCode = deviceCode;
        this.userCode = userCode;
        this.verificationUrl = verificationUrl;
        this.qrcode = qrcode;
        this.expires = expires;
        this.interval = interval;
    }

    public Boolean isAllNotNull() {
        return (this.deviceCode != null && !this.deviceCode.trim().isEmpty() &&
                this.userCode != null && !this.userCode.trim().isEmpty() &&
                this.verificationUrl != null && !this.verificationUrl.trim().isEmpty() &&
                this.qrcode != null && !this.qrcode.trim().isEmpty() &&
                this.interval != null && this.interval > 0L);
    }


    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
