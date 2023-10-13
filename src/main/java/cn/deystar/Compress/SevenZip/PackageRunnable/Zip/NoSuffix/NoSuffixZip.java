package cn.deystar.Compress.SevenZip.PackageRunnable.Zip.NoSuffix;

import cn.deystar.Compress.Bean.FileListBean;
import cn.deystar.CompressArgument.CompressArgument;
import cn.deystar.Const.Suffix;
import cn.deystar.Compress.SevenZip.PackageRunnable.Common.ZipServiceImpl;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class NoSuffixZip extends ZipServiceImpl implements Callable<FileListBean> {



    public NoSuffixZip(CompressArgument argument, FileListBean bean, String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }

    @Override
    public FileListBean call() {

        File origin = new File(bean.getCompressName());
        String renameTo = bean.getCompressName().replace(Suffix.ZIP.value, Suffix.NULL.value);
        origin.renameTo(new File(renameTo));
        return this.start(command);
    }
}
