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
    public boolean add(UnSyncEntity unsyncEntity) {
        while (lock.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(unsyncEntity.getPath() + "设置为不同步失败");
                return false;
            }
        }
        lock.incrementAndGet();
        cacheList.add(unsyncEntity);
        flushFile();
        lock.decrementAndGet();
        return true;
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
        lock.decrementAndGet();
        return null;
    }

    @Override
    public List<UnSyncEntity> query() {
        return new ArrayList<>(cacheList);
    }

    @Override
    public void removeByPath(String path) throws InterruptedException {
        while (lock.get() > 0) {
            Thread.sleep(100);

        }
        lock.incrementAndGet();
        for (UnSyncEntity entity : cacheList) {
            if (entity.getPath().equals(path)) {
                cacheList.remove(entity);
                break;
            }
        }
        flushFile();
        lock.decrementAndGet();
    }

    @Override
    public boolean isUnSync(String path) {
        return cacheList.stream()
                .anyMatch(entity -> entity.getPath().equals(path));
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
        executor.submit(() -> {
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
