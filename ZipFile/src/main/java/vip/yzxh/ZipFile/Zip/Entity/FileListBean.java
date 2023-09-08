package vip.yzxh.ZipFile.Zip.Entity;


import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author YeungLuhyun
 **/
@Data
public class FileListBean {


    private String parent;

    private List<File> fileLit;

    private String zipName;

    private Long totalSize;

    public FileListBean() {
        this.fileLit = new ArrayList<>();
        this.totalSize = 0L;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName+".zip";
    }

    @Override
    public String toString() {
        return "FileListBean{" +
                "parent='" + parent + '\'' +
                ", fileLit=" + JSONArray.toJSONString(fileLit) +
                ", totalSize=" + totalSize +
                ", zipName="+zipName+
                '}';
    }
}
