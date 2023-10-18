package cn.deystar.BaiduPan.Core.Config.Thymeleaf;


import cn.deystar.Util.Const.BaiduCategory;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 杨 名 (字 露煊)
 */
@Configuration
public class ThymeleafConfig implements WebMvcConfigurer {

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        Set<String> jsSet = new HashSet<>();
        jsSet.add("static/layui-v2.6.8/layui/layui.js");
        jsSet.add("static/echarts/echarts.min.js");
        templateResolver.setJavaScriptTemplateModePatterns(jsSet);
        Set<String> cssSet = new HashSet<>();
        cssSet.add("static/layui-v2.6.8/layui/css/layui.css");
        cssSet.add("static/custom/custom.css");
        cssSet.add("static/layui-v2.6.8/layui/css/modules/code.css");
        cssSet.add("static/layui-v2.6.8/layui/css/modules/laydate/default/laydate.css");
        cssSet.add("static/layui-v2.6.8/layui/css/modules/layer/default/layer.css");
        templateResolver.setCSSTemplateModePatterns(cssSet);
        templateResolver.setCacheable(false);
        Set<String> raw = new HashSet<>();
        for (BaiduCategory value : BaiduCategory.values()) {
            if (value.img!= null && !value.img.isEmpty()){
                raw.add(value.img);
            }
        }

        templateResolver.setRawTemplateModePatterns(raw);
        return templateResolver;
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine templateEngine() {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() {

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");

        return viewResolver;
    }

}
