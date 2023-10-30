package cn.deystar.Compress.ZipFourJ;

import cn.deystar.CompressArgument.CompressArgument;
import cn.deystar.Const.CompressStatus;

import cn.deystar.Compress.Bean.FileListBean;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;

import java.io.File;
import java.io.IOException;


/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * zip4j
 */
public class FileToZip {


    public synchronized static FileListBean zip(FileListBean bean, CompressArgument compressArgument) {
        ZipFile zipFile = null;
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(compressArgument.getEncryption() && compressArgument.getPassword() != null && !compressArgument.getPassword().trim().isEmpty());
            zipFile = new ZipFile(bean.getCompressName());
            if (compressArgument.getEncryption() && compressArgument.getPassword() != null && !compressArgument.getPassword().trim().isEmpty()) {
                zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                zipFile.setPassword(compressArgument.getPassword().toCharArray());
            }
            zipFile.addFiles(bean.getFileList(), zipParameters);
            bean.setStatus(CompressStatus.SUCCESS);
        } catch (IOException e) {
            bean.setStatus(CompressStatus.ERROR);
            File eFile = new File(bean.getCompressName());
            if (eFile.exists()) eFile.delete();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                    System.gc();
                } catch (IOException ignored) {
                }
            }
        }
        return bean;
    }
}
