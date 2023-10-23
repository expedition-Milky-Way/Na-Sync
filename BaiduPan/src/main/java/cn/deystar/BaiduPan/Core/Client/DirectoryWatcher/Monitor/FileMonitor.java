package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Monitor;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */

public class FileMonitor implements FileMonitorService{

    private FileAlterationMonitor monitor;

    private volatile static String watchPath;

    /**
     * 给文件添加监听
     *
     * @param path     文件路径
     * @param listener 文件监听器
     */
    @Override
    public void monitor(File path, FileAlterationListener listener) {

        if (path.exists()){
            watchPath = path.getPath();
            FileAlterationObserver observer = new FileAlterationObserver(path);
            monitor.addObserver(observer);
            observer.addListener(listener);
        }
    }

    @Override
    public void stop() throws Exception {
        monitor.stop();
    }

    @Override
    public void start() throws Exception {
        monitor.start();

    }

    @Override
    public String nowWatch(){
        return watchPath;
    }


    public FileMonitor(Long interval){
        monitor = new FileAlterationMonitor(interval);
    }


}
