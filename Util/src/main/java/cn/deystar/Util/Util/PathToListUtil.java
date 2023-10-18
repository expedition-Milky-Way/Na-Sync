package cn.deystar.Util.Util;

import cn.deystar.Util.Beans.PathLinkList.PathLinkList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 *
 */
public class PathToListUtil {


    /**
     * 将路径以链表的形式返回
     * @param path
     * @return
     */
    public static PathLinkList genPathLinkList(String path){
        if (path ==  null || path.trim().isEmpty())
           return new PathLinkList();
        path = path.replace("\\","/");
        String[] directories = path.split("/");
        PathLinkList prev = new PathLinkList();
        PathLinkList nex = prev;
        for (String item : directories){
            nex.setDirectory(item);
            nex.setNext(new PathLinkList());
            nex = nex.getNext();
        }
        return prev;
    }

    public static List<String> genPathList(String path){
        if (path== null || path.trim().isEmpty()) return new ArrayList<>();
        String[] pathStr = path.replace("\\", "/").split("/");
        return new ArrayList<>(Arrays.asList(pathStr));
    }


}
