package cn.com.deystar.Zip.SevenZip.PackageRunnable.Zip.Suffix;

import cn.com.deystar.Zip.Bean.FileListBean;
import cn.com.deystar.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.com.deystar.ZipArgument.ZipArgument;

/**
 * @Author YeungLuhyun
 **/
public class SuffixZip extends ZipAbstract {



    public SuffixZip(ZipArgument argument, FileListBean bean, String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }



}
