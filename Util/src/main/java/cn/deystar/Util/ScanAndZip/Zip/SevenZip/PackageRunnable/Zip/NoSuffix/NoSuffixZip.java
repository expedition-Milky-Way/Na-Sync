package cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.NoSuffix;


import cn.deystar.Util.ScanAndZip.Const.Suffix;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.ZipArgument.ZipArgument;

import java.io.File;
import java.util.concurrent.Future;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class NoSuffixZip extends ZipAbstract {



    public NoSuffixZip(ZipArgument argument, FileListBean bean, String command) {
        this.argument = argument;
        this.bean = bean;
        this.command = command;
    }

    @Override
    public FileListBean call() {
        FileListBean result = super.call();
        File origin = new File(bean.getZipName());
        String renameTo = bean.getZipName().replace(Suffix.ZIP.value, Suffix.NULL.value);
        origin.renameTo(new File(renameTo));
        return result;
    }
}
