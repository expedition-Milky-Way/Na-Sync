package cn.deystar.BaiduPan;

import javafx.collections.transformation.FilteredList;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 合并文件
 */
public class MergeFile {
    private List<File> destPaths;

    @Test
    public void mergeFile() {
        List<File> files = Arrays.asList(new File("C:\\Users\\YeungLuhyun\\Desktop\\测试toZip\\2023-10-4_修图").listFiles());
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String file1Name = o1.getName().split("\\.")[0];
                String file2Name = o2.getName().split("\\.")[0];
                String[] name1Strs = file1Name.split("_");
                String[] name2Strs = file2Name.split("_");
                file1Name = name1Strs[name1Strs.length - 1];
                file2Name = name2Strs[name2Strs.length - 1];
                return Integer.valueOf(file1Name).compareTo(Integer.valueOf(file2Name));
            }
        });
        this.destPaths = files;
        this.mergeFile("C:\\Users\\YeungLuhyun\\Desktop\\测试toZip\\合并\\xiu.zip");
    }

    /**
     * 文件的合并
     * destPath:目标路径+文件名
     */
    public void mergeFile(String destPath) {
        OutputStream os = null;
        SequenceInputStream sis = null; //序列流
        Vector<InputStream> vi = new Vector<InputStream>(); //流容器，这里Vector实现了List接口，所以此容器是有序的
        try {
            //输出流初始化
            os = new BufferedOutputStream(new FileOutputStream(destPath, true));
            //输入流初始化
            for (int i = 0; i < this.destPaths.size(); i++) {
                vi.add(new BufferedInputStream(new FileInputStream(destPaths.get(i))));
            }
            sis = new SequenceInputStream(vi.elements()); //vi.elements()获取容器的迭代对象并传入序列流构造器参数
            //读取后写出
            byte[] flushBuffer = new byte[1024];
            int readLen = -1;
            while ((readLen = sis.read(flushBuffer)) != -1) { //sis.read()会通过迭代器依次调用容器里每一个输入流对象的read()方法
                os.write(flushBuffer, 0, readLen);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sis != null) {
                try {
                    sis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    File[] sourceFiles(String directory) {
        return new File(directory).listFiles();
    }
}
