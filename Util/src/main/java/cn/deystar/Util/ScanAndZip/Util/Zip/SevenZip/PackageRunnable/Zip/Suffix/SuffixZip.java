package cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.Suffix;


import cn.deystar.Util.ScanAndZip.Util.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.Util.ZipArgument.ZipArgument;

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
