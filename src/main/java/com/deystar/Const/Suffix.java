package com.deystar.Const;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 *  package `s suffix
 */
public enum Suffix {

    ZIP(".zip"),

    NULL("");


    public String value;

    Suffix(String value) {
        this.value = value;
    }
}
