package cn.deystar.Compress.SevenZip.PackageRunnable.Common;

import cn.deystar.CompressArgument.CompressArgument;
import cn.deystar.Const.CompressStatus;
import cn.deystar.Compress.Bean.FileListBean;
import cn.deystar.Compress.ZipFourJ.FileToZip;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 公共
 */
public class ZipServiceImpl {

    protected  FileListBean bean;

    protected CompressArgument argument;

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
        File file = new File(bean.getCompressName());
        if (file.exists()) file.delete();
        bean.setStatus(CompressStatus.ERROR);

    }
}
