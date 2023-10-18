package cn.deystar.Util.Beans.PathLinkList;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class PathLinkList {

    private String directory;

    private PathLinkList next;

    public PathLinkList(String directory){
        this.directory = directory;
    }

    public PathLinkList(){

    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public PathLinkList getNext() {
        return next;
    }

    public void setNext(PathLinkList next) {
        this.next = next;
    }
}
