package com.deystar.StreamToZip;

import com.deystar.Result.ResultState;
import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;
import lombok.SneakyThrows;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class StreamZipServiceImpl implements StreamZipService {


    @SneakyThrows
    @Override
    public synchronized void zipOutputStreamExample(File outputZipFile, FileListBean fileListBean, UserTyper userTyper) {

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(userTyper.getIsEncryption());
        if (userTyper.getIsEncryption()) {
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        }


        ZipOutputStream zos = null;
        try {
            zos = initializeZipOutputStream(outputZipFile, userTyper.getPassword());
            for (File fileToAdd : fileListBean.getFileLit()) {
                int readLen;
                byte[] buff = new byte[2048];
                // Entry size has to be set if you want to add entries of STORE compression method (no compression)
                // This is not required for deflate compression


                zipParameters.setFileNameInZip(fileToAdd.getName());
                zos.putNextEntry(zipParameters);

                try (InputStream inputStream = Files.newInputStream(fileToAdd.toPath())) {
                    while ((readLen = inputStream.read(buff)) != -1) {
                        zos.write(buff, 0, readLen);
                    }
                }

            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (zos != null) {

                zos.close();
            }
            ResultState.success(fileListBean.getZipName()+" is completed");
        }
    }


    public ZipOutputStream initializeZipOutputStream(File outputZipFile, String password)
            throws IOException {

        FileOutputStream fos = new FileOutputStream(outputZipFile);
        if (password != null && !password.trim().isEmpty()) {
            return new ZipOutputStream(fos, password.toCharArray());

        } else {
            return new ZipOutputStream(fos);
        }

    }

    public ZipParameters buildZipParameters(boolean encrypt,
                                            EncryptionMethod encryptionMethod, AesKeyStrength aesKeyStrength) {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptionMethod(encryptionMethod);
        zipParameters.setAesKeyStrength(aesKeyStrength);
        zipParameters.setEncryptFiles(encrypt);
        return zipParameters;
    }
}
