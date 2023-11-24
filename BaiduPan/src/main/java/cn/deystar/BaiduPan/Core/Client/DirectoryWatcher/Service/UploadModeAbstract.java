package cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Service;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public abstract class UploadModeAbstract implements UploadModeService{

    public abstract boolean add(String path);
    @Override
    public boolean remove(String path){
        return false;
    }
}
