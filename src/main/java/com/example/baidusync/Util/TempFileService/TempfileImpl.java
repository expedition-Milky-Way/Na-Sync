package com.example.baidusync.Util.TempFileService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Util.FileService.FileService;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;

/**
 * @author 杨名 （字 露煊）
 * 文件切片
 */
@Service
public class TempfileImpl implements TempFileService {

    @Resource
    private FileSettingMapping settingMapping;
    @Resource
    private RequestNetDiskService netDiskService;
    @Resource
    private FileService fileService;
    @Resource
    private RequestNetDiskService requestNetDiskService;

    private static String CACHE_PATH = null;


    private static Long SIZE = 0L;
    /**
     * 百度网盘最小分片大小. 假如文件大小 <= MIN_SIZE 直接上传
     */
    private static Long MIN_SIZE = 4194304L;

    /**
     * 默认分片存放文件夹
     */
    private static String TEMP_FILE_CACHE = "/temp";

    /**
     * 切片
     */
    public void splitFile(File file) {

        //获取用户信息
        netDiskService.getBaiduUsInfo();
        //获取分片大小
        SIZE = requestNetDiskService.getMaxTempSize();
        //查看有没有用来存放temp文件的位置
        String tempDir = CACHE_PATH + TEMP_FILE_CACHE;
        File cacheDir = new File(tempDir);
        if (!cacheDir.exists() && !cacheDir.isDirectory()) {
            cacheDir.mkdir();
        }
        //获取文件，计算大小
        String filePath = file.getPath();
        String parent = file.getParent();
        String name = filePath.split("/")[filePath.length() - 1];
        File oneTempFileDir = new File(tempDir + name);
        if (!oneTempFileDir.exists()) oneTempFileDir.mkdir();
        name = tempDir + oneTempFileDir;
        Long fileSize = FileUtils.sizeOf(file);
        if (fileSize <= MIN_SIZE) {
            //直接上传
            fileService.computedMD5(name, file, fileSize, parent);
        } else {
            BigDecimal total = new BigDecimal(fileSize).divide(new BigDecimal(SIZE), 0, BigDecimal.ROUND_UP);
            Integer count = total.intValue(); //分多少片
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(file, "r");
                long maxSize = fileSize / count;
                long offSet = 0L;
                for (int i = 0; i < count; i++) {
                    long begin = offSet;
                    long end = (i + 1) * maxSize;
                    offSet = write(name, raf, i, begin, end);
                }
                //计算md5并放入队列
                fileService.computedMD5(name, new File(tempDir + oneTempFileDir), fileSize, parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 切分并写文件
     *
     * @param filename 文件名
     * @param file     文件片
     * @param index    索引
     * @param end      索引
     */
    private Long write(String filename, RandomAccessFile file, int index, long begin, long end) {
        long enPointer = 0L; //结尾指针
        try {
            RandomAccessFile in = file;
            RandomAccessFile out = new RandomAccessFile(new File(filename + "_" + index + ".tmp"), "rw");
            byte[] bytes = new byte[Math.toIntExact(SIZE)];
            int n = 0;
            in.seek(begin);
            while (in.getFilePointer() <= end && (n = in.read(bytes)) != -1) {
                out.write(bytes, 0, n);
            }
            enPointer = in.getFilePointer();
            out.close();
        } catch (Exception e) {

        }
        return enPointer;
    }

    /**
     * 读取文件
     */
    @Override
    public void scanZipFile(File[] file) {
        if (CACHE_PATH == null) { //获取路径
            LambdaQueryWrapper<FileSetting> lambda = new LambdaQueryWrapper<>();
            lambda.orderBy(true, false, FileSetting::getId).last("LIMIT 1");
            CACHE_PATH = settingMapping.selectOne(lambda).getCachePath();
        }
        for (File item : file) {
            splitFile(item);
        }
    }

}
