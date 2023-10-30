package cn.deystar.Const;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * executable file enums
 */
public enum SystemEnums {


    Linux("Linux/7zz"),
    Windows("Windows/7z.exe");

    public String bashFile;

    SystemEnums(String bashFile) {
        this.bashFile = bashFile;
    }
}
