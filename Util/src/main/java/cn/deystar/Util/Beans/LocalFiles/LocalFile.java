package cn.deystar.Util.Beans.LocalFiles;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class LocalFile {

    private String name;

    private String path;

    private boolean isDirectory;

    private boolean unSync;

    private boolean hasFile;

    private String img;

    public LocalFile(String path, String name,boolean isDirectory,boolean hasFile,String img) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
        this.hasFile = hasFile;
        this.img = img;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }

    public boolean isUnSync() {
        return unSync;
    }

    public void setUnSync(boolean unSync) {
        this.unSync = unSync;
    }
}
