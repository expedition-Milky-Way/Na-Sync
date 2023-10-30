package cn.deystar.CompressArgument;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class CompressArgument {

    /**
     * The path to be compressed
     */
    private String originPath;

    /**
     * compress file where storage
     */
    private String packageStorage;

    /**
     * zip file encryption
     */
    private Boolean isEncryption;

    /**
     * the Zip`s name need anonymity
     */
    private Boolean nameAnonymity;

    private String password;

    /**
     * Max File Size
     */
    private Long fileMaxSize; // The value`s unit is Byte

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getPackageStorage() {
        return packageStorage;
    }

    public void setPackageStorage(String zipToPath) {
        this.packageStorage = zipToPath;
    }

    public Boolean getEncryption() {
        return isEncryption;
    }

    public void setEncryption(Boolean encryption) {
        isEncryption = encryption;
    }

    public Boolean getNameAnonymity() {
        return nameAnonymity;
    }

    /**
     *  The compress file names needs to be  anonymity
     * @param pathAnonymity
     */
    public void setNameAnonymity(Boolean pathAnonymity) {
        this.nameAnonymity = pathAnonymity;
    }

    public String getPassword() {
        return password;
    }

    /**
     * If the Compress package needs to be encrypted.
     * If password is Null or null character string.The compress package will not be encrypted
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getFileMaxSize() {
        return fileMaxSize;
    }

    public void setFileMaxSize(Long oneFileSize) {
        this.fileMaxSize = oneFileSize;
    }

    @Override
    public String toString() {
        return "{\"originPath\":\""+originPath+"\",\n"
                +"\"packageStorage\":\""+packageStorage+"\",\n"
                +"\"isEncryption\":\""+isEncryption+"\",\n"
                +"\"nameAnonymity\":\""+nameAnonymity+"\",\n"
                +"\"password\":\""+password+"\"\n}";
    }
}
