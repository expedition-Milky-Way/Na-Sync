package com.example.baidusync.Util.FileLog;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 杨名 （字 露煊） YeungLuhyun
 **/
public interface FileLogService extends IService<FileLogEntity> {
    Integer add(FileLogEntity entity);

    Boolean up(FileLogEntity entity);

    List<FileLogEntity> get();
}
