package com.the.raise.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 拦截器配置
 *
 *
 * @Auther: Tt(yehuawei)
 * @Date: 2018/7/11 15:05
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {
//    以下WebMvcConfigurerAdapter 比较常用的重写接口
//    /** 解决跨域问题 **/
//    public void addCorsMappings(CorsRegistry registry) ;
//    /** 添加拦截器 **/
//    void addInterceptors(InterceptorRegistry registry);
//    /** 这里配置视图解析器 **/
//    void configureViewResolvers(ViewResolverRegistry registry);
//    /** 配置内容裁决的一些选项 **/
//    void configureContentNegotiation(ContentNegotiationConfigurer configurer);
//    /** 视图跳转控制器 **/
//    void addViewControllers(ViewControllerRegistry registry);
//    /** 静态资源处理 **/
//    void addResourceHandlers(ResourceHandlerRegistry registry);
//    /** 默认静态资源处理器 **/
//    void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer);
 
    @Autowired
    private MyInterceptor myInterceptor;

    @Value("${web.upload-path}")
    private String path;
 
    /**
     *
     * 功能描述:
     *  配置静态资源,避免静态资源请求被拦截
     * @auther: Tt(yehuawei)
     * @date:
     * @param:
     * @return:
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/node_modules/**")
                .addResourceLocations("classpath:/static/node_modules/");
        registry.addResourceHandler("/cpts/**")
                .addResourceLocations("classpath:/static/cpts/");
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/static/admin/");
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/opt/img/**")
                .addResourceLocations("file:" + path);
        super.addResourceHandlers(registry);
    }
 
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor)
                //addPathPatterns 用于添加拦截规则
                .addPathPatterns("/**")
                //跳转登陆页面
                .excludePathPatterns("/Login/toLogin")
                //跳转注册页面
                .excludePathPatterns("/Login/toRegister")
                //用户注册
                .excludePathPatterns("/Login/AddRaiseUser")
                //校验手机号码
                .excludePathPatterns("/Login/CheckoutPhone")
                //用户登陆
                .excludePathPatterns("/Login/Login");
        super.addInterceptors(registry);
    }
}