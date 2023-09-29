package cn.deystar.Util.ScanAndZip.Util.Const;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public enum SystemEnums {


    Linux(0, "Linux/7zz"),
    Windows(1, "Windows/7z.exe");

    public String bashFile;

    public Integer index;

    SystemEnums(Integer index, String bashFile) {
        this.index = index;
        this.bashFile = bashFile;
    }
}
