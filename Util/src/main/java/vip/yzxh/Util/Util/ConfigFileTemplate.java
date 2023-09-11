package vip.yzxh.Util.Util;

import java.io.*;
import java.nio.file.Files;

/**
 * @Author YeungLuhyun
 **/
public class ConfigFileTemplate {

    public static void writeFile(File writeTo, String content) {

        OutputStream stream = null;
        try {
            stream = Files.newOutputStream(writeTo.toPath());
            byte[] bytes = content.getBytes();
            stream.write(bytes, 0, bytes.length);
            System.out.println(writeTo.getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static String readFile(File needRead) {
        if (!needRead.exists()) return "";
        InputStream stream = null;
        StringBuffer result = new StringBuffer("");
        try {
            stream = Files.newInputStream(needRead.toPath());
            int content ;
            while ((content = stream.read()) != -1){
                result.append((char)content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (stream != null){
                try {
                    stream.close();
                    System.gc();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result.toString();
    }
}
