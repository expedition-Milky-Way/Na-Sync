package cn.deystar.Util.SplitFile.Bean;

import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 一个文件和文件的分片集合对象
 */
public class ChunkBean {


    private String fileName;

    private String digest;

    private List<TempBean> beanList;

    private String path;

    private Long size;

    private String absoluteFile;


    public String[] getBlockList() {
        if (beanList == null) return null;
        String[] result = new String[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            result[i] = beanList.get(i).getDigest();
        }
        return result;
    }

    public String getBlockString(Integer index) {
        if (beanList == null) return null;
        return beanList.get(index).getDigest();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public List<TempBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<TempBean> beanList) {
        this.beanList = beanList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getAbsoluteFile() {
        if (this.absoluteFile == null || this.absoluteFile.trim().isEmpty()) {
            if (this.getPath().endsWith("/") || this.getPath().endsWith("\\")) {
                this.setAbsoluteFile(this.getPath() + this.getFileName());
            } else {
                this.setAbsoluteFile((this.path.contains("\\") ? this.path + "\\" : this.path + "/")
                        + this.getFileName());
            }
        }
        return absoluteFile;
    }

    public void setAbsoluteFile(String absoluteFile) {
        this.absoluteFile = absoluteFile;
    }
}
