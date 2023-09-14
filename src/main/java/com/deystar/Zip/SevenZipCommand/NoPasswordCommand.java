package com.deystar.Zip.SevenZipCommand;

import java.io.File;
import java.util.List;

/**
 * @Author YeungLuhyun
 **/
public  class NoPasswordCommand {

    private String command = "";

    public AppendFile outPut(String path){
        this.command+= " -tzip a -mmt=8 -mx1 \""+path+"\"";

        return new AppendFile();
    }

    /**
     *
     * @param softwarePath 7zip的绝对路径
     */
   public NoPasswordCommand(String softwarePath){
       this.command = softwarePath;
   }



    public class AppendFile{
        public AppendFile append(String absolutPath){
            command+=" \""+absolutPath+"\"";
            return new AppendFile();
        }
        public AppendFile append(List<File> fileList) {
            if (fileList == null || fileList.isEmpty())
                throw new RuntimeException("paths not value");
            fileList.forEach(item -> {
                command += " \"" + item.getAbsolutePath()+"\"";
            });
            return new AppendFile();
        }

        public String build(){
            return command;
        }

    }
}
