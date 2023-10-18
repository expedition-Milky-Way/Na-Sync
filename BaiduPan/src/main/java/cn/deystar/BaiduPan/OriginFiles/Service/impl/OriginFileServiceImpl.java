package cn.deystar.BaiduPan.OriginFiles.Service.impl;

import cn.deystar.BaiduPan.BaiduConst.BaiduConst;
import cn.deystar.BaiduPan.Core.BaiduRequest.NetDiskPath.NetDiskPathService;
import cn.deystar.BaiduPan.OriginFiles.Bean.OriginFiles;
import cn.deystar.BaiduPan.OriginFiles.Enums.Sort;
import cn.deystar.Util.BaiduPanResponse.OriginFileResponse;
import cn.deystar.BaiduPan.OriginFiles.Service.OriginFileService;
import cn.deystar.Util.Const.BaiduCategory;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Service
public class OriginFileServiceImpl implements OriginFileService {


    @Resource
    NetDiskPathService netDiskPathService;

    @Value("${baidu-netdisk.path}")
    private String originPath;

    @Override
    public List<List<OriginFiles>> listAll(String path,Sort sort) {
        if (path==null || path.trim().isEmpty()) path = originPath;
        List<OriginFileResponse> allFiles = netDiskPathService.listAll(path);
        List<List<OriginFiles>> result = new ArrayList<>();
        if (!allFiles.isEmpty()) {
            //排序
            if (sort != null) {
                Collections.sort(allFiles, new Comparator<OriginFileResponse>() {
                    @Override
                    public int compare(OriginFileResponse o1, OriginFileResponse o2) {
                        if (sort.method == Sort.CHANGED_TIME_AUGMENT.method) {
                            return o1.getServerChangedTime().compareTo(o2.getServerChangedTime());
                        } else if (sort.method == Sort.CHANGED_TIME_REDUCE.method) {
                            return -o1.getServerChangedTime().compareTo(o2.getServerChangedTime());
                        }

                        if (sort.method == Sort.SIZE_AUGMENT.method) {
                            return o1.getSize().compareTo(o2.getSize());
                        } else if (sort.method == Sort.SIZE_REDUCE.method) {
                            return -o1.getSize().compareTo(o2.getSize());
                        }
                        return 0;
                    }
                });
            }
            //分子数组
            Integer size = allFiles.size(); //总共数量
            Integer colMax = 12; //单行最大
            Integer colNum; // 行数
            if (size <= colMax) {
                colNum = 1;
            } else {
                colNum = (int) Math.ceil(size.doubleValue() / colMax.doubleValue());
            }
            Integer step = size / colNum;
            for (int i = 0; i < colNum; i+=step) {

                Integer toIndex = i * colMax + colMax;
                if (toIndex > allFiles.size()) toIndex = allFiles.size();
                List<OriginFileResponse> ofSubList = allFiles.subList(i * colMax, toIndex);

                List<OriginFiles> fileList = new ArrayList<>();

                ofSubList.forEach(item -> {
                    OriginFiles file = new OriginFiles();
                    BeanUtil.copyProperties(item, file);

                    if (file.getCategoryEnum().equals(BaiduCategory.VIDEO) ||
                            file.getCategoryEnum().equals(BaiduCategory.PICTURE) ||
                            file.getCategoryEnum().equals(BaiduCategory.TOR)) {
                        file.setImg(file.getThumbs());
                    }

                    if (file.getCategoryEnum().equals(BaiduCategory.ANOTHER))
                        file.setImg(BaiduCategory.ANOTHER.img);
                    if (file.getCategoryEnum().equals(BaiduCategory.MOVIE))
                        file.setImg(BaiduCategory.MOVIE.img);
                    if (file.getCategoryEnum().equals(BaiduCategory.DOCUMENTS))
                        file.setImg(BaiduCategory.DOCUMENTS.img);
                    if (file.getIsDir().equals(BaiduConst.IS_DIR_INT))
                        file.setImg(BaiduCategory.HAS_FILE_FOLDER.img);
                    fileList.add(file);
                });
                result.add(fileList);
            }
        }
        return result;
    }


}
