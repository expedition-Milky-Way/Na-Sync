package cn.com.deystar.Zip.SevenZip.PackageRunnable.Zip.NoSuffix;

import cn.com.deystar.Zip.Bean.FileListBean;
import cn.com.deystar.Const.Suffix;
import cn.com.deystar.Zip.SevenZip.PackageRunnable.Core.ZipServiceImpl;
import cn.com.deystar.ZipArgument.ZipArgument;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class NoSuffixZip extends ZipServiceImpl implements Callable<FileListBean> {



    public NoSuffixZip(ZipArgument argument, FileListBean bean, String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }

    @Override
    public FileListBean call() {

        File origin = new File(bean.getZipName());
        String renameTo = bean.getZipName().replace(Suffix.ZIP.value, Suffix.NULL.value);
        origin.renameTo(new File(renameTo));
        return this.start(command);
    }
}
