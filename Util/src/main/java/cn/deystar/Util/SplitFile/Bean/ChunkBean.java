package cn.deystar.Util.SplitFile.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 一个文件和文件的分片集合对象
 */
public class ChunkBean {

    /**
     * 被分片文件的文件名
     */
    private String fileName;

    /**
     * 被分片文件的md5摘要
     */
    private String digest;

    /**
     * 未排序的分片对象
     */
    private List<TempBean> beanList;

    /**
     * 分片在本地的路径
     */
    private String path;

    /**
     * 所有分片的总容量大小
     */
    private Long size;

    /**
     * 绝对路径
     */
    private String absoluteFile;

    /**
     * 远程文件夹
     */
    private String originPath;

    /**
     * 远程文件夹+文件名
     */
    private String originFileName;

    public List<String> getBlockList() {
        if (beanList == null) return null;
        String[] result = new String[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            result[i] = beanList.get(i).getDigest();
        }
        return new ArrayList<>(Arrays.asList(result));
    }

    /**
     * 通过索引获取blockList中的摘要
     * @param index
     * @return
     */
    public String getBlockString(Integer index) {
        if (beanList == null) return null;
        return beanList.get(index).getDigest();
    }

    /**
     * 将blocklist转化为String
     * @return
     */
    public String getBlockListString(){
        StringBuilder blockListString = new StringBuilder("[");
        for (int i = 0; i< beanList.size();i++){
            blockListString.append("\"").append(beanList.get(i).getDigest()).append("\"");
            if (i != beanList.size() - 1){
                blockListString.append(",");
            }
        }
        blockListString.append("]");
        return blockListString.toString();
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (this.originPath != null && !this.originPath.isEmpty()) {
            this.setOriginFileName(originPath.endsWith("/") || originPath.endsWith("\\") ?
                    originPath + fileName : originPath + "/" + fileName);
        }
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

    public void addBean(TempBean tempBean) {
        if (this.beanList == null) this.beanList = new ArrayList<>();
        this.beanList.add(tempBean);
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

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        if (this.fileName != null && !this.fileName.isEmpty()) {
            this.setOriginFileName(originPath.endsWith("/") || originPath.endsWith("\\") ?
                    originPath+fileName : originPath+"/" + fileName);
        }
        this.originPath = originPath;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    private void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }
}
