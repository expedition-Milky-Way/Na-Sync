package com.deystar.UserTyper;

import lombok.Data;

/**
 * @Author YeungLuhyun
 **/
@Data
public class UserTyper {


    private String originPath;

    private String zipToPath;

    private String sevenZipPath;

    private Boolean isEncryption;

    private Boolean needEncryPath;

    private String password;


    private String excelOutput;

    private Long oneFileSize; // The value`s unit is Byte

    @Override
    public String toString() {
        return "UserTyper{" +
                "originPath='" + originPath + '\'' +
                ", zipToPath='" + zipToPath + '\'' +
                ", isEncryption=" + isEncryption +
                ", needEncryPath=" + needEncryPath +
                ", password='" + password + '\'' +
                ", excelOutput='" + excelOutput + '\'' +
                '}';
    }
}
