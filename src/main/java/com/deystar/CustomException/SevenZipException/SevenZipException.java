package com.deystar.CustomException.SevenZipException;

import org.omg.SendingContext.RunTime;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class SevenZipException extends RuntimeException {

    private String message;

    public SevenZipException(String message){
        super(message);
    }


    public SevenZipException(SevenZipExceptionEnums enums){
        this.message = enums.message;
    }
    @Override
    public String toString(){
        return this.message+="\n"+ super.getMessage();
    }
}
