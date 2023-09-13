package com.deystar.Zip;

import com.deystar.Zip.SevenZipCommand.CommandBuilder;
import com.deystar.Zip.SevenZipCommand.SevenZipCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @Author YeungLuhyun
 **/
public class SevenZipTryBuilder {

    static SevenZipCommand zipCommand = new CommandBuilder();

    public static void main(String[] args) {
        String command = zipCommand.noPassword("C:\\Program Files\\7-Zip\\7z.exe")
                .outPut("E:\\zipTest\\STAR.zip")
                .append("E:\\zipTest\\star\\miliOne.mp4")
                .append("E:\\zipTest\\star\\miliTow.mp4")
                .build();
        String command2 = zipCommand.hasPassword("C:\\Program Files\\7-Zip\\7z.exe")
                .outPut("E:\\zipTest\\STAR2.zip")
                .password("123")
                .append("E:\\zipTest\\star\\miliOne.mp4")
                .append("E:\\zipTest\\star\\miliTow.mp4")
                .build();

        Thread c1T = new Thread(() -> {
            try {
                run(command2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        c1T.setName("有密码的线程");
        Thread c2T = new Thread(() -> {
            try {
                run(command);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        c2T.start();
        c1T.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void run(String command) throws IOException, InterruptedException {

        System.out.println(Thread.currentThread().getName());
        Process proc = Runtime.getRuntime().exec(command);
        InputStream stdIn = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdIn, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        System.out.println("<OUTPUT>");

        while ((line = br.readLine()) != null)
            System.out.println(line);

        System.out.println("</OUTPUT>");
        int exitVal = proc.waitFor();
        System.out.println("Process exitValue: " + exitVal);


    }
}
