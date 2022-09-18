package com.example.baidusync.Util;

import java.io.File;

/**
 * @author 杨 名 (字 露煊)
 *文件和md5的实体类
 */
public class FileAndDigsted {

    /**
     * tempfile文件的路径（包括文件名）
     */
    private String pathAndName;
    /**
     * MD5
     */
    private String digsted;

    public FileAndDigsted() {
    }

    public FileAndDigsted(String pathAndName, String digsted) {
        this.pathAndName = pathAndName;
        this.digsted = digsted;
    }

    public String getPathAndName() {
        return pathAndName;
    }

    public void setPathAndName(String pathAndName) {
        this.pathAndName = pathAndName;
    }

    public String getDigsted() {
        return digsted;
    }

    public void setDigsted(String digsted) {
        this.digsted = digsted;
    }
}
