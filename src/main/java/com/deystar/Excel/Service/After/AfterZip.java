package com.deystar.Excel.Service.After;

import com.deystar.Excel.Beans.FieldBean;
import com.deystar.Excel.Service.ExcelService;
import com.deystar.Excel.Util.WriteService;

import java.util.List;

/**
 * @Author YeungLuhyun
 * 在压缩后输出excel
 **/
public class AfterZip implements ExcelService {


    @Override
    public void todo(String outputFilePath, List<FieldBean> beanList){
        WriteService.write(outputFilePath,beanList);
    }
}
