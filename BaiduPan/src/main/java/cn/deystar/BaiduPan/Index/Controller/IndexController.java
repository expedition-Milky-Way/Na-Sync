package cn.deystar.BaiduPan.Index.Controller;

import cn.deystar.BaiduPan.Index.Service.IndexService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Controller
@RequestMapping("/")
public class IndexController {


    @Resource
    IndexService indexService;


    @Resource
    ThreadPoolTaskExecutor taskExecutor;


    @GetMapping()
    public String indexPage(ModelMap modelMap){
        modelMap.put("title","运行情况");
        taskExecutor.execute(()->{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            indexService.sendMessage();
        });
        return "Main/Index/index";
    }


}
