package com.example.baidusync.Admin;

import com.example.baidusync.Admin.Entity.UserEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping("/doLogin")
    @ResponseBody
    public String  doLogin(UserEntity user){
        System.out.println(user.getAccount());
        System.out.println(user.getPassword());
        return "";
    }

    @RequestMapping("/adminIndex")
    public String adminIndex(){
        return "Admin/admin-index";
    }



}
