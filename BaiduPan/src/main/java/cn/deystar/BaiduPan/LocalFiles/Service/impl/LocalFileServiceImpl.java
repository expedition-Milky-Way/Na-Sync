package cn.deystar.BaiduPan.LocalFiles.Service.impl;

import cn.deystar.BaiduPan.LocalFiles.Service.LocalFileService;
import cn.deystar.Util.Beans.LocalFiles.LocalFile;
import cn.deystar.Util.Util.LocalFileScan;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Service
public class LocalFileServiceImpl implements LocalFileService {
    @Override
    public List<List<LocalFile>> listAll(String path) {
        List<LocalFile> localFiles = LocalFileScan.scan(path);
        List<List<LocalFile>> finalLocalFiles = new ArrayList<>();
        Integer rowMax = 12; //单行最多
        Integer size = localFiles.size();

        if (size <= rowMax) {
            finalLocalFiles.add(localFiles);
        } else {
            Integer rowNum = new BigDecimal(size).divide(new BigDecimal(rowMax), RoundingMode.UP).intValue(); //行数
            for (int i = 0; i< rowNum;i++) {
                Integer toIndex = i* rowMax + rowMax;
                if (toIndex > size) toIndex = size;
                Integer fromIndex = i*rowMax;
                List<LocalFile> subList = localFiles.subList(fromIndex,toIndex);
                finalLocalFiles.add(subList);
            }
        }
        return finalLocalFiles;
    }
}
