package com.example.baidusync.Admin.Service;

import cn.hutool.core.util.ObjectUtil;
import com.example.baidusync.Admin.Entity.UserEntity;
import com.example.baidusync.Util.ResponseData;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService{

    @Value("${custom-servicePass}")
    String servicePass;
    @Value("${custom-serveiceUser}")
    String serviceUser;
    @Value("${custom-serviceToken}")
    String token;

    public static final String ZIP_PATH = "F:/WorkSpace/zip_tryFiles/files.zip";

    /**
     * @Author YeungLuhyun Demo
     * 将文件复制到一个文件夹中，然后将该文件夹压缩
     * @return
     * @throws IOException
     */
    public  String zipFile(String path) throws IOException {
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
        zipFile.addFolder(new File(ZIP_PATH),zipParameters);
        return "success";
    }
}
