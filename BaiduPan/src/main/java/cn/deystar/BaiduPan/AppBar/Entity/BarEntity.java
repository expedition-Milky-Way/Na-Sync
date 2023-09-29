package cn.deystar.BaiduPan.AppBar.Entity;

import java.util.List;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class BarEntity {

    private String title;

    private Boolean isRoot;


    private List<BarEntity> subBar;

    private String url;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public List<BarEntity> getSubBar() {
        return subBar;
    }

    public void setSubBar(List<BarEntity> subBar) {
        this.subBar = subBar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsRoot() {
        return this.isRoot;
    }

    public void setIsRoot(Boolean isRoot) {
        this.isRoot = isRoot;
    }
}
