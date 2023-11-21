package cn.deystar.Setting.UnSync.Entity;

import com.alibaba.fastjson.JSON;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class UnSyncEntity {


    private Long id;

    private String name;

    private String path;

    private Boolean isDelete;

    public UnSyncEntity() {
        this.isDelete = false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}
