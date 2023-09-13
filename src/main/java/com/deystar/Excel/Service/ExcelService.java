package com.deystar.Excel.Service;

import com.deystar.Excel.Beans.FieldBean;

import java.util.List;

/**
 * @Author YeungLuhyun
 **/
public interface ExcelService {


    void todo(String outputFilePath, List<FieldBean> beanList);
}
