package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class DeleteResponse {

    private Integer errno;

    private JSONArray info;

    private String taskId;

    @JSONField(name = "request_id")
    private String requestId;

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public JSONArray getInfo() {
        return info;
    }

    public void setInfo(JSONArray info) {
        this.info = info;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
