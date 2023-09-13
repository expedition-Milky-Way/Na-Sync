package com.deystar.UserTyper;

import com.deystar.Const.BaiduConstEnums;
import com.deystar.Const.BaiduEnums;
import com.deystar.Const.BaiduEnumsService;
import com.deystar.CustomException.Path.PathException;
import com.deystar.CustomException.Path.PathExceptionEnums;
import com.deystar.CustomException.Size.SizeException;
import com.deystar.CustomException.Size.SizeExceptionEnums;

import java.io.File;
import java.util.Scanner;

/**
 * @Author YeungLuhyun
 **/
public class TyperBuilder {
    private static UserTyper user = new UserTyper();
    private static BaiduEnums baiduEnums = new BaiduEnumsService();

    public static UserTyper builder() {
        sevenZipPath();
        originPath();
        toZipPath();
        isEncryption();
        needEncryptionPath();
        setSize();
        return user;
    }

    private static void sevenZipPath() {
        String msg = "7Zip.exe 的绝对路径:";
        String path = getInputPath(msg, false);
        user.setSevenZipPath(path);
    }

    private static void originPath() {
        String msg = "请输入要压缩的文件路径:";
        String path = getInputPath(msg, false);
        user.setOriginPath(path);
    }

    private static void toZipPath() {
        String msg = "请输入压缩文件输出的文件夹路径:";
        String path = getInputPath(msg, true);
        user.setZipToPath(path);
    }

    private static void needEncryptionPath() {
        String msg = "是否需要打乱文件名称,默认为不需要(y/Y | N/n):";
        String input = getInputOder(msg);
        if (input.trim().equals("")) {
            System.out.println("不需要打乱");
        }
        if (input.contains("Y") || input.contains("y") || input.contains("ye")) {
            msg = "请输入excel输出的文件夹路径:";
            String path = getInputPath(msg, true);
            user.setNeedEncryPath(true);
            user.setExcelOutput(path);
        }
        user.setNeedEncryPath(false);
    }


    private static void isEncryption() {
        String message = "是否需要加密,默认为不加密(y/Y | N/n):";
        String input = getInputOder(message);
        if (input.trim().equals("")) {
            System.out.println("不需要加密");
        }
        if (input.contains("Y") || input.contains("y") || input.contains("ye")) {
            Integer count = 0;
            String pass = setPassword();
            while (count < 2 && pass.trim().equals("")) {
                pass = setPassword();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            user.setIsEncryption(true);
            user.setPassword(pass);
            return;
        }
        user.setIsEncryption(false);
    }

    private static String setPassword() {
        String message = "输入密码：";
        String input = getInputOder(message);
        return input;
    }

    private static void setSize() {
        String message = "请输入你的百度网盘账号会员等级(normal,vip,svip)：";
        String input = getInputOder(message);
        BaiduConstEnums enums = baiduEnums.getVipType(input);
        if (enums == null) {
            throw new SizeException(SizeExceptionEnums.CANT_NOT_FIND_VIP_TYPE);
        }
        user.setOneFileSize(enums.size);

    }


    private static String getInputPath(String message, boolean maybeGenPath) {
        String path = getInputString(message);
        if (path == null || path.trim().equals("")) throw new PathException(PathExceptionEnums.NULL_PATH);
        if (path.contains("\\")) {
            path = path.replace("\\", "/");
        }
        File pathObject = new File(path);
        if (!pathObject.exists() && !maybeGenPath) {
            throw new PathException(PathExceptionEnums.HAS_NOT_DIR);
        } else if (!pathObject.exists() && maybeGenPath) {
            pathObject.mkdir();
        }
        return path;
    }

    private static String getInputOder(String message) {
        String input = getInputString(message);
        return input;
    }

    private static String getInputString(String message) {
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }

}
