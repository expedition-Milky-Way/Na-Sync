package cn.com.deystar.CustomException.Argurment;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public enum ArgumentExceptionEnums {

    IS_NULL(" is null or is empty"),

    ARGUMENT_NULL(" The function`s argument is null or is empty");

    public String message;

    ArgumentExceptionEnums(String message) {
        this.message = message;
    }
}
