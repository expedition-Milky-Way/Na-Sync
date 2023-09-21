package com.deystar.Zip.SevenZip.RunZipTask;

import com.deystar.ZipArgument.ZipArgument;
import com.deystar.Zip.Entity.FileListBean;

/**
 * @Author YeungLuhyun
 **/
public class SuffixZip extends ZipAbstract implements Runnable {



    public void start() {
        super.start(command);
    }


    public SuffixZip(ZipArgument user, FileListBean bean,String command) {
        this.argument = user;
        this.bean = bean;
        this.command = command;
    }

    @Override
    public void run() {
        this.start(this.command);
    }


}
