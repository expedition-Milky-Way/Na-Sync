package cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.NoSuffix;


import cn.deystar.Util.ScanAndZip.Util.Const.Suffix;
import cn.deystar.Util.ScanAndZip.Util.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.Util.ZipArgument.ZipArgument;

import java.io.File;

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
    public void run() {
        super.run();
        File origin = new File(bean.getZipName());
        String renameTo = bean.getZipName().replace(Suffix.ZIP.value, Suffix.NULL.value);
        origin.renameTo(new File(renameTo));
    }
}
