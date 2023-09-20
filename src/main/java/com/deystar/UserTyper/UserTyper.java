package com.deystar.UserTyper;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @Author YeungLuhyun
 **/
@Data
public class UserTyper {


    private String originPath;

    private String zipToPath;



    private Boolean isEncryption;

    private Boolean needEncryPath;

    private String password;


    private String excelPath;

    private String excelFile;
    private Long oneFileSize; // The value`s unit is Byte

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
