package cn.deystar.Setting.UnSync.MappedService.impl;

import cn.deystar.Setting.UnSync.Entity.UnSyncEntity;
import cn.deystar.Setting.UnSync.MappedService.UnSyncMapped;
import cn.deystar.Util.Util.ConfigFileTemplate;
import com.alibaba.fastjson.JSONArray;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Service
public class UnSyncMappedServiceImpl implements UnSyncMapped {

    @Resource
    private ThreadPoolTaskExecutor executor;

    private static final AtomicInteger lock = new AtomicInteger(0);

    private static final List<UnSyncEntity> cacheList = new ArrayList<>();

    public static final String UNSYNC_FILE_NAME = "UnSync.json";


    @Override
    public Long add(UnSyncEntity unsyncEntity) {
        while (lock.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lock.incrementAndGet();
        Long id = cacheList.size() + 1L;
        unsyncEntity.setId(id);
        cacheList.add(unsyncEntity);
        flushFile();
        lock.decrementAndGet();
        return id;
    }

    @Override
    public UnSyncEntity findByPath(String path) {
        while (lock.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lock.incrementAndGet();
        for (UnSyncEntity entity : cacheList) {
            if (entity.getPath().equals(path)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public List<UnSyncEntity> query() {
        List<UnSyncEntity> list = new ArrayList<>();
        list.addAll(cacheList);
        return list;
    }


    @Override
    public Boolean removeById(Long id) {
        if (id == null || id < 1) {
            System.out.println("id异常：：" + id);
            return false;
        }
        while (lock.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lock.incrementAndGet();
        UnSyncEntity entity = null;
        int index = -1;
        int end = cacheList.size();
        int begin = 0;
        while (begin <= end) {
            int mid = (end + begin) / 2;
            if (cacheList.get(mid).getId().compareTo(id) > 0) {
                end = mid - 1;
            } else if (cacheList.get(mid).getId().compareTo(id) < 0) {
                begin = mid + 1;
            } else {
                index = mid;
                entity = cacheList.get(mid);
            }
        }
        if (entity != null) {
            entity.setDelete(true);
            cacheList.set(index, entity);
            flushFile();
        }
        return lock.getAndDecrement() - 1 == 0;
    }

    /**
     * Spring启动时调用
     *
     * @return
     */
    public boolean readFile() {
        try {
            String path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/settings/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            path += UnSyncMappedServiceImpl.UNSYNC_FILE_NAME;
            if (new File(path).exists()) {
                String value = ConfigFileTemplate.readFile(path);
                cacheList.addAll(JSONArray.parseArray(value, UnSyncEntity.class));
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return cacheList.isEmpty();
    }

    private void flushFile() {
        executor.execute(() -> {
            while (lock.get() > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lock.incrementAndGet();
            StringBuilder builder = new StringBuilder("[");
            for (UnSyncEntity entity : cacheList) {
                builder.append(entity.toString());
                if (cacheList.indexOf(entity) != cacheList.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
            ConfigFileTemplate.writeFile(this.genFilePath(), builder.toString());
            lock.decrementAndGet();
        });
    }

    /**
     * 获取文件位置。如果没有该文件，将会被创建
     *
     * @return
     */
    private String genFilePath() {
        String path = "";
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/settings/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            path += UNSYNC_FILE_NAME;
            return path;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
