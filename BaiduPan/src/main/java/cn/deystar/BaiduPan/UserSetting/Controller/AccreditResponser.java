package cn.deystar.BaiduPan.UserSetting.Controller;

import cn.deystar.Util.HttpServerlet.RequestAndResponse;
import cn.deystar.Util.HttpServerlet.Response.ResponseData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author YeungLuhyun
 * <p>
 * 异步回调对象
 **/
public class AccreditResponser {

    private ResponseData responseData;


    private RequestAndResponse requestAndResponse;


    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public void setRequestAndResponse(RequestAndResponse requestAndResponse) {
        this.requestAndResponse = requestAndResponse;
    }


    public AccreditResponser(ResponseData responseData,HttpServletRequest request,HttpServletResponse response) {
        this.responseData = responseData;
        this.requestAndResponse = new RequestAndResponse(request,response);
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public RequestAndResponse getRequestAndResponse() {
        return requestAndResponse;
    }
}
