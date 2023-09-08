package vip.yzxh.ZipFile.Zip.Entity;

import java.util.List;

/**
 * @Author YeungLuhyun
 **/
public class FileBean {


    private List<FileListBean> fileListBeanList;

    private Long totalSize;

    public FileBean(){

    }


    public List<FileListBean> getFileListBeanList() {
        return fileListBeanList;
    }

    public void setFileListBeanList(List<FileListBean> fileListBeanList) {
        this.fileListBeanList = fileListBeanList;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
}
