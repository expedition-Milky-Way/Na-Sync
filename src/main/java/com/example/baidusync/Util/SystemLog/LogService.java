package com.example.baidusync.Util.SystemLog;

import java.util.List;

/**
 * @author 杨 名 (字 露煊)
 */
public interface LogService {
    boolean InsertInto(LogEntity logEntity);

    List<LogEntity> getLog();
}
