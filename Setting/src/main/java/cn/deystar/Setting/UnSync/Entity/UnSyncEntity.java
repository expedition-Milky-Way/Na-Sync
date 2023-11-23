package cn.deystar.Setting.UnSync.Entity;

import com.alibaba.fastjson.JSON;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class UnSyncEntity {




    private String name;

    private String path;



    public UnSyncEntity() {

    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
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


}
