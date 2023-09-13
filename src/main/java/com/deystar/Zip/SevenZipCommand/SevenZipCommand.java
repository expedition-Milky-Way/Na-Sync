package com.deystar.Zip.SevenZipCommand;

/**
 * @Author YeungLuhyun
 **/
public interface SevenZipCommand {


    NoPasswordCommand noPassword(String softwarePath);

    PasswordCommand hasPassword(String softwarePath);
}
