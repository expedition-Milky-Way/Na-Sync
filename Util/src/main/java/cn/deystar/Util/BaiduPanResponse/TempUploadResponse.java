package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 分片上传回调
 */
public class TempUploadResponse {


    @JSONField(name = "uploadid")
    private String uploadId;

    @JSONField(name = "md5")
    private String digest;

    /**
     * 上传到第几个分片
     */
    @JSONField(name = "partseq")
    private String partSeq;

    public TempUploadResponse() {
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getPartSeq() {
        return Integer.valueOf(this.partSeq);
    }

    public void setPartSeq(String partSeq) {
        this.partSeq = partSeq;
    }
}
