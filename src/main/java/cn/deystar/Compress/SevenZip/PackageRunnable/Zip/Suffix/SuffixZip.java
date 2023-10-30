package cn.deystar.Compress.SevenZip.PackageRunnable.Zip.Suffix;

import cn.deystar.Compress.Bean.FileListBean;
import cn.deystar.Compress.SevenZip.PackageRunnable.Common.ZipServiceImpl;
import cn.deystar.CompressArgument.CompressArgument;

import java.util.concurrent.Callable;

/**
 * @Author YeungLuhyun
 **/
public class SuffixZip extends ZipServiceImpl implements Callable<FileListBean> {


    public SuffixZip(CompressArgument argument, FileListBean bean, String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }


    @Override
    public FileListBean call()  {
        return  this.start(command);
    }
}
