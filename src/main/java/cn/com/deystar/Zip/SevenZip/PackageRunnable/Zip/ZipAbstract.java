package cn.com.deystar.Zip.SevenZip.PackageRunnable.Zip;

import cn.com.deystar.CustomException.SevenZipException.SevenZipException;
import cn.com.deystar.CustomException.SevenZipException.SevenZipExceptionEnums;
import cn.com.deystar.Result.ResultState;
import cn.com.deystar.Zip.Bean.FileListBean;
import cn.com.deystar.Zip.ZipFourJ.FileToZip;
import cn.com.deystar.ZipArgument.ZipArgument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public abstract class ZipAbstract implements Runnable {
    protected FileListBean bean;

    protected ZipArgument argument;

    protected String command;


    protected void start(String command) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            FileToZip.zip(bean, argument);
            return;
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
                throw new SevenZipException(line + "\n" + SevenZipExceptionEnums.FILE_ERROR);
            }
            System.out.println(exitVal);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        ResultState.success(bean);
    }

    @Override
    public void run() {
        this.start(command);
    }


}
