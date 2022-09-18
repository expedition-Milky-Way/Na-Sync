package com.example.baidusync.Util.SystemLog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨 名 (字 露煊)
 */
@Service
public class LogServiceIMPL extends ServiceImpl<LogEntityMapper,LogEntity> implements LogService {

    @Override
    public boolean InsertInto(LogEntity logEntity){
        return baseMapper.insert(logEntity)==1;
    }

    @Override
    public List<LogEntity> getLog(){
        LambdaQueryWrapper<LogEntity> lambda = new LambdaQueryWrapper<>();
        lambda.orderBy(true,false,LogEntity::getCreateTime).last("LIMIT 10");
        return baseMapper.selectList(lambda);
    }
}
