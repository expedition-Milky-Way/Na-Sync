package com.deystar.Zip.SevenZipCommand;

import com.deystar.Const.SystemEnums;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;


import java.net.URL;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊）
 **/
public class CommandBuilder {
    protected StringBuilder command = new StringBuilder("");

    /**
     *
     * @param superThreadNum How many cpu`s thread use in zip task
     * @param zipName The file`s absolute path
     * @param enums What system run this
     * @return
     */
    public CommandBuilder outPut(@NotNull Integer superThreadNum, @NotNull String zipName, @NotNull SystemEnums enums) {
        String command = " -mmt=:superThread -tzip a \":output\"";
        //1. Find an executable file and append the file path into command
        URL sevenZip = this.getClass().getClassLoader().getResource(enums.bashFile);
        if (sevenZip != null && sevenZip.getFile() != null && !sevenZip.getFile().isEmpty()) {
            String bashFile = sevenZip.getFile();
            String[] bashFileStrs = bashFile.split("/");
            bashFile = "";
            for (int i = 1; i < bashFileStrs.length; i++) {
                bashFile += bashFileStrs[i];
                if (i != bashFileStrs.length - 1){
                    bashFile+="/";
                }
            }
            this.command.append(bashFile);
        } else {
            throw new RuntimeException(" Can not find 7z.exe or 7zz");
        }
        //2. Create a command of zip
        this.command.append(command.replace(":superThread",superThreadNum+"").replace(":output",zipName));
        return this;
    }

    /**
     * @param password null|"" : no encrypted
     * @return
     */
    public AppendFile password(@Nullable String password) {
        if (password != null && !password.trim().isEmpty()) {
            command.append(" -p").append(password);
        }
        return new AppendFile(command);

    }

}