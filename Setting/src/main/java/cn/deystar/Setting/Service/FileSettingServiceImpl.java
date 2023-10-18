package cn.deystar.Setting.Service;


import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Util.Util.ConfigFileTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

@Service
public class FileSettingServiceImpl implements FileSettingService {

    @Resource
    Lock updateLock;
    private final SimpleDateFormat formator = new SimpleDateFormat("yyyy-mm-dd HH:mm");

    private static final String SETTING_FILE_NAME = "setting.json";

    private static FileSetting setting;

    private static AtomicInteger version = new AtomicInteger(0);

    private final Object tokenLock = new Object();

    @Override
    public FileSetting updateSetting(FileSetting setting) {
            try {
                updateLock.lock();
                FileSetting update = getSetting();
                if (update == null) update = new FileSetting();
                if (setting.getAppId() != null && !setting.getAppId().trim().isEmpty())
                    update.setAppId(setting.getAppId());
                if (setting.getAppKey() != null && !setting.getAppKey().trim().isEmpty()) {
                    update.setAppKey(setting.getAppKey());
                }
                if (setting.getSecretKey() != null && !setting.getSecretKey().trim().isEmpty()) {
                    update.setSecretKey(setting.getSecretKey());
                }
                if (setting.getPassword() != null && !setting.getPassword().trim().isEmpty()) {
                    update.setPassword(setting.getPassword());
                }
                if (setting.getSignKey() != null && !setting.getSignKey().trim().isEmpty()) {
                    update.setSignKey(setting.getSignKey());
                }
                if (setting.getCompressThread() != null && setting.getCompressThread() > 0) {
                    update.setCompressThread(setting.getCompressThread());
                }
                if (setting.getTaskNum() != null && setting.getTaskNum() > 0) {
                    update.setTaskNum(setting.getTaskNum());
                }
                if (setting.getIsListen() != null && setting.getIsListen() > -1) {
                    update.setIsListen(setting.getIsListen());
                }
                if (setting.getPath() != null && !setting.getPath().trim().isEmpty()) {
                    update.setPath(setting.getPath());
                }
                if (setting.getCachePath() != null && !setting.getCachePath().trim().isEmpty()) {
                    update.setCachePath(setting.getCachePath());
                }
                if (setting.getPathEncryption() != null) update.setPathEncryption(setting.getPathEncryption());
                if (setting.getSystem() != null) {
                    update.setSystem(setting.getSystem());
                }
                if (setting.getToken() != null) {
                    update.setToken(setting.getToken());
                }
                if (setting.getSystemEnums() != null)
                    update.setSystemEnums(setting.getSystemEnums());
                setting.setVersion(version.incrementAndGet());
                update.setDateTime(formator.format(new Date()));
                return (FileSetting) this.settingFile(update);
            }finally {
                updateLock.unlock();
            }

    }

    /**
     * 设置fileSetting
     */
    @Override
    public Object settingFile(FileSetting fileSetting) {
        fileSetting.setVersion(version.incrementAndGet());
        setting = fileSetting;
        ConfigFileTemplate.writeFile(genFilePath(), fileSetting.toString());
        return fileSetting;
    }

    /**
     * 获取setting
     */
    @Override
    public FileSetting getSetting() {
        if (setting == null || setting.getVersion() < version.get()) {
            String jsonString = ConfigFileTemplate.readFile(genFilePath());
            setting = JSONObject.parseObject(jsonString, FileSetting.class);
        }
        return setting;

    }


    /**
     * 如果没有这个path就
     *
     * @return
     */
    private String genFilePath() {
        File path = new File(path());
        if (!path.exists()) {
            path.mkdirs();
        }
        String dir = path.getAbsolutePath();
        if (dir.contains("\\")) dir = dir.replace("\\", "/");
        dir += "/" + SETTING_FILE_NAME;
        return dir;
    }

    private String path() {
        String path = "";
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/settings/";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    @Override
    public TokenResponse getToken() {
        synchronized (tokenLock) {
            return setting.getToken();
        }
    }

    @Override
    public void holdOn() {
        synchronized (tokenLock) {
            try {
                tokenLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void goOn() {
        synchronized (tokenLock) {
            tokenLock.notify();
        }
    }
}
