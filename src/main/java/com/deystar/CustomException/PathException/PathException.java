package com.deystar.CustomException.PathException;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class PathException extends RuntimeException {


    public PathException(String argument, PathExceptionEnums enums) {
        super(argument + "\t" + enums.message);
    }
}
