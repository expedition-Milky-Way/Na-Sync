package cn.deystar.Util.ScanAndZip.Util.Zip.SevenZip.PackageRunnable.Zip;



import cn.deystar.Util.ScanAndZip.Util.Const.CompressStatus;
import cn.deystar.Util.ScanAndZip.Util.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Util.Zip.ZipFourJ.FileToZip;
import cn.deystar.Util.ScanAndZip.Util.ZipArgument.ZipArgument;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public abstract class ZipAbstract implements Runnable {
    protected FileListBean bean;

    protected ZipArgument argument;

    protected String command;


    protected FileListBean start(String command) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            return FileToZip.zip(bean, argument);

        }
        InputStream stdIn = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdIn, Charset.forName("GBK"));
        BufferedReader br = new BufferedReader(isr);
        String line = null;

        try {
            while ((line = br.readLine()) != null)
                System.out.println(line);
            int exitVal = proc.waitFor();
            if (exitVal != 0) {
                beforeError();
            }
            System.out.println(exitVal);
        } catch (InterruptedException | IOException e) {
            beforeError();
        }
        return bean;
    }

    @Override
    public void run() {
        this.start(command);
    }

    private void beforeError() {
        File file = new File(bean.getZipName());
        if (file.exists()) file.delete();
        bean.setStatus(CompressStatus.ERROR);

    }


}
