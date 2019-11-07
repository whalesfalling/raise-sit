package com.the.raise.controller;

import com.the.raise.config.CacheFetchUtils;
import com.the.raise.config.RedisOperations;
import com.the.raise.dict.Dict;
import com.the.raise.util.Utils;
import com.the.raise.model.RaiseUser;
import com.the.raise.service.RaiseUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: T-luot
 * @Date: 2019/10/22 13:03
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping(value = "/Login")
public class LoginController {
    @Autowired
    private RaiseUserService raiseUserService;

    @Autowired
    private RedisOperations redisOperations;

    /**
     * 登陆页面
     *
     * @return
     */
    @RequestMapping(value = {"/toLogin"})
    public ModelAndView toLogin() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        return mv;
    }

    /**
     * 下注页面
     *
     * @return
     */
    @RequestMapping(value = {"/toBet"})
    public ModelAndView toBet() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("bet");
        return mv;
    }

    /**
     * 充值信息处理页面
     *
     * @return
     */
    @RequestMapping(value = {"/toTopUpDispose"})
    public ModelAndView toTopUpDispose() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("topUpDispose");
        return mv;
    }

    /**
     * 充值信息处理页面
     *
     * @return
     */
    @RequestMapping(value = {"/toTopUpHistory"})
    public ModelAndView toTopUpHistory() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("topUpHistory");
        return mv;
    }



    /**
     * 我的页面
     *
     * @return
     */
    @RequestMapping(value = {"/toMy"})
    public ModelAndView toMy() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("my");
        return mv;
    }

    /**
     * 我的页面
     *
     * @return
     */
    @RequestMapping(value = {"/toAdmin"})
    public ModelAndView toAdmin() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin");
        return mv;
    }



    /**
     * 注册页面
     * @return
     */
    @RequestMapping(value = {"/toRegister"})
    public ModelAndView toRegister() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("register");
        return mv;
    }

    /**
     * 主页面
     * @return
     */
    @RequestMapping(value = {"/toIndex"})
    public ModelAndView toIndex() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        return mv;
    }

    /**
     * 代理信息处理页面
     * @return
     */
    @RequestMapping(value = {"/toAgencyDispose"})
    public ModelAndView toAgencyDispose() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("agencyDispose");
        return mv;
    }

    /**
     * 代理页面
     * @return
     */
    @RequestMapping(value = {"/toAgency"})
    public ModelAndView toAgency() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("agency");
        return mv;
    }

    /**
     * 提现页面
     * @return
     */
    @RequestMapping(value = {"/toWithdrawal"})
    public ModelAndView toWithdrawal() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("withdrawal");
        return mv;
    }

    /**
     * 提现信息处理页面
     * @return
     */
    @RequestMapping(value = {"/toWithdrawalDispose"})
    public ModelAndView toWithdrawalDispose() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("withdrawalDispose");
        return mv;
    }

    /**
     * 新增后台人员页面
     * @return
     */
    @RequestMapping(value = {"/toAddAdmin"})
    public ModelAndView toAddAdmin() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("addAdmin");
        return mv;
    }

    /**
     * 提现历史记录页面
     * @return
     */
    @RequestMapping(value = {"/toWithdrawalHistory"})
    public ModelAndView toWithdrawalHistory() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("withdrawalHistory");
        return mv;
    }

    /**
     * 修改密码页面
     * @return
     */
    @RequestMapping(value = {"/toSetPassword"})
    public ModelAndView toSetPassword() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("setPassword");
        return mv;
    }

    /**
     * 上传二维码
     * @return
     */
    @RequestMapping(value = {"/toUploadImg"})
    public ModelAndView toUploadImg() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("uploadImg");
        return mv;
    }

    /**
     * 上传二维码
     * @return
     */
    @RequestMapping(value = {"/toSystemSet"})
    public ModelAndView toSystemSet() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("systemSet");
        return mv;
    }

    /**
     * 联系方式设置
     * @return
     */
    @RequestMapping(value = {"/toWindowsSet"})
    public ModelAndView toWindowsSet() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("windowsSet");
        return mv;
    }

    /**
     * 普通用户列表
     * @return
     */
    @RequestMapping(value = {"/toUseUserList"})
    public ModelAndView toUseUserList() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("useUserList");
        return mv;
    }

    /**
     * 普通用户列表
     * @return
     */
    @RequestMapping(value = {"/toAgencyUserList"})
    public ModelAndView toAgencyUserList() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("agencyUserList");
        return mv;
    }

    /**
     * 普通用户列表
     * @return
     */
    @RequestMapping(value = {"/toSystemUserList"})
    public ModelAndView toSystemUserList() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("systemUserList");
        return mv;
    }




    /**
     * 校验手机号码
     *
     * @return 注册页面
     */
    @ResponseBody
    @RequestMapping(value = {"/CheckoutPhone"})
    public Map CheckoutPhone(@RequestBody Map theMap) {
        String Phone = (String) theMap.get("Phone");
        Map map = new HashMap();
        if (Phone.length() != 11 && Phone.length() != 8 && !Utils.isChinaPhoneLegal(Phone) && !Utils.isChinaPhoneLegal(Phone)) {
            map.put("error", true);
            map.put("user", null);
        } else {
            map.put("error", false);
            map.put("user", getRaiseUser(Phone));
        }
        return map;
    }

    /**
     * 返回客户信息
     *
     * @param phone
     * @return
     */
    private RaiseUser getRaiseUser(String phone) {
        RaiseUser user = raiseUserService.getRaiseUser(phone);
        if (user == null) {
            log.error(" The RaiseUser" + phone + " is null !");
            return null;
        }
        return user;
    }

    /**
     * 获得一个UUID
     * @return String UUID
     */
    public String getUUID(){
        String cardNnumer = Utils.getCard();//调用生成随机数的方法
        RaiseUser user = raiseUserService.QueryRaiseUserByUserid(cardNnumer);
        if(user != null){//如果有相同的数据
            return getUUID();//再次调用方法，生成一个随机数
        }else{//否则
            return cardNnumer;//这个数据我就要
        }
    }

    /**
     * 用户注册
     * @return 注册页面
     */
    @ResponseBody
    @RequestMapping(value = {"/AddRaiseUser"})
    public Map AddRaiseUser(@RequestBody Map theMap) {
        List<String> formValue = (List<String>) theMap.get("formValue");
        String formRecommend =  (String) theMap.get("formRecommend");
        List<RaiseUser> systemUser = raiseUserService.QueryRaiseUserListByIdentity("2");
        boolean has = false;
        Map map = new HashMap();
        for(RaiseUser suser:systemUser){
            if(suser.getRecommend().equals(formRecommend)){
                has = true;
            }
        }
        if(has == false){
            map.put("error", "您输入的推荐人工号不存在");
            return map;
        }
        RaiseUser user = new RaiseUser();
        user.setName((String) theMap.get("formName"));
        user.setSex((String) theMap.get("formSex"));
        user.setPhone((String) theMap.get("formPhone"));
        user.setRecommend(formRecommend);
        user.setPassword((String) theMap.get("formPassword2"));
        user.setProvince(formValue.get(0));
        user.setCity(formValue.get(1));
        user.setUserid(getUUID());
        user.setSetNumber(0);
        int i = raiseUserService.AddRaiseUser(user);
        if (i == 1) {
            log.info(" The RaiseUser" + user.getPhone() + " is success !");
            map.put("error", false);
        } else {
            map.put("error", true);
        }
        return map;
    }

    /**
     * 注销
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/LoginOut"})
    public ModelAndView LoginOut(HttpServletRequest request) {
        RaiseUser user = raiseUserService.getRaiseUser(queryMe(request).getPhone());
        String key = Dict.RAISEUSER + user.getPhone();
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        HttpSession session = request.getSession();
        session.removeAttribute(Dict.SESSION_KEY);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        return mv;
    }

    /**
     * 查询我的个人信息
     *
     * @return
     */
    private RaiseUser queryMe(HttpServletRequest request) {
        HttpSession session = request.getSession();
        RaiseUser raiseUser = raiseUserService.QueryRaiseUserByPhone((String) session.getAttribute(Dict.SESSION_KEY));
        return raiseUser;
    }

    /**
     * 登陆
     * @param theMap
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/Login"})
    public Map Login(@RequestBody Map theMap, HttpServletRequest request) {
        RaiseUser user = raiseUserService.getRaiseUser((String)theMap.get("Phone"));
        Map map = new HashMap();
        if (user == null) {
            map.put("error", "用户不存在，请先注册！");
            return map;
        } else if (!user.getPassword().equals(theMap.get("Password"))) {
            map.put("error", "用户名或密码输入有误！");
            return map;
        } else {
            String key = Dict.RAISEUSER + user.getPhone();
            CacheFetchUtils.cleanRedisByKey(redisOperations,key);
            HttpSession session = request.getSession();
            session.setAttribute(Dict.SESSION_KEY,user.getPhone());
            String phone = (String)session.getAttribute(Dict.SESSION_KEY);
            log.info(" Login user Phone is : " + phone);
            map.put("error", null);
            map.put("user", raiseUserService.QueryRaiseUserByPhone(phone));
            return map;
        }
    }

}
