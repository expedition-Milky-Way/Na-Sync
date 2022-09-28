package com.example.baidusync.Util.FileUtil;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.util.List;

/**
 * @author 杨 名 (字 露煊)
 * <p>
 * 文件压缩
 */

public class ZipFileUtil {
    //如果出现同名文件，将会采用  xx(NAME_PREFIX).zip来命名文件
    private static Integer NAME_PREFIX = 1;
    //文件后缀
    private static final String FILE_ZIP_PREFIX = ".zip";
    private static final String FILE_7Z_PREFIX = ".7z";

    public void zipFile(String name, List<File> fileList, String password) throws ZipException {
        if (fileList.size() > 0) {
            String fileName = this.rename(name, FILE_ZIP_PREFIX);
            ZipFile zipFile = new ZipFile(fileName);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
            char[] passowrdChard = password.toCharArray();
            zipFile.setPassword(passowrdChard);
            zipFile.addFiles(fileList,zipParameters);
        }
    }


    /**
     * 重命名（检查文件名是否重复）
     */
    public  String rename(String name, String prefix) {
        synchronized (NAME_PREFIX){
            String fileFinalName = name + prefix;
            if (isExist(fileFinalName)) { //如果存在相同的名
                String[] nameChar = name.split("/");
                String oldName = nameChar[nameChar.length - 1];
                String nName = oldName + "(" + NAME_PREFIX + ")";
                String dir = name.replace(oldName, nName);
                NAME_PREFIX++;
                return rename(dir,prefix);
            }
            NAME_PREFIX = 1;
            return name + prefix;
        }
    }

    /**
     * 检查文件是否重复
     */
    private Boolean isExist(String name) {
        File file = new File(name);
        return file.exists();
    }

}
