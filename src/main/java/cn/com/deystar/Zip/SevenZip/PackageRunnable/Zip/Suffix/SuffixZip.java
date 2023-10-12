package cn.com.deystar.Zip.SevenZip.PackageRunnable.Zip.Suffix;

import cn.com.deystar.Zip.Bean.FileListBean;
import cn.com.deystar.Zip.SevenZip.PackageRunnable.Core.ZipServiceImpl;
import cn.com.deystar.ZipArgument.ZipArgument;

import java.util.concurrent.Callable;

/**
 * @Author YeungLuhyun
 **/
public class SuffixZip extends ZipServiceImpl implements Callable<FileListBean> {


    public SuffixZip(ZipArgument argument, FileListBean bean, String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }


    @Override
    public FileListBean call()  {
        return  this.start(command);
    }
}
