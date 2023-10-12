package cn.com.deystar.Zip.ZipFourJ;

import cn.com.deystar.Const.CompressStatus;
import cn.com.deystar.Result.ResultState;
import cn.com.deystar.Zip.Bean.FileListBean;
import cn.com.deystar.ZipArgument.ZipArgument;
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


    public synchronized static FileListBean zip(FileListBean bean, ZipArgument userTyper) {
        ZipFile zipFile = null;
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(userTyper.getEncryption() && userTyper.getPassword() != null && !userTyper.getPassword().trim().isEmpty());
            zipFile = new ZipFile(bean.getZipName());
            if (userTyper.getEncryption() && userTyper.getPassword() != null && !userTyper.getPassword().trim().isEmpty()) {
                zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                zipFile.setPassword(userTyper.getPassword().toCharArray());
            }
            zipFile.addFiles(bean.getFileLit(), zipParameters);
            bean.setStatus(CompressStatus.SUCCESS);
        } catch (IOException e) {
            bean.setStatus(CompressStatus.ERROR);
            File eFile = new File(bean.getZipName());
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
