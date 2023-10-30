package cn.deystar.ScanUtil;

import cn.deystar.Compress.Bean.FileListBean;
import cn.deystar.CompressArgument.CompressArgument;
import cn.deystar.CustomException.PathException.PathException;
import cn.deystar.CustomException.PathException.PathExceptionEnums;
import cn.hutool.core.util.IdUtil;

import java.io.File;
import java.util.*;

/**
 * @author Ming Yeung Luhyun (杨名 字露煊)
 **/
public class FileScan {

    private final List<FileListBean> fileListBeans = new ArrayList<>();
    private final Map<String, Integer> parentIndex = new HashMap<>();

    private final Set<String> nameSet = new HashSet<>();

    private CompressArgument zipArgument;

    /**
     * scan all the file from directory
     *
     * @param files  a Directory
     */
    public FileScan scan(List<File> files) {
        if (files == null || files.isEmpty()) return null;
        for (File file : files) {
            if (!file.exists() || !file.canRead()) continue;
            if (file.isDirectory()) {
                scan(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            } else {
                File renameFile = renameFile(file);
                this.appFileIntoList(renameFile);
            }
        }
        return this;
    }

    /***
     *
     * @param parent 父路径
     * @param needEncryptionPath 是否需要匿名文件名
     * @return zipName
     */
    public String genZipName(String parent, String zipToPath, boolean needEncryptionPath) {
        if (parent == null || parent.trim().isEmpty()) {
            throw new PathException("parent", PathExceptionEnums.PATH_NULL);
        }
        if (zipToPath == null || zipToPath.trim().isEmpty()) {
            throw new PathException("zipToPath", PathExceptionEnums.PATH_NULL);
        }
        // 压缩包路径
        String path = zipToPath + "/";
        if (path.contains("\\")) path = path.replace("\\", "/");
        //匿名压缩包
        if (needEncryptionPath) return path + IdUtil.simpleUUID();
        //原名压缩
        String[] parentStr = parent.contains("\\") ? parent.split("\\\\") : parent.split("/");
        parent = parentStr.length > 2 ? parentStr[parentStr.length - 2] + "_" + parentStr[parentStr.length - 1] : parentStr[parentStr.length - 1];
        if (!nameSet.add(parent)) {
            int index = 1;
            while (nameSet.add(parent + "(" + index + ")")) {
                index++;
            }
            path += parent + "(" + index + ")";
        } else {
            path += parent;
        }
        return path;

    }

    /**
     * insert into file list
     *
     * @param file
     */
    private void appFileIntoList(File file) {
        String parent = file.getParent();
        Long fileSize = file.length();
        FileListBean bean = null;
        Integer index = null;
        if (parentIndex.containsKey(parent) && (index = parentIndex.get(parent)) >= 0) {
            bean = fileListBeans.get(index);
            if (bean.getTotalSize() > zipArgument.getFileMaxSize() || bean.getTotalSize() + fileSize > zipArgument.getFileMaxSize()) {
                Integer cf = 1;
                while (parentIndex.containsKey(parent + "_" + (cf + ""))) {
                    cf++;
                }
                parent = parent + "_" + (cf + "");
                bean = new FileListBean();
            }
        } else {
            bean = new FileListBean();
        }
        if (bean.getCompressName() == null) {
            bean.setCompressName(this.genZipName(parent, zipArgument.getPackageStorage(), zipArgument.getNameAnonymity()));
        }

        bean.setSourceParent(parent);
        // 塞进bean里面
        if (bean.getParent() == null) bean.setParent(parent);
        bean.getFileList().add(file);
        bean.setTotalSize(bean.getTotalSize() + fileSize);
        if (parentIndex.containsKey(parent)) {
            fileListBeans.set(parentIndex.get(parent), bean);
        } else {
            fileListBeans.add(bean);
            parentIndex.put(parent, fileListBeans.size() - 1);
        }
    }


    /**
     * if the file`s name has space.
     * the file`s name wille be renamed to no space name
     *
     * @return
     */
    private File renameFile(File file) {
        if (file != null && file.exists()) {
            StringBuilder reto = new StringBuilder();
            for (String single : file.getName().split("\\s+")) {
                reto.append(single);
            }
            String name = file.getParent().replace("\\", "/") + "/" + reto;
            File renameFile = new File(name);
            if (file.renameTo(renameFile)) {
                file = renameFile;
            }
        }
        return file;
    }


    public List<FileListBean> getList(){
        return fileListBeans;
    }

    public FileScan(CompressArgument zipArgument) {
        this.zipArgument = zipArgument;
    }
}
