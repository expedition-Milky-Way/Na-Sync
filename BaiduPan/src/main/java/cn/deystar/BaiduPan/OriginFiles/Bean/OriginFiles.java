package cn.deystar.BaiduPan.OriginFiles.Bean;

import cn.deystar.Util.BaiduPanResponse.OriginFileResponse;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class OriginFiles extends OriginFileResponse {

    /**
     * 文件呈现图片
     */
    private String img;

    public OriginFiles() {
        super();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
