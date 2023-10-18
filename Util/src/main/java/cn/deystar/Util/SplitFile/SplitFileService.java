package cn.deystar.Util.SplitFile;

import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.*;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 分片工具类
 */
@Component
public class SplitFileService {


    /**
     * @param fileListBean Compress Task return`s instance
     * @param outPut       The file`s output directory
     * @param chunkSize    A Chunk`s size
     * @return
     */
    public  ChunkBean splitFile(FileListBean fileListBean, String outPut, Long chunkSize) throws IOException {
        //1. 判断缓存路径是否存在，如果不存在就创建路径
        if (outPut == null || outPut.trim().isEmpty()) return null;
        if (fileListBean == null) return null;
        if (chunkSize == null || chunkSize < 1L) return null;
        File directory = new File(outPut);
        if (directory.exists()) {
            return null;
        }
        if (!directory.mkdirs()) {
            throw new RuntimeException("create the directory error");
        }
        System.out.println("The " + outPut + " directory create success");

        //2. 计算分片个数  单个文件的大小 / chunkSize;
        int chunkNum = new BigDecimal(fileListBean.getTotalSize())
                .divide(new BigDecimal(chunkSize), RoundingMode.UP)
                .intValue();

        // 3. 开始分片
        ChunkBean chunkBean = new ChunkBean();
        chunkBean.setSize(fileListBean.getTotalSize());
        chunkBean.setPath(outPut);
        chunkBean.setDigest(DigestUtil.md5Hex(new File(fileListBean.getZipName())));

        //3.1切割
        RandomAccessFile sourceFile = new RandomAccessFile(fileListBean.getZipName(), "r");
        long offSet = 0L;
        for (int i = 0; i < chunkNum; i++) {

            String chunkName = chunkNameGenerator(outPut, i);
            long begin = offSet;
            long end = (i + 1) * chunkSize;
            offSet = write(chunkName, chunkSize, sourceFile, begin, end);
        }
        sourceFile.close();

        List<File> chunkFiles = sortChunkFile(new ArrayList<>(Arrays.asList(directory.listFiles())));
        for (File chunkFile : chunkFiles) {
            if (chunkFile.length() <= 0L){
                chunkFile.delete();
            }else{
                TempBean tempBean = new TempBean();
                tempBean.setChunk(chunkFile);
                tempBean.setDigest(DigestUtil.md5Hex(chunkFile));
                chunkBean.addBean(tempBean);
            }


        }


        // 3.获取分片文件的原文件
        String fileName = fileListBean.getZipName();
        if (fileName.contains("\\") || fileName.contains("/")) {
            fileName = fileName.replace("\\", "/");
            String[] fileNameStrs = fileName.split("/");
            fileName = fileNameStrs[fileNameStrs.length - 1];
        }

        chunkBean.setFileName(fileName);

        return chunkBean;

    }

    private List<File> sortChunkFile(List<File> chunkFiles) {
        if (chunkFiles == null) return chunkFiles;
        Collections.sort(chunkFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                Integer o1Index = Integer.valueOf(o1.getName().split("\\.")[0]);
                Integer o2Index = Integer.valueOf(o2.getName().split("\\.")[0]);
                return -o2Index.compareTo(o1Index);
            }
        });
        return chunkFiles;
    }

    private String chunkNameGenerator(String outPut, Integer index) {

        return (outPut.endsWith("\\") || outPut.endsWith("/") ? outPut + index : outPut + "/" + index)
                + ".tmp";

    }

    /**
     * write chunk file
     *
     * @param filename the file`s output name. those must contain the output path
     * @param raf      source file
     * @param begin    index
     * @param end      index
     */
    private Long write(String filename, long size, RandomAccessFile raf, long begin, long end) {
        long enPointer = 0L;// 结尾指针
        try {
            RandomAccessFile in = raf;
            RandomAccessFile out = new RandomAccessFile(new File(filename), "rw");
            byte[] bytes = new byte[1024]; //设置大了可能会出现问题
            int n = 0;
            in.seek(begin);

            while (in.getFilePointer() < end && (n = in.read(bytes)) != -1) {
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
