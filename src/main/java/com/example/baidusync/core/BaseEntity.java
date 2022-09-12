package com.example.baidusync.core;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

/**
 * @author 杨 名 (字 露煊)
 */
@Data
public class BaseEntity  {

    @TableField(value = "createTime",jdbcType = JdbcType.TIMESTAMP)
    public String createTime;
    @TableField(value = "updateTime",jdbcType = JdbcType.TIMESTAMP)
    public String updateTime;

    public String version;

}
