package com.deystar.Zip.Entity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author YeungLuhyun
 **/
@Data
public class FileListBean {

    private String originParent;
    private String parent;

    private List<File> fileLit;

    private String zipName;

    private Long totalSize;

    public FileListBean() {
        this.fileLit = new ArrayList<>();
        this.totalSize = 0L;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    @Override
    public String toString() {
        return "FileListBean{" +
                "parent='" + parent + '\'' +
                ", fileLit=" + JSONArray.toJSONString(fileLit, JSONWriter.Feature.FieldBased) +
                ", totalSize=" + totalSize +
                ", zipName="+zipName+
                '}';
    }
}
