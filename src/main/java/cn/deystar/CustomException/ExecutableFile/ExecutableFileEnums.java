package cn.deystar.CustomException.ExecutableFile;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * ExecutableFile Exeption
 */
public enum ExecutableFileEnums {


    CANT_RUN(" executable file can not run on this system"),
    NOT_FOUND("7z.exe OR 7ZZ NOT FOUND.CAN NOT FIND THE EXECUTABLE FILE");

    public String meessage;

    ExecutableFileEnums(String message){
        this.meessage = message;
    }
}
