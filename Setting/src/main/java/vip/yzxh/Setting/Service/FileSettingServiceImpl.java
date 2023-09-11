package vip.yzxh.Setting.Service;


import com.alibaba.fastjson.JSONObject;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Util.Util.ConfigFileTemplate;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileSettingServiceImpl implements FileSettingService {


    private final SimpleDateFormat formator = new SimpleDateFormat("yyyy-mm-dd HH:mm");

    private static final String SETTING_FILE_NAME = "setting.json";

    private static FileSetting setting;

    private static AtomicInteger version = new AtomicInteger(0);


    /**
     * 设置fileSetting
     */
    @Override
    public Object settingFile(FileSetting fileSetting) {
        if (fileSetting.getVersion() == null) {
            fileSetting.setVersion(version.incrementAndGet());
        }
        setting = fileSetting;
        ConfigFileTemplate.writeFile(genFilePath(), fileSetting.toString());
        return null;
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
    private File genFilePath() {
        File path = new File(path());
        if (!path.exists()) {
            path.mkdirs();
        }
        String dir = path.getAbsolutePath();
        if (dir.contains("\\")) dir = dir.replace("\\", "/");
        dir += "/" + SETTING_FILE_NAME;
        return new File(dir);
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

}
