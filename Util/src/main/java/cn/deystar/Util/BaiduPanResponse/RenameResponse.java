package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 百度网盘重命名回调
 */
public class RenameResponse {

    private Integer errno;

    @JSONField(name = "info")
    private List<PathInstance> info;

    @JSONField(name = "request_id")
    private String requestId;

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public List<PathInstance> getInfo() {
        return info;
    }

    public void setInfo(List<PathInstance> info) {
        this.info = info;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public class PathInstance{

        private Integer errno;

        private String path;

        public PathInstance(Integer errno, String path) {
            this.errno = errno;
            this.path = path;
        }

        public Integer getErrno() {
            return errno;
        }

        public void setErrno(Integer errno) {
            this.errno = errno;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}

