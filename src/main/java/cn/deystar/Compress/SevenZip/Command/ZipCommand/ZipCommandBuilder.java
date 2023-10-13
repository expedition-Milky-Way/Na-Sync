package cn.deystar.Compress.SevenZip.Command.ZipCommand;

import cn.deystar.CustomException.Argurment.ArgumentException;
import cn.deystar.CustomException.Argurment.ArgumentExceptionEnums;
import cn.deystar.CustomException.ExecutableFile.ExecutableFileEnums;
import cn.deystar.Const.SystemEnums;
import cn.deystar.CustomException.ExecutableFile.ExecutableFileException;
import cn.deystar.Compress.SevenZip.Command.Common.AppendFile;


import java.net.URL;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊）
 **/
public  class ZipCommandBuilder  {
    protected StringBuilder command;



    /**
     * @param superThreadNum How many cpu`s thread use in zip task
     * @param zipName        The file`s absolute path
     * @return
     */
    public ZipCommandBuilder outPut(Integer superThreadNum, String zipName) {
        if (superThreadNum == null || superThreadNum < 1 || zipName == null || zipName.trim().isEmpty()) {
            throw new ArgumentException("superThreadNum:"+superThreadNum+"zipName:"+zipName, ArgumentExceptionEnums.ARGUMENT_NULL);
        }
        superThreadNum = Math.max(superThreadNum,1);
        String command = " -mmt=:superThread -tzip a \":output\"";
        //2. Create a command of zip
        this.command.append(command.replace(":superThread", superThreadNum + "").replace(":output", zipName));
        return this;
    }

    /**
     * @param password null|"" : no encrypted
     * @return appendFile(command)
     */
    public AppendFile password(String password) {
        if (password != null && !password.trim().isEmpty()) {
            command.append(" -p").append(password);
        }
        return new AppendFile(command);

    }

    public ZipCommandBuilder(SystemEnums enums) {
        URL sevenZip = this.getClass().getClassLoader().getResource(enums.bashFile);
        if (sevenZip != null && sevenZip.getFile() != null && !sevenZip.getFile().isEmpty()) {
            String bashFile = sevenZip.getFile();
            String[] bashFileStrs = bashFile.split("/");
            bashFile = "";
            for (int i = 1; i < bashFileStrs.length; i++) {
                bashFile += bashFileStrs[i];
                if (i != bashFileStrs.length - 1) {
                    bashFile += "/";
                }
            }
            this.command = new StringBuilder(bashFile);
        } else {
            throw new ExecutableFileException(ExecutableFileEnums.NOT_FOUND);
        }
    }
}