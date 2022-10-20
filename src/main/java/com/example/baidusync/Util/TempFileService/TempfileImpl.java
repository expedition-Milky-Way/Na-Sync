package com.example.baidusync.Util.TempFileService;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Util.FileService.FileService;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SysUtil;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.core.Bean.SysConst;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.file.FileStore;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private static FileSetting fileSetting;

    private static Long SIZE = 0L;

    /**
     * 分片存放文件夹
     */
    public static String TEMP_FILE_DIR;

    /**
     * 切片
     */
    public void splitFile(File file) {
        //获取最大分片大小
        SIZE = SysConst.getMaxTempSize();
        //获取文件，计算大小
        String filePath = SysUtil.reNamePath(file.getPath());
        String parent = SysUtil.reNamePath(file.getParent());
        String fileName = file.getName();

        File oneTempFileDir = new File(TEMP_FILE_DIR + fileName);

        Long fileSize = FileUtils.sizeOf(file);
        if (fileSize <= SysConst.getMinSize()) {
            //直接上传
            LogEntity log = new LogEntity("","文件"+fileName+"≤ 4MB，直接上传",LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(log);
            fileService.computedMD5(fileName, file, fileSize, parent);
        } else {
            if (!oneTempFileDir.exists()){
                oneTempFileDir.mkdir();
            }
            String splitFileName = SysUtil.reNamePath(oneTempFileDir.toString())+"/"+fileName;//分片文件的绝对路径
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
                    offSet = write(splitFileName, raf, i, begin, end);
                }
                //计算md5并放入队列
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fileService.computedMD5(fileName, oneTempFileDir, fileSize, parent);
        }

    }


    /**
     * 切分并写文件
     *
     * @param filename 文件名
     * @param raf     切片文件
     * @param index    索引
     * @param end      索引
     */
    private Long write(String filename, RandomAccessFile raf, int index, long begin, long end) {
        long enPointer = 0L; //结尾指针
        try {
            RandomAccessFile in = raf;
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm");
            LogExecutor.addSysLogQueue(
                    new LogEntity("",
                            dateFormat.format(new Date())+"对文件"+filename+"进行分片失败：\n"+e,
                            LogEntity.LOG_TYPE_ERROR));
            e.printStackTrace();
        }
        return enPointer;
    }

    /**
     * 读取文件
     */
    @Override
    public void scanZipFile(File[] file) {
        if (fileSetting == null) { //获取路径
            LambdaQueryWrapper<FileSetting> lambda = new LambdaQueryWrapper<>();
            lambda.orderBy(true, false, FileSetting::getId).last("LIMIT 1");
            fileSetting = settingMapping.selectOne(lambda);
        }
        String cachePath = fileSetting.getCachePath();
        String[] cachePathChar = cachePath.split("");
        if (cachePathChar[cachePathChar.length-1] == "/" || cachePathChar[cachePathChar.length-1].equals("/")) {
            cachePath = cachePath.substring(0, cachePath.length() - 1);
        }
        TEMP_FILE_DIR = cachePath + SysConst.getTempPath();
        File temDir = new File(TEMP_FILE_DIR);
        if (!temDir.exists()){
            temDir.mkdirs();
        }
        int i = 0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (File item : file) {
            System.out.println(i+":::"+item.getName());
            if (!item.isDirectory())  splitFile(item);
            i++;
        }
    }



}
