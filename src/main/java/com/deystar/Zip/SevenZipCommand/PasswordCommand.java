package com.deystar.Zip.SevenZipCommand;

import java.io.File;
import java.util.List;

/**
 * @Author YeungLuhyun
 **/
public class PasswordCommand {
    private StringBuilder command;


    public Password outPut(String path) {

        this.command.append(" -mmt=8 -tzip a " + "\"").append(path).append("\"");
        return new Password();
    }

    public PasswordCommand(String softwarePath) {
        this.command = new StringBuilder(softwarePath);
    }


    public class Password {

        public AppendFile password(String password) {
            command.append(" -p").append(password);
            return new AppendFile();
        }
    }

    public class AppendFile {
        public AppendFile append(String absolutPath) {
            command.append(" \"").append(absolutPath).append("\"");
            return new AppendFile();
        }

        public AppendFile append(List<File> fileList) {
            if (fileList == null || fileList.isEmpty())
                throw new RuntimeException("paths not value");
            fileList.forEach(item -> {
                command.append(" \"").append(item.getAbsolutePath()).append("\"");
            });
            return new AppendFile();
        }

        public String build() {
            return command.toString();
        }

    }
}
