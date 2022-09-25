package com.example.baidusync.Admin.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import kotlin.jvm.internal.Lambda;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class FileSettingImpl extends ServiceImpl<FileSettingMapping, FileSetting> implements FileSettingService{

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


    /**
     * 设置fileSetting
     */
    @Override
    public Object settingFile(FileSetting fileSetting){
        LambdaQueryWrapper<FileSetting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileSetting::getAppKey,fileSetting.getAppKey())
                        .eq(FileSetting::getAppId,fileSetting.getAppId()).eq(FileSetting::getSecretKey,fileSetting.getPassword());
        if (baseMapper.selectCount(lambdaQueryWrapper)  ==0){
            baseMapper.insert(fileSetting);
        }
        return null;
    }

    /**
     * 获取setting
     */
    @Override
    public FileSetting getSetting(){
        LambdaQueryWrapper<FileSetting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNotNull(true,FileSetting::getId)
                .orderBy(true,false,FileSetting::getId).last("LIMIT 1");
        FileSetting resultEntity = baseMapper.selectOne(lambdaQueryWrapper);
        return resultEntity;
    }

    /**
     * 查看是否已经有setting了
     */
    @Override
    public boolean excites(FileSetting fileSetting){
        LambdaQueryWrapper<FileSetting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileSetting::getAppKey,fileSetting.getAppKey())
                .eq(FileSetting::getAppId,fileSetting.getAppId());
       return baseMapper.exists(lambdaQueryWrapper);

    }
}
