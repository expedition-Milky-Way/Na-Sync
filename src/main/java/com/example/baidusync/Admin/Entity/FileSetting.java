package com.example.baidusync.Admin.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.baidusync.core.BaseEntity;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨 名 (字 露煊)
 */

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
    @TableField(value = "task_num")
    public Integer taskNum; //最大并行任务数量


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

    public FileSetting(){

    }

    public FileSetting(Integer id, String appId, String secretKey, String signKey, String appKey, String password, String path, String cachePath, String dateTime, Integer taskNum) {
        this.id = id;
        this.appId = appId;
        this.secretKey = secretKey;
        this.signKey = signKey;
        this.appKey = appKey;
        this.password = password;
        this.path = path;
        this.cachePath = cachePath;
        this.dateTime = dateTime;
        this.taskNum = taskNum;

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }



}
