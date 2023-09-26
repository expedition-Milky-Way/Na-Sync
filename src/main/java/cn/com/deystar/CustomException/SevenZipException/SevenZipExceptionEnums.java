package cn.com.deystar.CustomException.SevenZipException;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public enum SevenZipExceptionEnums {

    FILE_ERROR("cannot find the file or this once name  has blank space"),
    FILE_NAME_HAS_SPACE("the scan`s file has a space"),
    COMMAND_TO0_LONG("Need compresses file is too many. terminal cannot run this command"),

    FILE_NOT_FOUND("File not found");

    public String message;

    SevenZipExceptionEnums(String message){
        this.message = message;
    }
}
