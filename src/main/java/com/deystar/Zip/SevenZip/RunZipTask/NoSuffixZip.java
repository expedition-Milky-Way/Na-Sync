package com.deystar.Zip.SevenZip.RunZipTask;

import com.deystar.Const.Suffix;

import java.io.File;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class NoSuffixZip extends ZipAbstract implements Runnable {


    public void start(String command) {
        super.start(command);
        File origin = new File(bean.getZipName());
        String renameTo = bean.getZipName().replace(Suffix.ZIP.value, Suffix.NULL.value);
        origin.renameTo(new File(renameTo));
    }

    @Override
    public void run() {

    }
}
