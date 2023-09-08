package vip.yzxh.ZipFile.CustomException.Size;

/**
 * @Author YeungLuhyun
 **/
public class SizeException extends RuntimeException{

    private String message;

    public SizeException(SizeExceptionEnums enums) {
        super(enums.message);
        this.message = enums.message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
