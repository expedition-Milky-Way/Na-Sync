package com.example.baidusync.Util.FileLog;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.baidusync.core.Entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 杨 名 （ 字 露煊）YeungLuhyun
 * 存储上传到百度网盘后，网盘文件夹名称和网盘文件名
 */
@Data
@TableName("file_log")
public class FileLogEntity extends BaseEntity implements Serializable {

    public static Integer PROGRESS_NO_COMPLETE = 0;
    public static Integer PROGRESS_SUCCESS= 1;
    public static Integer PROGRESS_ERROR = 2;


    private static final long serialVersionUID = 8144362233683078479L;

    private Integer version;

    private String fileName;

    private String path;

    private String parent;
    //原文件、文件夹、父文件夹 的名
    @TableField("original_file_name")
    private String originalFileName;
    @TableField("original_path_name")
    private String originalPathName;
    @TableField("original_parent_name")
    private String originalParentName;

    private String nameHash;

    private String password;
//0：未完成上传 1：上传完成 2： 上传失败
    private Integer progress;


    public FileLogEntity() {
    }

    public FileLogEntity(Integer version, String fileName, String path,
                         String parent, String originalFileName,
                         String originalPathName, String originalParentName,
                         String nameHash, String password, Integer progress) {
        this.version = version;
        this.fileName = fileName;
        this.path = path;
        this.parent = parent;
        this.originalFileName = originalFileName;
        this.originalPathName = originalPathName;
        this.originalParentName = originalParentName;
        this.nameHash = nameHash;
        this.password = password;
        this.progress = progress;
    }
}
