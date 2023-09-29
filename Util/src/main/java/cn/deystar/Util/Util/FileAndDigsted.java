package cn.deystar.Util.Util;

import cn.hutool.log.Log;


import java.io.File;
import java.util.Map;

/**
 * @author 杨 名 (字 露煊)
 *文件和md5的实体类
 */
public class FileAndDigsted {

    private Integer index;

    /**
     * tempfile文件的路径（包括文件名）
     */
    private String path;
    /**
     * 源文件名(暂时无用）
     */
    private String name;

    /**
     * MD5
     */
    private String digsted;

    /**
     * 父文件夹名称
     */
    private String parentName;

    /**
     * 缓存文件大小
     */
    private Long size;


    public FileAndDigsted() {
    }



    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }


    public String getDigsted() {
        return digsted;
    }

    public void setDigsted( String digsted) {
        this.digsted = digsted;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
