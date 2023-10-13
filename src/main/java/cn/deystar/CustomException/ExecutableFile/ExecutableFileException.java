package cn.deystar.CustomException.ExecutableFile;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class ExecutableFileException extends RuntimeException {

    private String message;

    public ExecutableFileException(String message){
        super(message);
        this.message = message;
    }

    public ExecutableFileException(ExecutableFileEnums executableFileEnums){
        super(executableFileEnums.meessage);
        this.message = executableFileEnums.meessage;
    }

    public String getMessage(){
        return this.message+"\n"+super.getMessage();
    }

}
