package cn.deystar.BaiduPan.LocalFiles.Service.impl;

import cn.deystar.BaiduPan.LocalFiles.Service.LocalFileService;
import cn.deystar.Setting.UnSync.Entity.UnSyncEntity;
import cn.deystar.Setting.UnSync.MappedService.UnSyncMapped;
import cn.deystar.Util.Beans.LocalFiles.LocalFile;
import cn.deystar.Util.Util.LocalFileScan;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Service
public class LocalFileServiceImpl implements LocalFileService {

    @Resource
    UnSyncMapped unSyncMapped;

    @Override
    public List<List<LocalFile>> listAll(String path) {
        List<LocalFile> localFiles = LocalFileScan.scan(path);
        localFiles = filter(localFiles);
        List<List<LocalFile>> finalLocalFiles = new ArrayList<>();
        Integer rowMax = 12; //单行最多
        Integer size = localFiles.size();

        if (size <= rowMax) {
            finalLocalFiles.add(localFiles);
        } else {
            Integer rowNum = new BigDecimal(size).divide(new BigDecimal(rowMax), RoundingMode.UP).intValue(); //行数
            for (int i = 0; i < rowNum; i++) {
                Integer toIndex = i * rowMax + rowMax;
                if (toIndex > size) toIndex = size;
                Integer fromIndex = i * rowMax;
                List<LocalFile> subList = localFiles.subList(fromIndex, toIndex);

                finalLocalFiles.add(subList);
            }
        }
        return finalLocalFiles;
    }

    private List<LocalFile> filter(List<LocalFile> localFiles) {
        List<UnSyncEntity> unSyncEntities = unSyncMapped.query();
        for (LocalFile localFile : localFiles) {
            localFile.setUnSync(unSyncEntities.stream()
                    .anyMatch(unSyncEntity -> unSyncEntity.getPath().equals(localFile.getPath()) || localFile.getPath().contains(unSyncEntity.getPath())));
        }
        return localFiles;
    }

    @Override
    public boolean unSync(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        UnSyncEntity entity = new UnSyncEntity();
        entity.setPath(path);
        entity.setName(file.getName());
        return unSyncMapped.add(entity);

    }

    @Override
    public boolean canSync(String path) {
        File file = new File(path);
        if (!file.exists()) return false;
        try {
            unSyncMapped.removeByPath(path);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
