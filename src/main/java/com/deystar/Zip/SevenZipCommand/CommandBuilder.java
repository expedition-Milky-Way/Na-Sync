package com.deystar.Zip.SevenZipCommand;

/**
 * @Author YeungLuhyun
 **/
public class CommandBuilder implements SevenZipCommand {


    @Override
    public NoPasswordCommand noPassword(String softwarePath) {
        return new NoPasswordCommand(softwarePath);
    }

    @Override
    public PasswordCommand hasPassword(String softwarePath) {
        return new PasswordCommand(softwarePath);
    }
}
