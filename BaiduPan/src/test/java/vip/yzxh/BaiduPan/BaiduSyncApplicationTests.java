package vip.yzxh.BaiduPan;

import cn.hutool.core.lang.UUID;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Setting.Service.FileSettingService;

import javax.annotation.Resource;
import javax.swing.plaf.FileChooserUI;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
class BaiduSyncApplicationTests {

    @Resource
    FileSettingService fileSettingService;


//    @Test
//    public void tempFileTest() {
//
//        File[] dir = new File(path).listFiles();
//        Long totalSize = 0L;
//        for (int i = 0; i < dir.length; i++) {
//            if (!dir[i].isDirectory() && FileUtils.sizeOf(dir[i]) < SysConst.getMinSize()) {
//                totalSize += FileUtils.sizeOf(dir[i]);
//                System.out.println(dir[i].getName());
//            }
//        }
//        DecimalFormat decimalFormat = new DecimalFormat("#.00");
//        if (totalSize < 1024) {
//            Double size = Double.valueOf(decimalFormat.format((double) totalSize));
//            String unit = "B";
//            System.out.println("::" + decimalFormat.format((double) totalSize) + " B");
//
//
//        } else if (totalSize < 1048567) {
//            Double size = Double.valueOf(decimalFormat.format((double) totalSize / 1024));
//            String unit = "KB";
//            System.out.println("::" + decimalFormat.format((double) totalSize / 1024) + " KB");
//
//        } else if (totalSize < 1073741824) {
//            Double size = Double.valueOf(decimalFormat.format((double) totalSize / 1048567));
//            String unit = "MB";
//            System.out.println("::" + decimalFormat.format((double) totalSize / 1048567) + " MB");
//
//        }
//        Double size = Double.valueOf(decimalFormat.format((double) totalSize / 1073741824));
//    }

    @Test
    public void testUUID() {
        String s = "1(2)";
        System.out.println(UUID.nameUUIDFromBytes(s.getBytes()));
    }

    @Test
    public void testParent() {
        File[] files = new File("D:\\JavaWorkSpace\\g\\pixiv").listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getParent());
        }
    }


    @SneakyThrows
    @Test
    public void testNIO() {
        //E:\JavaWorkSpace\佳能\JPEG
        File[] files = new File("E:/JavaWorkSpace/佳能/JPEG").listFiles();
        for (int i = 0; i < files.length; i++) {
            byte[] fileBytes = FileUtils.readFileToByteArray(files[i]);
            FileOutputStream fileOutputStream = new FileOutputStream("E:/JavaWorkSpace/Cannon/" + i + ".jpg");
            try {
                fileOutputStream.write(fileBytes);
            } finally {
                fileOutputStream.close();
            }
        }
    }

    @Test
    public void testWrite() {
        FileSetting setting = new FileSetting();
        setting.setAppId("1q23");
        setting.setPassword("123");
        fileSettingService.settingFile(setting);
        System.out.println(fileSettingService.getSetting().toString());
    }


}
