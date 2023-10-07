package cn.deystar.BaiduPan;

import cn.deystar.Util.Const.VipTypeEnums;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */

public class SplitFile {

    String fileName = "C:\\Users\\YeungLuhyun\\Desktop\\测试toZip\\2023-10-4_修图.zip";

    String outPut = "C:\\Users\\YeungLuhyun\\Desktop\\测试toZip\\";


    Integer chunkNums;
    Long chunkSize = VipTypeEnums.SVIP.tempSize;


    @Test
    public void split() {
        File sourceFile = genFile(fileName);
        String fileName = sourceFile.getName();
        System.out.println(fileName);
        String originOutPut = this.genOutPut(fileName);
        File outPutDirectory = new File(originOutPut);
        if (outPutDirectory.mkdirs()){
            System.out.println("输出路径创建成功");
            try {
                this.splitFile(sourceFile, originOutPut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("输出路径创建失败");
        }

    }

    public String genOutPut(String fileName) {
        //1. 获取文件名
        if (fileName.contains("\\") || fileName.contains("/")) {
            String[] strs = fileName.replace("\\", "/").split("/");
            fileName = strs[strs.length - 1];
        }
        //2.获取没有后缀的文件名
        String[] nameSplits = fileName.split("\\.");
        fileName = nameSplits[0];
        return outPut + fileName+"/"+fileName;

    }

    public File genFile(String name) {

        return new File(name);
    }


    public void splitFile(File sourceFile, String outPutFileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
        Long fileSize = sourceFile.length();

        chunkNums = new BigDecimal(fileSize).divide(new BigDecimal(chunkSize), RoundingMode.HALF_UP).intValue();
        long offset = 0L;
        for (int i = 0; i < chunkNums; i++) {
            long begin = offset;
            long end = (i + 1) * chunkSize;
            offset = write(outPutFileName, raf, i, begin, end);
        }

        raf.close();

    }

    private Long write(String filename, RandomAccessFile raf, int index, long begin, long end) {
        long enPointer = 0L;// 结尾指针
        try {
            RandomAccessFile in = raf;
            RandomAccessFile out = new RandomAccessFile(new File(filename + "_" + index + ".tmp"), "rw");
            byte[] bytes = new byte[Math.toIntExact(chunkSize)];
            int n = 0;
            in.seek(begin);
            while (in.getFilePointer() <= end && (n = in.read(bytes)) != -1) {
                out.write(bytes, 0, n);
            }
            enPointer = in.getFilePointer();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enPointer;
    }
}
