package com.the.raise.config;

import com.the.raise.dict.Dict;
import com.the.raise.util.Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 自定义拦截器
 * @Auther: T(t-luot)
 * @Date: 2019/10/11 14:43
 */
@Component
public class MyInterceptor implements HandlerInterceptor {  //实现原生拦截器的接口
 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //进行逻辑判断，如果ok就返回true，不行就返回false，返回false就不会处理改请求
        String requestUrl = request.getRequestURI();
        if(!requestUrl.contains("/assets/") && !requestUrl.contains("/node_modules/") && !requestUrl.contains("/admin/") && !requestUrl.contains("/cpts/")){
            HttpSession session = request.getSession();
            if (session.getAttribute(Dict.SESSION_KEY) != null) {
                return true;
            }
            // 跳转登录
            String url = "/Login/toLogin";
            response.sendRedirect(url);
            return false;
        }
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }
}