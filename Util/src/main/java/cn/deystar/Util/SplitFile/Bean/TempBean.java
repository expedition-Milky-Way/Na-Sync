package cn.deystar.Util.SplitFile.Bean;

import java.io.File;
import java.io.OutputStream;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 文件块对象
 */
public class TempBean {


    /**
     * 摘要 md5
     */
    private String digest;


    /**
     * 分块文件
     */
    private File chunk;


    public TempBean() {
    }

    public TempBean(String digest, File chunk) {
        this.digest = digest;
        this.chunk = chunk;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public File getChunk() {
        return chunk;
    }

    public void setChunk(File chunk) {
        this.chunk = chunk;
    }
}
