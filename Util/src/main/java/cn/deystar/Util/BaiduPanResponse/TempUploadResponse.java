package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 分片上传回调
 */
public class TempUploadResponse {


    private Integer errno;

    @JSONField(name = "md5")
    private String digest;

    public TempUploadResponse() {
    }

    public TempUploadResponse(Integer errno, String digest) {
        this.errno = errno;
        this.digest = digest;
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
