package com.deystar.Excel.Util;

import com.alibaba.excel.EasyExcel;
import com.deystar.Excel.Beans.FieldBean;
import com.deystar.Zip.Entity.FileBean;

import java.util.List;

/**
 * @Author YeungLuhyun
 * 写文件
 **/
public class WriteService {

    public static void write(String output, List<FieldBean> list){
        if (!output.contains(".xlsx")) output+=".xlsx";

        EasyExcel.write(output, FieldBean.class)
                .sheet("zip message")
                .doWrite(list);
    }



}
