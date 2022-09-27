package com.example.baidusync.Util.SystemLog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 杨 名 (字 露煊)
 */
@Service
public class LogServiceIMPL extends ServiceImpl<LogEntityMapper, LogEntity> implements LogService {


    static Integer LAST_SQL_ID = 0;

    @Override
    @Transactional
    public boolean InsertInto(LogEntity logEntity) {
        return baseMapper.insert(logEntity) == 1;
    }

    @Override
    public List<LogEntity> getLog() {
        LambdaQueryWrapper<LogEntity> lambda = new LambdaQueryWrapper<>();
        lambda.orderBy(true, false, LogEntity::getCreateTime);
        List<LogEntity> logList = baseMapper.selectList(lambda);
        if (logList.size() > 0 && logList.get(0).getId() > LAST_SQL_ID) {
            LAST_SQL_ID = logList.get(0).getId();
            return logList;
        }
            return null;

    }

}
