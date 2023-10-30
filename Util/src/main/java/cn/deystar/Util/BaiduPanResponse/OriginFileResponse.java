package cn.deystar.Util.BaiduPanResponse;

import cn.deystar.Util.Const.BaiduCategory;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;


/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 网盘上的文件和目录
 */
public class OriginFileResponse {

    /**
     * 文件类型
     */
    @JSONField(name = "category")
    private Integer category;

    /**
     * 文件类型的系统枚举
     */
    private BaiduCategory categoryEnum;

    /**
     * 文件在云端的唯一标识id
     */
    @JSONField(name = "fs_id")
    private Long fsId;

    /**
     * 是否文件 0：否 1：是
     */
    @JSONField(name = "isdir")
    private Integer isDir;

    @JSONField(name = "path")
    private String path;
    @JSONField(name = "server_filename")
    private String serverFileName;
    @JSONField(name = "local_ctime")
    private Integer localCreateTime;

    @JSONField(name = "local_mtime")
    private Integer localChangedTime;

    @JSONField(name = "server_ctime")
    private Integer serverCreateTime;

    @JSONField(name = "server_mtime")
    private Integer serverChangedTime;

    @JSONField(name = "md5")
    private String digest;

    /**
     * 文件大小
     */
    @JSONField(name = "size")
    private Long size;

    /**
     * 缩略图地址
     */
    @JSONField(name = "thumbs")
    private JSONObject thumbs;

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        if (this.categoryEnum == null) {
            BaiduCategory[] baiduCategories = BaiduCategory.values();
            for (BaiduCategory c : baiduCategories) {
                if (c.category.equals(category)) {
                    this.setCategoryEnum(c);
                }
            }
        }
        this.category = category;
    }

    public BaiduCategory getCategoryEnum() {
        if (this.categoryEnum == null) {
            BaiduCategory[] baiduCategories = BaiduCategory.values();
            for (BaiduCategory c : baiduCategories) {
                if (c.category.equals(category)) {
                    this.setCategoryEnum(c);
                }
            }
        }
        return categoryEnum;
    }

    public void setCategoryEnum(BaiduCategory categoryEnum) {
        this.categoryEnum = categoryEnum;
    }

    public Long getFsId() {
        return fsId;
    }

    public void setFsId(Long fsId) {
        this.fsId = fsId;
    }

    public Integer getIsDir() {
        return isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public Integer getLocalCreateTime() {
        return localCreateTime;
    }

    public void setLocalCreateTime(Integer localCreateTime) {
        this.localCreateTime = localCreateTime;
    }

    public Integer getLocalChangedTime() {
        return localChangedTime;
    }

    public void setLocalChangedTime(Integer localChangedTime) {
        this.localChangedTime = localChangedTime;
    }

    public Integer getServerCreateTime() {
        return serverCreateTime;
    }

    public void setServerCreateTime(Integer serverCreateTime) {
        this.serverCreateTime = serverCreateTime;
    }

    public Integer getServerChangedTime() {
        return serverChangedTime;
    }

    public void setServerChangedTime(Integer serverChangedTime) {
        this.serverChangedTime = serverChangedTime;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getThumbs() {
        if (thumbs != null) {
            return thumbs.getString("icon");
        }
        return null;
    }

    public void setThumbs(JSONObject thumbs) {
        this.thumbs = thumbs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServerFileName() {
        return serverFileName;
    }

    public void setServerFileName(String serverFileName) {
        this.serverFileName = serverFileName;
    }
}
