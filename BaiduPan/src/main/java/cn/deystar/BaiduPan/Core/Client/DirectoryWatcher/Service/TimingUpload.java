package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service;

import cn.deystar.Setting.Setting.Service.FileSettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Service
public  class TimingUpload extends UploadModeAbstract {
    @Resource
    private FileSettingService settingService;

    private static Timer timerExecutor;

    private static final Set<String> fileSet = new HashSet<>();

    @Override
    public boolean add(String path) {
        synchronized (fileSet) {
            return fileSet.add(path);
        }
    }


    @Override
    public boolean remove(String path) {
        boolean result = false;
        synchronized (fileSet) {
            for (String filePath : fileSet) {
                if (filePath.equals(path)) {
                    result = fileSet.remove(path);
                    break;
                }
            }
        }
        return result;
    }







}
