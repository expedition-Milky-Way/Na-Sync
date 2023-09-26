package cn.com.deystar.ZipArgument;

/**
 * @author Ming Yeung Luhyun (杨名 字露煊）
 **/

public class ZipArgument{

    /**
     * The path to be compressed
     */
    private String originPath;

    /**
     * zip file where storage
     */
    private String zipToPath;

    /**
     * zip file encryption
     */
    private Boolean isEncryption;

    /**
     * the Zip`s name need anonymity
     */
    private Boolean pathAnonymity;

    private String password;

    /**
     * Max File Size
     */
    private Long oneFileSize; // The value`s unit is Byte

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getZipToPath() {
        return zipToPath;
    }

    public void setZipToPath(String zipToPath) {
        this.zipToPath = zipToPath;
    }

    public Boolean getEncryption() {
        return isEncryption;
    }

    public void setEncryption(Boolean encryption) {
        isEncryption = encryption;
    }

    public Boolean getPathAnonymity() {
        return pathAnonymity;
    }

    public void setPathAnonymity(Boolean pathAnonymity) {
        this.pathAnonymity = pathAnonymity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getOneFileSize() {
        return oneFileSize;
    }

    public void setOneFileSize(Long oneFileSize) {
        this.oneFileSize = oneFileSize;
    }

    @Override
    public String toString() {
        return "{\"originPath\":\""+originPath+"\",\n"
                +"\"zipToPath\":\""+zipToPath+"\",\n"
                +"\"isEncryption\":\""+isEncryption+"\",\n"
                +"\"pathAnonymity\":\""+pathAnonymity+"\",\n"
                +"\"password\":\""+password+"\"\n}";
    }
}
