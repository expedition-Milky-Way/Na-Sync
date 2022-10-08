package com.example.baidusync.Util.FileUtil;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Util.FileLog.FileLogEntity;
import com.example.baidusync.Util.FileLog.FileLogService;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.core.Bean.SysConst;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;


import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipException;

/**
 * @author 杨 名 (字 露煊)
 * <p>
 * 文件压缩
 */

public class ZipFileUtil {

    private FileLogService fileLogService = SpringUtil.getBean(FileLogService.class);
    private RequestNetDiskService diskService  =SpringUtil.getBean(RequestNetDiskService.class);
    //如果出现同名文件，将会采用  xx(NAME_PREFIX).zip来命名文件
    private static Integer NAME_PREFIX = 1;
    //文件后缀
    private static final String FILE_ZIP_PREFIX = ".zip";
    private static final String FILE_7Z_PREFIX = ".7z";

    public void zipFile(String name, List<File> fileList, String password) throws ZipException {
        if (fileList.size() > 0) {
            String fileName = this.rename(name, FILE_ZIP_PREFIX);
            //1.将文件名和文件父目录名哈希，防止百度网盘和谐盗版资源
            String[] nameDir = name.split("/"); //文件绝对路径
            String parent = "";
            if (nameDir[nameDir.length-3] != null){
                parent+= "/"+nameDir[nameDir.length -3];
            }
            if (nameDir[nameDir.length-2]!= null){
                parent +="/"+ nameDir[nameDir.length-2];
            }
            FileLogEntity fileLog = new FileLogEntity();
            String baiduParent = String.valueOf(RandomUtil.randomChar(parent));
            String baiduFileName = String.valueOf(RandomUtil.randomChar(fileName));
            fileLog.setOriginalFileName(fileName);
            fileLog.setOriginalParentName(parent);
            fileLog.setOriginalPathName(name); // name传进来的是绝对路径
            fileLog.setCreateTime(new Date());
            fileLog.setFileName(baiduFileName);
            fileLog.setParent(baiduParent);
            fileLog.setPassword(password);

            ZipFile zipFile = new ZipFile(fileName);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
            zipParameters.setCompressionMethod(CompressionMethod.STORE);
            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipParameters.setEncryptFiles(true);
            zipFile.setPassword(password.toCharArray());
            try {
                zipFile.addFiles(fileList,zipParameters);
                fileLogService.add(fileLog);
            } catch (net.lingala.zip4j.exception.ZipException e) {
                e.printStackTrace();
            }
            //查看网盘上是否有该文件目录
            String diskNetDisk = SysConst.getDefaultNetDiskDir()+nameDir;
            boolean hasDir = diskService.hasDir(diskNetDisk);
            if (!hasDir){
                diskService.postCreateNetDisk(diskNetDisk);
            }
        }
    }


    /**
     * 重命名（检查文件名是否重复）
     */
    public  String rename(String name, String prefix) {
        synchronized (NAME_PREFIX){
            String fileFinalName = name + prefix;
            while (true){
                if (isExist(fileFinalName)) { //如果存在相同的名
                    String[] nameChar = name.split("/");
                    String oldName = nameChar[nameChar.length - 1];
                    String nName = oldName + "(" + NAME_PREFIX + ")";
                    fileFinalName = name.replace(oldName, nName);
                    NAME_PREFIX++;
                }else{
                    break;
                }
            }
            NAME_PREFIX = 1;
            return fileFinalName;
        }

    }



    /**
     * 检查文件是否重复
     */
    private Boolean isExist(String name) {
        File file = new File(name);
        return file.exists();
    }

}
