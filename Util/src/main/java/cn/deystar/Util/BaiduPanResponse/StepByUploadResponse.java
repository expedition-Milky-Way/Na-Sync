package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.annotation.JSONField;

import javax.print.attribute.standard.MediaSize;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 单步回调响应
 */
public class StepByUploadResponse {


    private Integer errno;
    private String path;

    private Long size;

    @JSONField(name = "ctime")
    private Long cTime;

    @JSONField(name = "mtime")
    private Long mTime;

    @JSONField(name = "md5")
    private String digest;

    @JSONField(name = "fs_id")
    private Long fileNetDiskId;

    @JSONField(name = "request_id")
    private String requestId;

    public StepByUploadResponse(String path, Long size, Long cTime, Long mTime, String digest, Long fileNetDiskId, String requestId) {
        this.path = path;
        this.size = size;
        this.cTime = cTime;
        this.mTime = mTime;
        this.digest = digest;
        this.fileNetDiskId = fileNetDiskId;
        this.requestId = requestId;
    }

    public StepByUploadResponse() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getcTime() {
        return cTime;
    }

    public void setcTime(Long cTime) {
        this.cTime = cTime;
    }

    public Long getmTime() {
        return mTime;
    }

    public void setmTime(Long mTime) {
        this.mTime = mTime;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getFileNetDiskId() {
        return fileNetDiskId;
    }

    public void setFileNetDiskId(Long fileNetDiskId) {
        this.fileNetDiskId = fileNetDiskId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }
}
