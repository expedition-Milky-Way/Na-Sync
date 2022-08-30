package com.example.baidusync.Admin.Service;

import cn.hutool.core.util.ObjectUtil;
import com.example.baidusync.Admin.Entity.UserEntity;
import com.example.baidusync.Util.ResponseData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService{

    @Value("${custom-servicePass}")
    String servicePass;
    @Value("${custom-serveiceUser}")
    String serviceUser;
    @Value("${custom-serviceToken}")
    String token;

    @Override
    public ResponseData login(UserEntity user) {
        if(ObjectUtil.isNotNull(user)){
            if (!Objects.equals(user.getAccount(), serviceUser) && !Objects.equals(user.getPassword(), servicePass)){
                return new ResponseData(false,ResponseData.DEFAULT_ERROR_CODE,
                        ResponseData.DEFAULT_ERROR_MESSAGE,null);
            }else{
                return new ResponseData(true,ResponseData.DEFAULT_SUCCESS_CODE,
                        ResponseData.DEFAULT_SUCCESS_MESSAGE,token);
            }
        }
        return new ResponseData(false,ResponseData.DEFAULT_ERROR_CODE,
                ResponseData.DEFAULT_ERROR_MESSAGE,null);
    }
}
