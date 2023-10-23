package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Monitor;

import org.apache.commons.io.monitor.FileAlterationListener;

import java.io.File;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public interface FileMonitorService {

    /**
     * 新建监控
     * @param path
     * @param listener
     */
    void monitor(File path, FileAlterationListener listener);

    /**
     * 结束监控
     * @throws Exception 可能为空
     */
    void stop() throws Exception;

    /**
     * 开始监控
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 现在监控的文件夹
     * @return
     */
    String nowWatch();
}
