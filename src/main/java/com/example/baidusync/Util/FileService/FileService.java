package com.example.baidusync.Util.FileService;

import java.io.File;

/**
 * @author 杨 名 (字 露煊)
 */
public interface FileService {
    void goBackup(String zipPath, String filePath, String password);

    void computedMD5(String name, File file);
}
