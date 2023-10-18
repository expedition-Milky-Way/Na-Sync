package cn.deystar.BaiduPan.Core.Config.GlobalBean;

import cn.deystar.BaiduPan.Core.Compress.CompressService;
import cn.deystar.BaiduPan.Core.Compress.impl.CompressServiceImpl;
import cn.deystar.BaiduPan.Core.Upload.UploadTaskService;
import cn.deystar.BaiduPan.Core.Upload.impl.UploadTaskServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Configuration
public class CustomBean {

    /**
     * 压缩
     * @return
     */
    @Bean
    public CompressService compressService(){
        return new CompressServiceImpl();
    }


    /**
     * 上传
     * @return
     */
    @Bean
    public UploadTaskService uploadTaskService(){
        return new UploadTaskServiceImpl();
    }


}
