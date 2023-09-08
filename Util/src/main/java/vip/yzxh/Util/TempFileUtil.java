package vip.yzxh.Util;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author YeungLuhyun
 **/
public class TempFileUtil {

    /**
     /**
     * 切片
     */
//    public void splitFile(File file) {
//        //获取最大分片大小
//        SIZE = SysConst.getMaxTempSize();
//        //获取文件，计算大小
//        String filePath = SysUtil.reNamePath(file.getPath());
//        String parent = SysUtil.reNamePath(file.getParent());
//        String fileName = file.getName();
//
//        File oneTempFileDir = new File(TEMP_FILE_DIR + fileName);
//
//        Long fileSize = FileUtils.sizeOf(file);
//        if (fileSize <= SysConst.getMinSize()) {
//            //直接上传
//            System.out.println("文件" + fileName + "≤ 4MB，直接上传");
//            fileService.computedMD5(fileName, file, fileSize, parent);
//        } else {
//            if (!oneTempFileDir.exists()) {
//                oneTempFileDir.mkdir();
//            }
//            String splitFileName = SysUtil.reNamePath(oneTempFileDir.toString()) + "/" + fileName;//分片文件的绝对路径
//            BigDecimal total = new BigDecimal(fileSize).divide(new BigDecimal(SIZE), 0, BigDecimal.ROUND_UP);
//            Integer count = total.intValue(); //分多少片
//            RandomAccessFile raf = null;
//            try {
//                raf = new RandomAccessFile(file, "r");
//                long maxSize = fileSize / count;
//                long offSet = 0L;
//                for (int i = 0; i < count; i++) {
//                    long begin = offSet;
//                    long end = (i + 1) * maxSize;
//                    offSet = write(splitFileName, raf, i, begin, end);
//                }
//                //计算md5并放入队列
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(400);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
////            fileService.computedMD5(fileName, oneTempFileDir, fileSize, parent);
//        }
//
//    }
//
//
//    /**
//     * 切分并写文件
//     *
//     * @param filename 文件名
//     * @param raf      切片文件
//     * @param index    索引
//     * @param end      索引
//     */
//    private Long write(String filename, RandomAccessFile raf, int index, long begin, long end) {
//        long enPointer = 0L; //结尾指针
//        try {
//            RandomAccessFile in = raf;
//            RandomAccessFile out = new RandomAccessFile(new File(filename + "_" + index + ".tmp"), "rw");
//            byte[] bytes = new byte[Math.toIntExact(SIZE)];
//            int n = 0;
//            in.seek(begin);
//            while (in.getFilePointer() <= end && (n = in.read(bytes)) != -1) {
//                out.write(bytes, 0, n);
//            }
//            enPointer = in.getFilePointer();
//            out.close();
//        } catch (Exception e) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm");
//            System.out.println(
//                    dateFormat.format(new Date()) + "对文件" + filename + "进行分片失败：\n" + e);
//            e.printStackTrace();
//        }
//        return enPointer;
//    }


}
