package cn.deystar.Util.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class FileToChar {


    public static char[] readFileToCharArray(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[4096];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[4096];
        }

        reader.close();

        return fileData.toString().toCharArray();
    }


}
