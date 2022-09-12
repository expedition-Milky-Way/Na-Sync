package com.example.baidusync.Admin.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.baidusync.core.BaseEntity;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

/**
 * @author 杨 名 (字 露煊)
 */
@Data
@TableName("file_setting")
public class FileSetting extends BaseEntity {

    @TableId(type = IdType.AUTO)
    public Integer id;
    @TableField("appId")
    public String appId;
    @TableField("secretKey")
    public String secretKey;
    @TableField("signKey")
    public String signKey;
    @TableField("appKey")
    public String appKey;
    @TableField("password")
    public String password;
    @TableField("path")
    public String path;
    @TableField("cachePath")
    public String cachePath;
    @TableField(value = "dateTime", jdbcType = JdbcType.TIMESTAMP)
    public String dateTime;

    /**
     * 有空返回true 否则返回false
     *
     * @return
     */
    public Boolean isEmpty() {
        if (this.appId == null || this.secretKey == null
                || this.secretKey == null || this.signKey == null
                || this.appKey == null || this.password == null ||
                this.path == null || this.cachePath == null ||
                this.dateTime == null) {
            return true;
        } else {
            return false;
        }

    }
}
