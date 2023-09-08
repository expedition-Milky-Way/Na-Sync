package vip.yzxh.ZipFile.Zip;

import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.ZipFile.Zip.Entity.FileListBean;

import java.io.*;
import java.util.List;

/**
 * @Author YeungLuhyun
 **/
public class GoZip {
    private List<FileListBean> beans;

    private FileSetting user;


    public void start() {

        for (FileListBean bean : beans) {
            String outputPath = user.getCachePath();
            if (!outputPath.endsWith("/")) outputPath += "/";
            File outputZipFile = new File(outputPath + bean.getZipName());


            ZipParameters param = buildZipParameters(user.getPassword() != null && !user.getPassword().trim().isEmpty());
            try {
                zipOutputStreamExample(outputZipFile, bean.getFileLit(), param, user.getPassword());
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void zipOutputStreamExample(File outputZipFile, List<File> filesToAdd, ZipParameters zipParameters, String password)
            throws IOException {


        byte[] buff = new byte[4096];
        int readLen;

        try (ZipOutputStream zos = initializeZipOutputStream(outputZipFile, password)) {
            for (File fileToAdd : filesToAdd) {

                zipParameters.setFileNameInZip(fileToAdd.getName());
                zos.putNextEntry(zipParameters);
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(fileToAdd);
                    while ((readLen = inputStream.read(buff)) != -1) {
                        zos.write(buff, 0, readLen);
                    }
                } finally {
                    zos.closeEntry();
                    zos.flush();
                }

            }
        }
    }

    private ZipOutputStream initializeZipOutputStream(File outputZipFile, String password)
            throws IOException {

        FileOutputStream fos = new FileOutputStream(outputZipFile);
        if (password != null && !password.trim().equals("")) {
            return new ZipOutputStream(fos, password.toCharArray());
        }
        return new ZipOutputStream(fos);

    }


    private ZipParameters buildZipParameters(boolean encrypt) {
        if (encrypt) {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipParameters.setEncryptFiles(encrypt);
            return zipParameters;
        }
        return null;
    }


    public GoZip(FileSetting user, List<FileListBean> beans) {
        this.user = user;
        this.beans = beans;

    }
}
