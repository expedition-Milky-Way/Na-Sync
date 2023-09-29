package cn.deystar.Util.Util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * @Author YeungLuhyun
 * 写入文件和读取文件的工具类
 **/
public class ConfigFileTemplate {

    public static void writeFile(String writeTo, String content) {

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(writeTo);
            byte[] bytes = content.getBytes();
            stream.write(bytes, 0, bytes.length);
            System.out.println(writeTo);
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


    public static String readFile(String needRead) {
        if (!new File(needRead).exists()) return "";
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder("");
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(needRead), Charset.forName("utf8")));
            int c;
            while ((c = in.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
        return builder.toString();
    }
}
