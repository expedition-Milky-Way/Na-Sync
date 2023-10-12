package cn.com.deystar.Zip.SevenZip.PackageRunnable.Core;

import cn.com.deystar.Const.CompressStatus;
import cn.com.deystar.Zip.Bean.FileListBean;
import cn.com.deystar.Zip.ZipFourJ.FileToZip;
import cn.com.deystar.ZipArgument.ZipArgument;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class ZipServiceImpl {

    protected  FileListBean bean;

    protected ZipArgument argument;

    protected String command;


    protected FileListBean start(String command) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            return FileToZip.zip((FileListBean) bean, argument);

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

    private void beforeError() {
        FileListBean bean = (FileListBean) this.bean;
        File file = new File(bean.getZipName());
        if (file.exists()) file.delete();
        bean.setStatus(CompressStatus.ERROR);

    }
}
