package com.deystar.Zip;

import com.deystar.Const.SystemEnums;
import com.deystar.Result.ResultState;
import com.deystar.UserTyper.UserTyper;
import com.deystar.Zip.Entity.FileListBean;

import com.deystar.Zip.SevenZipCommand.CommandBuilder;

import com.deystar.Zip.ZipFourJ.FileToZip;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author YeungLuhyun
 **/
public class GoZip {
    private FileListBean bean;

    private UserTyper user;

    private SystemEnums systemEnums;

    public void start() {
        int superThreadNum = Runtime.getRuntime().availableProcessors();
        String command = new CommandBuilder().outPut(superThreadNum, bean.getZipName(), systemEnums)
                .password(user.getPassword()).append(bean.getFileLit()).build();
        try {
            this.zip(command);
        } catch (IOException | InterruptedException e) {
            FileToZip.zip(bean, user);
        }
    }

    public void zip(String command) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(command);
        InputStream stdIn = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdIn, Charset.forName("GBK"));
        BufferedReader br = new BufferedReader(isr);

        String line = br.lines().toString();


        int exitVal = proc.waitFor();
        if (exitVal != 0) {
            ResultState.error(line + " 压缩失败:\n" + bean.toString());
            return;
        }
        ResultState.success(bean.getZipName());
    }

    public GoZip(UserTyper user, FileListBean bean, SystemEnums enums) {
        this.user = user;
        this.bean = bean;
        this.systemEnums = enums;
    }
}
