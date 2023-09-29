package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.annotation.JSONField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author YeungLuhyun
 **/
public class TokenResponse {

    @JSONField(name = "expires_in")
    private Integer expires;

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "refresh_token")
    private String refreshToken;

    @JSONField(name = "scope")
    private String scope;

    private String createTime;


    public TokenResponse(Integer expires, String accessToken, String refreshToken, String scope, String createTime) {
        this.expires = expires;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.createTime = createTime;
    }

    public Boolean isAllNotNull() {
        return this.accessToken != null && !this.accessToken.trim().isEmpty() &&
                this.expires != null && this.expires > 0 &&
                this.refreshToken != null && !this.refreshToken.trim().isEmpty() &&
                this.scope != null && !this.scope.trim().isEmpty() &&
                this.createTime != null;
    }

    public Boolean tokenBodyNotNull(){
        return this.accessToken != null && !this.accessToken.trim().isEmpty() &&
                this.expires != null && this.expires > 0 &&
                this.refreshToken != null && !this.refreshToken.trim().isEmpty() &&
                this.scope != null && !this.scope.trim().isEmpty();
    }


    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "{" +
                "\"expires\":" + expires + "," +
                "\"accessToken\":\"" + accessToken + "\"," +
                "\"refreshToken\":\"" + refreshToken + "\"," +
                "\"scope\":\"" + scope + "\"," +
                "\"createTime\":\"" + this.getCreateTime() + "," +
                "}";
    }


    public String getCreateTime() {
        return createTime;
    }
    public Date getCreateTimeForDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(this.createTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCreateTime(Date createTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTime = sdf.format(createTime);
    }
}
