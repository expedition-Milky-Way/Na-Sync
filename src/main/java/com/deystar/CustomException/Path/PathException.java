package com.deystar.CustomException.Path;

/**
 * @Author YeungLuhyun
 **/
public class PathException extends RuntimeException {

    private String message;

    public PathException(PathExceptionEnums enums) {
        super(enums.msg);
        this.message = enums.msg;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
