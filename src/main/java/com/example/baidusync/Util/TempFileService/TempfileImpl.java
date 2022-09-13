package com.example.baidusync.Util.TempFileService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author 杨名 （字 露煊）
 * 文件切片
 */
public class TempfileImpl {

    @Resource
    private FileSettingMapping settingMapping;
    @Resource
    private RequestNetDiskService netDiskService;

    private static String CACHE_PATH = null;
    /**
     * 通过netDiskService.VIP_TYPE获取
     */
    private static Long[] SIZE_BY_VIP_TYPE = {4194304L,16777216L,33554432L};

    private static Long SIZE = 0L;

    /**
     * 默认分片存放文件夹
     */
    private static String TEMP_FILE_CACHE = "/temp";

    /**
     * 切片
     */
    public void splitFile(){
        /*
        获取用户信息和可用分片大小
         */
        //获取用户信息
        netDiskService.getBaiduUsInfo();
        //获取分片大小
        SIZE = SIZE_BY_VIP_TYPE[RequestNetDiskImpl.VIP_TYPE];
        if (CACHE_PATH == null){ //获取路径
            LambdaQueryWrapper<FileSetting> lambda = new LambdaQueryWrapper<>();
            lambda.orderBy(true,false,FileSetting::getId).last("LIMIT 1");
            CACHE_PATH = settingMapping.selectOne(lambda).getCachePath();
        }
        //查看有没有用来存放temp文件的位置
        File cacheDir = new File(CACHE_PATH+TEMP_FILE_CACHE);
        if (!cacheDir.exists() && !cacheDir.isDirectory()){
            cacheDir.mkdir();
        }

    }


    /**
     * 切分并写文件
     */


    /**
     * 读文件
     */

}
