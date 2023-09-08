package vip.yzxh.Setting.Service;


import com.alibaba.fastjson.JSONObject;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Util.ConfigFileTemplate;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileSettingServiceImpl implements FileSettingService {



    private final SimpleDateFormat formator = new SimpleDateFormat("yyyy-mm-dd HH:mm");

    /**
     * @return
     * @throws IOException
     * @Author YeungLuhyun Demo
     * 将文件复制到一个文件夹中，然后将该文件夹压缩
     */
    public String zipFile(String path) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        //设置压缩方法
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
        //设置压缩级别
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        //设置加密密码
        zipParameters.setEncryptFiles(true);
        //设置加密方式
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        //AES默认加密强度256
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        //new File
        File file = new File(path);
        //压缩对象
        ZipFile zipFile = new ZipFile(file);
        //开始压缩，参数1：对应的路径下,参数2：压缩配置
//        zipFile.addFolder(new File(ZIP_PATH), zipParameters);
        return "success";
    }


    /**
     * 设置fileSetting
     */
    @Override
    public Object settingFile(FileSetting fileSetting) {
        ConfigFileTemplate.writeFile(genPath(), fileSetting.toString());
        return null;
    }

    /**
     * 获取setting
     */
    @Override
    public FileSetting getSetting() {

        String jsonString = ConfigFileTemplate.readFile(new File(path()));
        return JSONObject.parseObject(jsonString, FileSetting.class);
    }


    /**
     * 如果没有这个path就
     *
     * @return
     */
    private File genPath() {
        File path = new File(path());
        if (!path.exists()) {
            path.mkdirs();
        }
        return path;
    }

    private String path() {
        String path = "";
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/templates/settings/";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

}
