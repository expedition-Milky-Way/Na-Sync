package cn.deystar.Util.BaiduPanResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFilter;

import javax.print.attribute.standard.MediaSize;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 预上传回调
 */
public class ReadyToUploadResponse {


    private Integer errno;


    @JSONField(name = "uploadid")
    private String uploadId;


    @JSONField(name = "return_type")
    private Integer returnType;


    @JSONField(name = "block_list")
    private String blockList;

    public ReadyToUploadResponse() {
    }

    public ReadyToUploadResponse(Integer errno, String uploadId, Integer returnType, String blockList) {
        this.errno = errno;

        this.uploadId = uploadId;
        this.returnType = returnType;
        this.blockList = blockList;
    }

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }


    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getReturnType() {
        return returnType;
    }

    public void setReturnType(Integer returnType) {
        this.returnType = returnType;
    }

    public String getBlockList() {
        return blockList;
    }

    public void setBlockList(String blockList) {
        this.blockList = blockList;
    }


    @Override
    public String toString() {
        return "{" +
                "\"errno\":" + this.errno + "," +
                "\"uploadId\":\"" + this.uploadId + "\"," +
                "\"returnType\":\"" + this.returnType + "\"," +
                "\"blockList\":\"" + this.blockList + "\"}";
    }
}
