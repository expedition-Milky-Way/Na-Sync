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
    public String index(HttpServletRequest request, ModelMap modelMap){
        File file = new File("D:/");
        for (String s : file.list()) {
            System.out.println(s);
        }
        return "Admin/admin-index";
    }

    @PostMapping("/add")
    public ResponseData submit(String filePath,String dateTime,Integer encryption,
                               String pass,String confirmPass,String appId,String appKey,
                               String secretKey,String signKey)
    {
        File file = new File(filePath);
        for (String s : file.list()) {
            System.out.println(s);
        }
        return new ResponseData();

    }
    

}
