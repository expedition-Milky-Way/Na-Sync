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
        return "Admin/admin-index";
    }



}
