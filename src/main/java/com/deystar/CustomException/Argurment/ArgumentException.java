package com.deystar.CustomException.Argurment;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class ArgumentException extends RuntimeException {




    public ArgumentException(String message){
        super(message);
    }

    public ArgumentException(ArgumentExceptionEnums enums){
        super(enums.message);
    }


    public ArgumentException(String argumentName,ArgumentExceptionEnums enums){
        super(argumentName+enums);
    }
}
