package cn.com.deystar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class Rename {


    static List<FileObj> fileObjs = new ArrayList<>();

    public static void main(String[] args) {
        scan(Arrays.asList(new File("C:\\Users\\YeungLuhyun\\Desktop\\测试\\重要数据迁移").listFiles()));
        fileObjs.forEach(f -> {
            new File(f.originFile).renameTo(new File(f.renameTo));
        });
    }


    static void scan(List<File> fileList) {
        for (File file : fileList) {
            if (file.isDirectory()) {
                scan(Arrays.asList(file.listFiles()));
            } else {
                StringBuilder rename = new StringBuilder();
                for (String i : file.getName().split("\\s+")) {
                    rename.append(i);
                }
                String name = file.getParent().replace("\\", "/") + "/" + rename;
                FileObj obj = new FileObj(file.getAbsolutePath(), name);
                fileObjs.add(obj);
            }
        }
    }


    static class FileObj {

        private String originFile;

        private String renameTo;

        @Override
        public String toString() {
            return "FileObj{" +
                    "originFile='" + originFile + '\'' +
                    ", renameTo='" + renameTo + '\'' +
                    '}';
        }

        public FileObj(String originFile, String renameTo) {
            this.originFile = originFile;
            this.renameTo = renameTo;
        }
    }
}
