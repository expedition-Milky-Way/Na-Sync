package com.deystar.Zip.SevenZip.PackageRunnable.Zip.Suffix;

import com.deystar.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import com.deystar.ZipArgument.ZipArgument;
import com.deystar.Zip.Bean.FileListBean;

/**
 * @Author YeungLuhyun
 **/
public class SuffixZip extends ZipAbstract {



    public SuffixZip(ZipArgument argument, FileListBean bean,String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }



}
