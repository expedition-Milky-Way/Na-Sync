package cn.deystar.Compress.Bean;

import cn.deystar.Const.CompressStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  YeungLuhyun
 **/

public class FileListBean {

    private String sourceParent;
    private String parent;

    /**
     * this directory`s files
     */
    private List<File> fileList;

    /**
     * compress package name
     */
    private String compressName;


    private Long totalSize;


    private CompressStatus status;
    public FileListBean() {
        this.fileList = new ArrayList<>();
        this.totalSize = 0L;
    }

    public String getSourceParent() {
        return sourceParent;
    }

    public void setSourceParent(String originParent) {
        this.sourceParent = originParent;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileLit) {
        this.fileList = fileLit;
    }

    public String getCompressName() {
        return compressName;
    }

    public void setCompressName(String compressName) {
        this.compressName = compressName;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public String toString() {
        return "{\"sourceParent\":\"" + sourceParent + "\"\n" +
                "\"parent\":\"" + parent + "\"\n" +
                "\"fileList\":" + fileList + "\n" +
                "\"compressName\":" + compressName + "\n" +
                "\"totalSize\":" + totalSize + "\n}";
    }

    public CompressStatus getStatus() {
        return status;
    }

    public void setStatus(CompressStatus status) {
        this.status = status;
    }
}
