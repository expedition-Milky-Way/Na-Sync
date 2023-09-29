package cn.deystar.Util.Beans.Upload;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class UploadBean {


    /**
     * 分片开始位置
     */
    private Integer partseq;

    /**
     * 上传的文件toChar
     */
    private char[] file;

    /**
     * 分片摘要 MD5
     * @return
     */
    private String digest;

    public Integer getPartseq() {
        return partseq;
    }

    public void setPartseq(Integer partseq) {
        this.partseq = partseq;
    }

    public char[] getFile() {
        return file;
    }

    public void setFile(char[] file) {
        this.file = file;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
