package com.example.baidusync.core.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 杨 名 (字 露煊)
 */

public class BaseEntity {
    @TableField(exist = false)
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm");

    @TableId(type = IdType.NONE)
    public Integer id;
    @TableField(value = "createTime",jdbcType = JdbcType.TIMESTAMP)
    public Date createTime;
    @TableField(value = "updateTime",jdbcType = JdbcType.TIMESTAMP)
    public Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateTime() {
        return simpleDateFormat.format(createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
