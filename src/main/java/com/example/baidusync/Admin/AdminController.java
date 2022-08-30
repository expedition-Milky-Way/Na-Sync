package com.example.baidusync.Admin;

import com.example.baidusync.Admin.Entity.UserEntity;
import com.example.baidusync.Admin.Service.AdminService;
import com.example.baidusync.Util.LocalCache;
import com.example.baidusync.Util.ResponseData;
import org.springframework.boot.web.server.Cookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
public class AdminController {

    @Resource
    private AdminService service;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping("/doLogin")
    @ResponseBody
    public ResponseData  doLogin(UserEntity user, HttpServletResponse response){
        ResponseData data = service.login(user);
        if (data.success){
            LocalCache.putContext(user.getAccount(), user.getPassword());
        }
        return data;
    }

    @RequestMapping("/adminIndex")
    public String adminIndex(HttpServletRequest request, ModelMap modelMap){
        //1.获取所有目录
        File dir = new File("/");
        String[] dirList = dir.list();
        modelMap.put("dirList",dirList);
        //2.获取
        return "Admin/admin-index";
    }



}
