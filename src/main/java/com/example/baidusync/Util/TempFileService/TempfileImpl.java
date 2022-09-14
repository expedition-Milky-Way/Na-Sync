package com.example.baidusync.Util.TempFileService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import org.apache.commons.io.FileUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;

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
        //获取文件，计算大小
        String filePath = "";
        File file = new File(filePath);
        String name = filePath.split("/")[filePath.length() - 1];
        Long fileSize = FileUtils.sizeOf(file);
        if (fileSize <= SIZE){
            //直接上传
        }else{
            BigDecimal total = new BigDecimal(fileSize).divide(new BigDecimal(SIZE),0,BigDecimal.ROUND_UP);
            Integer count = total.intValue(); //分多少片
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(file,"r");
                long maxSize = fileSize / count ;
                long offSet = 0L;
                for (int i= 0 ; i< count ; i++){
                    long begin = offSet;
                    long end = (i+1) * maxSize;
                    offSet = write(name,raf,i,begin,end);
                }
            }catch (Exception e){

            }
        }

    }


    /**
     * 切分并写文件
     * @param filename 文件名
     * @param file 文件片
     * @param index 索引
     * @param end 索引
     */
    private Long write(String filename,RandomAccessFile file,int index,long begin,long end){
        long enPointer = 0L; //结尾指针
        try {
            RandomAccessFile in = file;
            RandomAccessFile out = new RandomAccessFile(new File(filename +"_"+index+".tmp"),"rw");
            byte[] bytes = new byte[Math.toIntExact(SIZE)];
            int n = 0 ;
            in.seek(begin);
            while (in.getFilePointer() <= end && (n = in.read(bytes)) != -1){
                out.write(bytes,0,n);
            }
            enPointer = in.getFilePointer();
            out.close();
        }catch (Exception e){

        }
        return enPointer;
    }

    /**
     * 读文件
     */

}