package cn.deystar.Util.Util;

import cn.deystar.Util.Beans.LocalFiles.LocalFile;
import cn.deystar.Util.Const.BaiduCategory;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 扫描文件路径
 */
public class LocalFileScan {


    public static List<LocalFile> scan(String path) {
        if (path == null || path.trim().isEmpty()) return new ArrayList<>();

        File directory = new File(path);
        if (directory == null || !directory.exists() || !directory.isDirectory())
            return new ArrayList<>();
        List<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));

        List<LocalFile> result = new ArrayList<>();
        Integer swapIndex = 0;
        for (File file : files) {
            String fileName = file.getName();
            String filePath = file.getPath();
            boolean isDirectory = file.isDirectory();
            boolean hasFile = false;
            if (isDirectory) {
                hasFile = file.listFiles().length > 0;
            }
            String img = isDirectory ?
                    (hasFile ? BaiduCategory.HAS_FILE_FOLDER.img : BaiduCategory.NON_FILE_FOLDER.img)
                    : BaiduCategory.ANOTHER.img;
            LocalFile localFile = new LocalFile(filePath, fileName, isDirectory, hasFile, img);
            result.add(localFile);
            //排序
            if (file.isDirectory()){
                if (result.size() -1 > swapIndex && !result.get(swapIndex).isDirectory()){
                    LocalFile cache = result.get(swapIndex);
                    result.set(swapIndex,result.get(result.size()-1));
                    result.set(result.size() - 1,cache);
                    swapIndex++;
                }
            }
        }
        return result;

    }


}
