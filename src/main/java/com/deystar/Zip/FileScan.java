package com.deystar.Zip;

import cn.hutool.core.util.IdUtil;
import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;
import org.ehcache.xml.model.Heap;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @Author YeungLuhyun
 **/
public class FileScan {

    private List<FileListBean> fileListBeans = new ArrayList<>();
    private Map<String, Integer> parentIndex = new HashMap<>();


    private Boolean needEncryptionPath = false;

    private UserTyper user;

    /**
     * 扫描路径下的所有文件
     *
     * @param dir
     */
    public void scan(List<File> dir) {
        for (File file : dir) {
            if (file.isDirectory()) {
                scan(Arrays.asList(file.listFiles()));
            } else {

                String parent = file.getParent();
                String simpleUUID = genZipName(parent, user.getZipToPath(), user.getNeedEncryPath());
                Long fileSize = file.length();
                FileListBean bean = null;
                if (parentIndex.containsKey(parent)) {
                    Integer index = parentIndex.get(parent);
                    if (index >= 0) {
                        bean = fileListBeans.get(index);
                    }
                } else {
                    bean = new FileListBean();
                    bean.setZipName(simpleUUID);
                }
                bean.setOriginParent(parent);
                // 检查一下总大小是否超过了可以压缩的大小
                if (bean.getTotalSize() > user.getOneFileSize()
                        || bean.getTotalSize() + fileSize > user.getOneFileSize()) {
                    bean = new FileListBean();
                    bean.setZipName(simpleUUID);
                    Integer cf = 1;
                    while (parentIndex.containsKey(parent + "_" + (cf + ""))) {
                        cf++;
                    }
                    parent = parent + "_" + (cf + "");
                }
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
        }
    }

    /***
     *
     * @param parent 父路径
     * @param needEncryptionPath 是否需要匿名文件名
     * @return
     */
    public String genZipName(String parent, String zipToPath, boolean needEncryptionPath) {
        // 压缩包路径
        String path = zipToPath + "/";
        if (path.contains("\\")) path = path.replace("\\", "/");
        //匿名压缩包
        if (needEncryptionPath) return path + IdUtil.simpleUUID() + ".zip";
        //原名压缩
        String[] parentStr = parent.contains("\\") ? parent.split("\\\\") : parent.split("/");
        parent = parentStr.length > 1 ? parentStr[parentStr.length - 2] + "_" + parentStr[parentStr.length - 1] : parentStr[parentStr.length - 1];
        if (new File(parent + ".zip").exists()) {
            Integer index = 1;
            while (new File(parent + "(" + index + ").zip").exists()) {
                index++;
            }
            path += parent + "(" + index + ").zip";
        } else {
            path += parent + ".zip";
        }
        return path;

    }


    public Queue<FileListBean> getTasks() {
        Queue<FileListBean> queue = new LinkedTransferQueue<>();
        queue.addAll(this.fileListBeans);
        return queue;
    }

    public FileScan(UserTyper userTyper) {
        this.user = userTyper;
        needEncryptionPath = userTyper.getNeedEncryPath();
    }
}
