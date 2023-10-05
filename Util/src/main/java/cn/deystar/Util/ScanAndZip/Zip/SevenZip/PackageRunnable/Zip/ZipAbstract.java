package cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip;


import cn.deystar.Util.ScanAndZip.Const.CompressStatus;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.deystar.Util.ScanAndZip.Zip.ZipFourJ.FileToZip;
import cn.deystar.Util.ScanAndZip.ZipArgument.ZipArgument;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public abstract class ZipAbstract implements Callable<FileListBean> {
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
        synchronized (bean){
            bean.setStatus(CompressStatus.SUCCESS);
        }
        return bean;
    }

    @Override
    public FileListBean call() {
        return this.start(command);
    }

    private void beforeError() {
        synchronized (this.bean){
            this.bean.setStatus(CompressStatus.ERROR);
        }
        File file = new File(bean.getZipName());
        if (file.exists()) file.delete();
        bean.setStatus(CompressStatus.ERROR);

    }


    public FileListBean getBean(){
        return this.bean;
    }


    public void setStatus(CompressStatus status){
        synchronized (this.bean){
            this.bean.setStatus(status);
        }
    }

}
