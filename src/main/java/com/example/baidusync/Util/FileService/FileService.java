package com.example.baidusync.Util.FileService;

import java.io.File;

/**
 * @author 杨 名 (字 露煊)
 */
public interface FileService {
    /**
     *
     * @param name 源文件名（不包含路径）
     * @param file 文件夹（绝对路径）
     */
    void computedMD5(String name, File file,Long fileSize,String parent);
}
