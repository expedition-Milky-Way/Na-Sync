package vip.yzxh.Util.Beans.Upload;

import com.alibaba.fastjson.annotation.JSONField;

import javax.print.attribute.standard.MediaSize;
import java.util.List;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class UploadBeanSetup {
    /**
     * 文件或目录的大小 Byte单位
     */
    @JSONField(name = "size")
    private Integer size;
    /**
     * 百度网盘的path
     */
    @JSONField(name = "path")
    private String baiduPath;

    /**
     * 是否为目录 1:目录 0：文件
     */
    @JSONField(name = "is_dir")
    private Integer isDir;

    private Integer autoinit = 1;



    /**
     * 上传id
     */
    @JSONField(name = "upload_id")
    private String uploadId;

    /**
     * 每个分片的的MD5数组
     */
    @JSONField(name = "block_list")
    private List<String> blockList;

    @JSONField(serialize = false)
    private List<UploadBean> beanList;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getBaiduPath() {
        return baiduPath;
    }

    public void setBaiduPath(String baiduPath) {
        this.baiduPath = baiduPath;
    }

    public Integer getIsDir() {
        return isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public Integer getAutoinit() {
        return autoinit;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public List<String> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<String> blockList) {
        this.blockList = blockList;
    }

    public List<UploadBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<UploadBean> beanList) {
        this.beanList = beanList;
    }
}
