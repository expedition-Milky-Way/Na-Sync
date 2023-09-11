package vip.yzxh.BaiduPan.BaiduPanResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import vip.yzxh.Util.HttpServerlet.ResponseData;

/**
 * @Author YeungLuhyun
 **/
public class TokenResponse extends ResponseData {

    @JSONField(name = "expires_in")
    private Integer expires;

    @JSONField(name = "access_token")
    private String token;

    @JSONField(name = "refresh_token")
    private String refreshToken;

    @JSONField(name = "scope")
    private String scope;


    public TokenResponse(Integer expires, String token, String refreshToken, String scope) {
        super(true, ResponseData.DEFAULT_SUCCESS_CODE, ResponseData.DEFAULT_SUCCESS_MESSAGE, null);
        this.expires = expires;
        this.token = token;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public TokenResponse(Boolean success, Integer code, String msg) {
        super(success, code, msg, null);
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        return JSONObject.toJSONString(this);
    }
}
