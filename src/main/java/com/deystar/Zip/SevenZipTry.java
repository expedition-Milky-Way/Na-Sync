package com.deystar.Zip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @Author YeungLuhyun
 **/
public class SevenZipTry {


    private String commands = "C:\\Program Files\\7-Zip\\7z.exe";
    public static void main(String[] args) throws IOException, InterruptedException {
        SevenZipTry command = new SevenZipTry();
        String commands = command.outTo("E:\\zipTest\\star.zip")
                .pass("123123")
                .append("E:\\zipTest\\star\\miliOne.mp4")
                .append("E:\\zipTest\\star\\miliTow.mp4")
                .build();
        Process proc = Runtime.getRuntime().exec(commands);

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

    public SevenZipTry append(String command){
        this.commands+=" "+command+"";
        return this;
    }

    public SevenZipTry outTo(String path){
        this.commands+= " -tzip a "+path;
        return this;
    }

    public SevenZipTry pass(String password){
        this.commands+=" -p"+password;
        return this;
    }

    public String build(){
        return this.commands;
    }
}
