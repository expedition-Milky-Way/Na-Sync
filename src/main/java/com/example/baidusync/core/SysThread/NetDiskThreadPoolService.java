package com.example.baidusync.core.SysThread;

import java.util.Map;

/**
 * @author 杨名 （字 露煊） YeungLuhyun
 **/
public interface NetDiskThreadPoolService {
    void TurnOnSendFile();

    void run(Map<String, Object> map);

    boolean turnOff();
}
