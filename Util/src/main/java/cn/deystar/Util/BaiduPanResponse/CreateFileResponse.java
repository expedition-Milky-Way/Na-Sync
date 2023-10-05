package cn.deystar.Util.BaiduPanResponse;

import cn.deystar.Util.Const.BaiduCategory;
import com.alibaba.fastjson.annotation.JSONField;

import javax.print.attribute.standard.MediaSize;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 创建文件回调
 */
public class CreateFileResponse {

    private Integer errno;

    /**
     * 文件在网盘中的唯一标识id
     */
    @JSONField(name = "fs_id")
    private Long fsId;


    @JSONField(name = "md5")
    private String digest;

    /**
     * 文件名
     */
    @JSONField(name = "server_filename")
    private String fileName;

    /**
     * 百度网盘所认准的类型
     */
    @JSONField(name = "category")
    private Integer category;


    @JSONField(serialize = false)
    private BaiduCategory categoryEnums;


    private String path;


    private Integer size;

    @JSONField(name = "ctime")
    private Long createTime;

    @JSONField(name = "mtime")
    private Long mtime;

    @JSONField(name = "isdir")
    private Integer isDirectory;


    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public Long getFsId() {
        return fsId;
    }

    public void setFsId(Long fsId) {
        this.fsId = fsId;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
        BaiduCategory[] categories = BaiduCategory.values();
        for (BaiduCategory cEnums : categories){
            if (cEnums.category.equals(category)){
                this.categoryEnums = cEnums;
            }
        }
    }

    public BaiduCategory getCategoryEnums() {
        return categoryEnums;
    }

    public void setCategoryEnums(BaiduCategory categoryEnums) {
        this.categoryEnums = categoryEnums;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getMtime() {
        return mtime;
    }

    public void setMtime(Long mtime) {
        this.mtime = mtime;
    }

    public Integer getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Integer isDirectory) {
        this.isDirectory = isDirectory;
    }
}
