package com.deystar.Scan;

import cn.hutool.core.util.IdUtil;
import com.deystar.CustomException.PathException.PathException;
import com.deystar.CustomException.PathException.PathExceptionEnums;
import com.deystar.ZipArgument.ZipArgument;
import com.deystar.Zip.Entity.FileListBean;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author Ming Yeung Luhyun (杨名 字露煊)
 **/
public class FileScan {

    private final List<FileListBean> fileListBeans = new ArrayList<>();
    private final Map<String, Integer> parentIndex = new HashMap<>();

    private final Set<String> nameSet = new HashSet<>();

    private ZipArgument user;

    /**
     * 扫描路径下的所有文件
     *
     * @param dir
     */
    public void scan(List<File> dir) {
        if (dir == null || dir.isEmpty()) return;
        for (File file : dir) {
            if (!file.exists() || !file.canRead()) continue;
            if (file.isDirectory()) {
                scan(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            } else {
                File renameFile = renameFile(file);
                this.appFileIntoList(renameFile);
            }
        }
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
        if (needEncryptionPath) return path + IdUtil.simpleUUID() + ".zip";
        //原名压缩
        String[] parentStr = parent.contains("\\") ? parent.split("\\\\") : parent.split("/");
        parent = parentStr.length > 2 ? parentStr[parentStr.length - 2] + "_" + parentStr[parentStr.length - 1] : parentStr[parentStr.length - 1];
        if (!nameSet.add(parent + ".zip")) {
            int index = 1;
            while (nameSet.add(parent + "(" + index + ").zip")) {
                index++;
            }
            path += parent + "(" + index + ").zip";
        } else {
            path += parent + ".zip";
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
            if (bean.getTotalSize() > user.getOneFileSize() || bean.getTotalSize() + fileSize > user.getOneFileSize()) {
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
        if (bean.getZipName() == null) {
            bean.setZipName(this.genZipName(parent, user.getZipToPath(), user.getPathAnonymity()));
        }

        bean.setOriginParent(parent);
        // 塞进bean里面
        if (bean.getParent() == null) bean.setParent(parent);
        bean.getFileLit().add(file);
        bean.setTotalSize(bean.getTotalSize() + fileSize);
        if (parentIndex.containsKey(parent)) {
            fileListBeans.set(parentIndex.get(parent), bean);
        } else {
            fileListBeans.add(bean);
            parentIndex.put(parent, fileListBeans.size() - 1);
        }
    }


    /**
     * 如果文件名存在空格，就将文件重命名为没有空格的文件
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

    public Queue<FileListBean> getTasks() {
        return new LinkedTransferQueue<>(fileListBeans);

    }

    public FileScan(ZipArgument userTyper) {
        this.user = userTyper;
    }
}
