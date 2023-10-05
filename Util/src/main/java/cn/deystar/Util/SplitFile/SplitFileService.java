package cn.deystar.Util.SplitFile;

import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.SplitFile.Bean.ChunkBean;
import cn.deystar.Util.SplitFile.Bean.TempBean;
import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public synchronized ChunkBean splitFile(FileListBean fileListBean, String outPut, Integer chunkSize) {
        //1. 判断缓存路径是否存在，如果不存在就创建路径
        if (outPut == null || outPut.trim().isEmpty()) return null;
        if (fileListBean == null) return null;
        if (chunkSize == null || chunkSize < 1L) return null;
        File directory = new File(outPut);
        if (!directory.exists()) directory.mkdirs();

        //2. 计算分片个数  单个文件的大小 / chunkSize;
        BigDecimal fileSize = new BigDecimal(fileListBean.getTotalSize());
        BigDecimal chunkSizeDecimal = fileSize.divide(new BigDecimal(chunkSize), RoundingMode.HALF_DOWN);
        Long chunkNum = chunkSizeDecimal.longValue();

        // 3. 开始分片
        ChunkBean chunkBean = new ChunkBean();
        chunkBean.setSize(Math.toIntExact(fileListBean.getTotalSize()));
        chunkBean.setPath(outPut);

        chunkBean.setDigest(DigestUtil.md5Hex(new File(fileListBean.getZipName())));
        List<TempBean> tempBeans = new ArrayList<>();
        RandomAccessFile sourceFile = null;

        try {
            sourceFile = new RandomAccessFile(fileListBean.getZipName(), "r");
            long offSet = 0L;
            for (int i = 0; i < chunkNum; i++) {
                //切割
                String chunkName = chunkNameGenerator(outPut, fileListBean.getZipName(), i);
                long begin = offSet;
                long end = (long) (i + 1) * chunkSize;
                offSet = write(chunkName, chunkSize, sourceFile, i, begin, end);
                //切割完成后塞进对象
                TempBean tempBean = new TempBean();
                File chunkFile = new File(chunkName);
                tempBean.setChunk(chunkFile);
                tempBean.setDigest(DigestUtil.md5Hex(chunkFile));
                tempBeans.add(tempBean);

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (sourceFile != null) {
                try {
                    sourceFile.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        chunkBean.setBeanList(tempBeans);
        String fileName = fileListBean.getZipName();
        if (fileName.contains("\\") || fileName.contains("/")) {
            fileName = fileName.replace("\\", "/");
            String[] fileNameStrs = fileName.split("/");
            fileName = fileNameStrs[fileNameStrs.length - 1];
        }
        chunkBean.setFileName(fileName);

        return chunkBean;
    }


    private String chunkNameGenerator(String outPut, String zipNameAndPath, Integer chunkIndex) {
        StringBuilder pathBuilder = null;
        String name = null;

        if (outPut.endsWith("/"))
            if (zipNameAndPath.contains("\\"))
                zipNameAndPath = zipNameAndPath.replace("\\", "/");
        if (outPut.contains("\\"))
            outPut = outPut.replace("\\", "/");
        pathBuilder = new StringBuilder(outPut);
        if (!outPut.endsWith("/")) pathBuilder.append("/");

        List<String> strs = Arrays.asList(zipNameAndPath.split("/"));
        name = strs.get(strs.size() - 1);
        if (name.contains(".")) name = name.split("\\.")[0];

        pathBuilder.append(name);
        if (chunkIndex > 0) {
            pathBuilder.append("(").append(chunkIndex).append(")");
        }
        return pathBuilder.toString();
    }

    /**
     * 切分并写文件
     *
     * @param filename 文件名
     * @param raf      切片文件
     * @param index    索引
     * @param end      索引
     */
    private Long write(String filename, Integer size, RandomAccessFile raf, int index, long begin, long end) {
        long enPointer = 0L; //结尾指针
        try {
            RandomAccessFile in = raf;
            RandomAccessFile out = new RandomAccessFile(new File(filename + "_" + index + ".tmp"), "rw");
            byte[] bytes = new byte[size];
            int n = 0;
            in.seek(begin);
            while (in.getFilePointer() <= end && (n = in.read(bytes)) != -1) {
                out.write(bytes, 0, n);
            }
            enPointer = in.getFilePointer();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return enPointer;
    }


}
