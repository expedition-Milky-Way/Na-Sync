package com.example.baidusync.Util.SystemLog;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.baidusync.core.BaseEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 杨 名 (字 露煊)
 *
 */
@TableName("sys_log")
@Data
@Slf4j
public class LogEntity implements Serializable {

    public static final Integer LOG_TYPE_INFO = 0;

    public static final Integer LOG_TYPE_WARN = 1;

    public static final Integer LOG_TYPE_ERROR = 2;
    private static final long serialVersionUID = 6843145008833598631L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "createTime",jdbcType = JdbcType.TIMESTAMP)
    private String createTime;
    @TableField(value = "updateTime",jdbcType = JdbcType.TIMESTAMP)
    private String updateTime;

    /**
     * 类名
     */
    @TableField("classz")
    private String Calssz;
    /**
     * 信息
     */
    @TableField("message")
    private String message;

    /**
     * 类型 0： info 1: warn 2:error
     */
    @TableField("type")
    private Integer type;


    /**
     *
     * @param classz
     * @param message
     * @param type LogEntity.LOG_TYPE_x
     */
    public LogEntity(String classz,String message,Integer type){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm");
       this.createTime = format.format(new Date());
        this.Calssz = classz;
        this.message = message;
        this.type = type;
    }



}
