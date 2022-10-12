package com.example.baidusync.Util.FileLog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 杨名 （字 露煊） YeungLuhyun
 **/
@Service
public class FileLogServiceIMPL extends ServiceImpl<FileMapper, FileLogEntity> implements FileLogService {


    /**
     * 增
     */
    @Override
    public Integer add(FileLogEntity entity) {
        this.save(entity);
        Integer id = entity.getId();
        return id;
    }


    /**
     * 改
     */
    @Override
    public Boolean up(FileLogEntity entity) {
        LambdaUpdateWrapper<FileLogEntity> lambda = new LambdaUpdateWrapper<>();
        if (entity.getId() != null) {
            lambda.eq(FileLogEntity::getId, entity.getId());
        }
        entity.setUpdateTime(new Date());
        return baseMapper.update(entity,lambda) ==1 ;
    }

    /**
     * 查
     */
    @Override
    public List<FileLogEntity> get() {
        LambdaQueryWrapper<FileLogEntity> lambda = new LambdaQueryWrapper<>();
        lambda.orderBy(true, false, FileLogEntity::getCreateTime);
        return baseMapper.selectList(lambda);
    }


}
