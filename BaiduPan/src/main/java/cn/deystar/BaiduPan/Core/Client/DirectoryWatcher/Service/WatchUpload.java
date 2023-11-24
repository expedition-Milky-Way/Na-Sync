package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service;

import cn.deystar.BaiduPan.Core.BaiduRequest.User.UserRequestService;
import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;
import cn.deystar.Setting.UnSync.MappedService.UnSyncMapped;
import cn.deystar.Setting.UnSync.Service.UnSyncService;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import cn.deystar.Util.BaiduPanResponse.UserMsg;
import cn.deystar.Util.ScanAndZip.Const.SystemEnums;
import cn.deystar.Util.ScanAndZip.Scan.FileScan;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.Command.ZipCommand.CommandBuilder;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.Suffix.SuffixZip;
import cn.deystar.Util.ScanAndZip.Zip.SevenZip.PackageRunnable.Zip.ZipAbstract;
import cn.deystar.Util.ScanAndZip.ZipArgument.ZipArgument;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;

import java.util.List;
import java.util.Objects;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Service
public class WatchUpload extends UploadModeAbstract {

    @Resource
    private FileSettingService settingService;
    @Resource
    private UserRequestService userRequestService;
    @Resource
    private CompressService compressService;
    @Resource
    private UnSyncMapped unSyncMapped;

    /**
     * 如果顶级目录下面的目录没有发生文件修改事件，就筛掉子目录
     */

    @Override
    public boolean add(String path) {
        ZipArgument zipArgument = new ZipArgument();
        FileSetting setting = settingService.getSetting();
        zipArgument.setOriginPath(path);
        zipArgument.setZipToPath(setting.getCachePath());
        zipArgument.setEncryption(setting.getPassword() != null && !setting.getPassword().trim().isEmpty());
        zipArgument.setPathAnonymity(setting.getPathEncryption() != null ? setting.getPathEncryption() : false);

        TokenResponse tokenDetail = settingService.getToken();

        if (tokenDetail != null && tokenDetail.isAllNotNull()) {
            UserMsg userMsg = userRequestService.getBaiduUsInfo(tokenDetail.getAccessToken());
            zipArgument.setOneFileSize(userMsg.getVipTypeEnums().fileSize);
            FileScan scan = new FileScan(zipArgument);

            File parentFile = new File(path);
            if (!parentFile.exists()) return false;
            //扫描
            scan.scan(Arrays.asList(Objects.requireNonNull(parentFile.listFiles())));
            SystemEnums systemEnums = setting.getSystemEnums();
            if (!scan.getList().isEmpty()) {
                scan.getList().forEach(item -> {
                    // 文件夹筛除
                    if (!unSyncMapped.isUnSync(item.getParent())){
                        // 文件筛除
                        item.getFileLit().forEach(fileItem ->{
                            if (unSyncMapped.isUnSync(fileItem.getPath())){
                                item.getFileLit().remove(fileItem);
                            }
                        });

                        //压缩任务建造
                        String command = new CommandBuilder(systemEnums)
                                .outPut(setting.getCompressThread(), item.getZipName())
                                .password(setting.getPassword())
                                .append(item.getFileLit())
                                .build();
                        ZipAbstract zipService = new SuffixZip(zipArgument, item, command);
                        //压缩任务入队
                        compressService.addTask(zipService);
                    }
                });
                return true;
            }
        }
        return false;
    }


}
