package cn.deystar.BaiduPan.OriginFiles.Controller;

import cn.deystar.BaiduPan.Core.BaiduRequest.NetDiskPath.NetDiskPathService;
import cn.deystar.BaiduPan.OriginFiles.Bean.OriginFiles;
import cn.deystar.BaiduPan.OriginFiles.Enums.Sort;
import cn.deystar.BaiduPan.OriginFiles.Service.OriginFileService;
import cn.deystar.Util.BaiduPanResponse.DeleteResponse;
import cn.deystar.Util.HttpServerlet.Response.ResponseData;
import cn.deystar.Util.HttpServerlet.Response.Success;
import cn.deystar.Util.HttpServerlet.Response.Warning;
import cn.deystar.Util.Util.PathToListUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 网盘上的文件浏览
 */
@Controller
@RequestMapping("/origin")
public class OriginFileController {
    static Map<Integer, String> errnoMsg = new HashMap<>();

    static {
        errnoMsg.put(2, "参数不正确");
        errnoMsg.put(-9, "文件不存在");
        errnoMsg.put(111, "有其他异步任务正在执行");
        errnoMsg.put(-7, "文件名非法");
    }

    @Resource
    OriginFileService originFileService;
    @Value("${baidu-netdisk.path}")
    private String defaultPath;
    @Resource
    private NetDiskPathService netDiskPathService;

    @GetMapping
    public String view(String path, String sortName, Integer page,
                       Integer total, Integer onceNum,
                       ModelMap modelMap) {
        String title = "云端目录";
        modelMap.put("title", title);
        Sort sort = null;
        if (sortName != null && !sortName.trim().isEmpty()) sort = Sort.valueOf(sortName);
        List<List<OriginFiles>> fileList = originFileService.listAll(path, sort);


        Sort[] sorts = Sort.values();
        List<Map<String, Object>> sortMaps = new ArrayList<>();
        for (Sort s : sorts) {
            Map<String, Object> map = new HashMap<>();
            map.put("method", s.method);
            map.put("detail", s.detail);
            map.put("name", s.name());
            sortMaps.add(map);
        }


        if (path != null && !path.trim().isEmpty()) {
            path = path.replace(defaultPath, "");
        }


        List<String> directories = PathToListUtil.genPathList(path);
        modelMap.put("originPath", defaultPath);
        modelMap.put("pathList", directories);
        modelMap.put("listEmpty", fileList == null || fileList.isEmpty());
        modelMap.put("fileList", fileList);
        modelMap.put("path", path);
        modelMap.put("sortMaps", sortMaps);
        modelMap.put("sortName", sortName);
        modelMap.put("page", page);
        modelMap.put("total", total);
        modelMap.put("onceNum", onceNum);
        return "Main/OriginFiles/origin-file";
    }

    @DeleteMapping
    @ResponseBody
    public ResponseData deleteFile(String path) {
        if (path != null && !path.trim().isEmpty()) {
            DeleteResponse delResp = netDiskPathService.delete(path);
            if (delResp.getErrno() != null && delResp.getErrno() != 0) {
                return new Warning(errnoMsg.get(delResp.getErrno()));
            }
            return new Success();
        }
        return new Warning("path为空");
    }
}

