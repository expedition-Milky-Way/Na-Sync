package cn.deystar.Setting.UnSync.MappedService;

import cn.deystar.Setting.UnSync.Entity.UnSyncEntity;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface UnSyncMapped {
    boolean add(UnSyncEntity unsyncEntity);

    UnSyncEntity findByPath(String path);

    List<UnSyncEntity> query();


    void removeByPath(String path) throws InterruptedException;

    boolean isUnSync(String path);
}
