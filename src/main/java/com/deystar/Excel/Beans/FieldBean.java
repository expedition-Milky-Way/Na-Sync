package com.deystar.Excel.Beans;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.List;

/**
 * @Author YeungLuhyun
 **/
@EqualsAndHashCode
public class FieldBean {

    @ExcelProperty("压缩包名")
    private String zipName;

    @ExcelProperty("大小")
    private String size;



    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("压缩包取自路径")
    private String originParent;


    public String getZipName() {
        return zipName;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }





    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginParent() {
        return originParent;
    }

    public void setOriginParent(String originParent) {
        this.originParent = originParent;
    }


    public FieldBean(String zipName, String size, String password, String originParent) {
        this.zipName = zipName;
        this.size = size;

        this.password = password;
        this.originParent = originParent;
    }

    public FieldBean() {
    }
}
