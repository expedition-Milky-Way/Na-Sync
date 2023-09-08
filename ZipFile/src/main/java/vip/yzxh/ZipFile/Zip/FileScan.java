package vip.yzxh.ZipFile.Zip;

import cn.hutool.core.util.IdUtil;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.ZipFile.Zip.Entity.FileBean;
import vip.yzxh.ZipFile.Zip.Entity.FileListBean;


import java.io.File;
import java.util.*;

/**
 * @Author YeungLuhyun
 **/
public class FileScan {

    private List<FileListBean> fileListBeans = new ArrayList<>();
    private Map<String, Integer> parentIndex = new HashMap<>();


    private Boolean needEncryptionPath = false;

    private FileSetting fileSetting;

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
                String simpleUUID = IdUtil.simpleUUID();
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
                // 检查一下总大小是否超过了可以压缩的大小
                if (bean.getTotalSize() > fileSetting.getOneFileSize()
                        || bean.getTotalSize() + fileSize > fileSetting.getOneFileSize()) {
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
                    parentIndex.put(parent, fileListBeans.indexOf(bean));
                }
            }
        }
    }


    public FileBean getFiles() {
        FileBean bean = new FileBean();
        fileListBeans.forEach(item -> {
            if (bean.getTotalSize() == null || bean.getTotalSize() < 1L) {
                bean.setTotalSize(item.getTotalSize());
            } else {
                bean.setTotalSize(item.getTotalSize() + bean.getTotalSize());
            }
        });
        bean.setFileListBeanList(fileListBeans);
        return bean;
    }

    public FileScan(FileSetting setting) {
        this.fileSetting = setting;
        needEncryptionPath = setting.getPassword() == null || setting.getPassword().trim().isEmpty() ? false : true;
    }
}
