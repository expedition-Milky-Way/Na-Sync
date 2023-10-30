package cn.deystar.Compress.SevenZip.Command.Common;

import java.io.File;
import java.util.List;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
public class AppendFile {

    private StringBuilder fileCommand = new StringBuilder();
    private StringBuilder command;

    public AppendFile append(String absolutPath) {
        fileCommand.append(" \"").append(absolutPath).append("\"");
        return this;
    }

    public AppendFile append(List<File> fileList) {
        if (fileList == null || fileList.isEmpty())
            throw new RuntimeException("paths not value");
        fileList.forEach(item -> {
            fileCommand.append(" \"").append(item.getAbsolutePath()).append("\"");
        });
        return this;
    }


    public String build() {
        return this.command.append(fileCommand).toString();
    }


    public AppendFile(StringBuilder command) {
        this.command = command;
    }
}
