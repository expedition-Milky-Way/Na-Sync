package com.deystar.StreamToZip;

import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * NIO压缩
 */
public interface StreamZipService {

    void zipOutputStreamExample(File outputZipFile, FileListBean bean, UserTyper userTyper);



}
