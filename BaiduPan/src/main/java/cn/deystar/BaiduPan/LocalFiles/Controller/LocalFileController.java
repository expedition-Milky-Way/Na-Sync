package cn.deystar.BaiduPan.LocalFiles.Controller;


import cn.deystar.BaiduPan.LocalFiles.Service.LocalFileService;
import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;

import cn.deystar.Util.HttpServerlet.Response.ResponseData;
import cn.deystar.Util.HttpServerlet.Response.Success;
import cn.deystar.Util.Util.PathToListUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Controller
@RequestMapping("local")
public class LocalFileController {

    @Resource
    private LocalFileService localFileService;
    @Resource
    private FileSettingService settingService;

    @GetMapping
    public String localFileView(String path, ModelMap modelMap) {
        FileSetting setting = settingService.getSetting();
        if (setting != null && setting.isAllNotNull()) {

            String localPath = setting.getPath().replace("\\","/");
            if (!localPath.endsWith("/")) localPath+="/";
            if (path == null || path.trim().isEmpty()){
                path = localPath;
            }
            modelMap.put("localFiles", localFileService.listAll(path));
            if (path!= null && !path.trim().isEmpty()){
                path = path.replace("\\","/");
                path = path.replace(localPath,"");
            }

            modelMap.put("pathList",PathToListUtil.genPathList(path));
            modelMap.put("path",path);
            modelMap.put("hasFile", true);
            modelMap.put("localPath",localPath);
        } else {
            modelMap.put("hasFile", false);
        }

        modelMap.put("title", "本地目录");
        return "Main/LocalFiles/localfile";
    }



}
