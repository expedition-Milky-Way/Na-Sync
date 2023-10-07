package cn.deystar.Util.ScanAndZip.Zip.Bean;



import cn.deystar.Util.ScanAndZip.Const.CompressStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  YeungLuhyun
 **/

public class FileListBean {

    /**
     * 原文件路径
     */
    private String originParent;


    private String parent;

    /**
     * 原文件列表
     */
    private List<File> fileLit;

    /**
     * 压缩文件的绝对路径
     */
    private String zipName;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * task状态
     */
    private CompressStatus status;



    private Integer uploadPercent;
    public FileListBean() {
        this.fileLit = new ArrayList<>();
        this.totalSize = 0L;
    }

    public String getOriginParent() {
        return originParent;
    }

    public void setOriginParent(String originParent) {
        this.originParent = originParent;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<File> getFileLit() {
        return fileLit;
    }

    public void setFileLit(List<File> fileLit) {
        this.fileLit = fileLit;
    }

    public String getZipName() {
        return zipName;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public String toString() {
        return "{\"originParent\":\"" + originParent + "\"\n" +
                "\"parent\":\"" + parent + "\"\n" +
                "\"fileList\":" + fileLit + "\n" +
                "\"zipName\":" + zipName + "\n" +
                "\"totalSize\":" + totalSize + "\n}";
    }

    public CompressStatus getStatus() {
        return status;
    }

    public void setStatus(CompressStatus status) {
        this.status = status;
    }


    public Integer getUploadPercent() {
        return uploadPercent;
    }

    public void setUploadPercent(Integer uploadPercent) {
        this.uploadPercent = uploadPercent;
    }
}
