package com.the.raise.controller;

import com.the.raise.config.CacheFetchUtils;
import com.the.raise.config.RedisOperations;
import com.the.raise.dict.Dict;
import com.the.raise.model.*;
import com.the.raise.service.*;
import com.the.raise.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Auther: T-luot
 * @Date: 2019/10/25 13:59
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping(value = "/Raise")
public class RaiseController {
    @Autowired
    private RaisePeriodsService raisePeriodsService;

    @Autowired
    private RaiseWinService raiseWinService;

    @Autowired
    private RaiseUserService raiseUserService;

    @Autowired
    private RedisOperations redisOperations;

    @Autowired
    private RaiseBetService raiseBetService;

    @Autowired
    private RaiseRecordService raiseRecordService;

    @Autowired
    private RaiseVolumeService raiseVolumeService;

    @Autowired
    private RaiseSystemService raiseSystemService;

    @Autowired
    private RaiseAgencyService raiseAgencyService;
    /**
     * 查询总期数信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectRaisePeriodsList"})
    public Map selectRaisePeriodsList() {
        Map map = new HashMap();
        List<RaisePeriods> raisePeriodsList = raisePeriodsService.selectRaisePeriodsList();
        map.put("raisePeriodsList", raisePeriodsList);
        return map;
    }

    /**
     * 查询上期中奖名单信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectRaiseWinList"})
    public Map selectRaiseWinList() {
        Map map = new HashMap();
        try {
            List<RaiseWin> raiseWinList = CacheFetchUtils.fromRedisList(redisOperations, Dict.RELECTRAISEWINLIST, RaiseWin.class, () -> queryRaiseWinList());
            List<RaiseUser> raiseUserList = CacheFetchUtils.fromRedisList(redisOperations, Dict.RAISEUSERLIST, RaiseUser.class, () -> queryWinUserList(raiseWinList));
            map.put("raiseWinList", raiseWinList);
            map.put("winUserList", raiseUserList);
        }catch (Exception e){
            log.error("list is null!");
        }
        return map;
    }

    /**
     * 查询上期中奖用户信息(缓存版)
     *
     * @return
     */
    private List<RaiseUser> queryWinUserList(List<RaiseWin> raiseWinList) {
        List<RaiseUser> raiseUserList = new ArrayList<RaiseUser>();
        for (RaiseWin raiseWin : raiseWinList) {
            RaiseUser raiseUser = raiseUserService.QueryRaiseUserByUserid(raiseWin.getWuserid());
            raiseUser.setPhone(Utils.filtrationPhone(raiseUser.getPhone()));
            raiseUser.setName(Utils.filtrationName(raiseUser.getName()));
            raiseUserList.add(raiseUser);
        }
        return raiseUserList;
    }

    /**
     * 查询上期中奖信息(缓存版)
     *
     * @return
     */
    private List<RaiseWin> queryRaiseWinList() {
        List<RaisePeriods> raisePeriodsList = raisePeriodsService.selectRaisePeriodsList();
        List<RaiseWin> raiseWinList = raiseWinService.selectRaiseWinList(raisePeriodsList.get(1).getPserial());
        return raiseWinList;
    }

    /**
     * 查询当前期数信息
     *
     * @return
     */
    private RaisePeriods queryThisRaise() {
        List<RaisePeriods> raisePeriodsList = raisePeriodsService.selectRaisePeriodsList();
        return raisePeriodsList.get(0);
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
     * 查询系统设置值
     * @param sysName
     * @return
     */
    private Float querySystemValueFloat(String sysName){
        return Float.parseFloat(raiseSystemService.selectRaiseSystemBySysname(sysName));
    }

    /**
     * 查询系统设置值
     * @param sysName
     * @return
     */
    private String querySystemValueString(String sysName){
        return raiseSystemService.selectRaiseSystemBySysname(sysName);
    }

    /**
     * 查询系统设置值
     * @param sysName
     * @return
     */
    private int querySystemValueInt(String sysName){
        return Integer.parseInt(raiseSystemService.selectRaiseSystemBySysname(sysName));
    }

    /**
     * 查询当前期投注信息及用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectThisRaiseAndUser"})
    public Map selectThisRaiseAndUser(HttpServletRequest request) {
        Map map = new HashMap();
        RaisePeriods raisePeriods = CacheFetchUtils.fromRedis(redisOperations, Dict.THISRAISEPERIODS, RaisePeriods.class, () -> queryThisRaise());
        Map thisBetMap = new HashMap();
        RaiseUser user = queryMe(request);
        thisBetMap.put("pserial" , raisePeriods.getPserial());
        thisBetMap.put("userid" , user.getUserid());
        map.put("thisRaisePeriods", raisePeriods);
        map.put("thisPserial",queryThisBetNumber(thisBetMap));
        map.put("thisUser", user);
        map.put("goldNumber", querySystemValueInt("一奖卷等同金币"));
        map.put("moneyNumber", querySystemValueInt("一奖卷等同金额"));
        return map;
    }

    /**
     * 投注
     * @param theMap
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/bitRaise"})
    public synchronized Map bitRaise(@RequestBody Map theMap,HttpServletRequest request){
        Map userMap = (Map)theMap.get("user");
        Map periodsMap = (Map)theMap.get("periods");
        Map thisBetMap = new HashMap();
        thisBetMap.put("pserial" , periodsMap.get("pserial"));
        thisBetMap.put("userid" , userMap.get("userid"));
        HttpSession session = request.getSession();
        Integer goldNumber = (Integer)theMap.get("goldNumber");
        Integer prizeNumber = (Integer)theMap.get("prizeNumber");
        log.info("用户 "+ userMap.get("phone") +" 开始投注,持有奖卷："+ userMap.get("prizenumber") +" 持有金币：" + userMap.get("goldnumber"));

        // 封装投注信息 begin
        RaiseBet bet = new RaiseBet();
        bet.setBcity((String)userMap.get("city"));
        bet.setBuserid((String) userMap.get("userid"));
        bet.setBprovince((String) userMap.get("province"));
        bet.setBperiodsid((Integer) periodsMap.get("pserial"));
        bet.setPrizenumber(prizeNumber);
        bet.setGoldnumber(goldNumber);
        bet.setState("已生效");
        bet.setDate(new Date());
        bet.setBetid((Integer) periodsMap.get("psum") + 1);
        int i = raiseBetService.insertBet(bet);
        log.info("第 "+ periodsMap.get("pserial") +" 期投注成功，有效记录条数：" + i);
        // 封装投注信息 end

        // 奖卷使用 begin
        RaiseVolume raiseVolume = new RaiseVolume();
        raiseVolume.setVuserid((String) userMap.get("userid"));
        raiseVolume.setVpserial((Integer) periodsMap.get("pserial"));
        raiseVolume.setVstate("未使用");
        RaiseVolume useRaise = null;
        for(int z = 0, length = prizeNumber; z < length; z++){
            // 查询用户名下本期可用的第一张奖卷
            useRaise = raiseVolumeService.useVserial(raiseVolume).get(0);
            // 修改奖卷状态，更改为已使用
            useRaise.setVstate("已使用");
            useRaise.setVusetime(new Date());
            // 更新数据库x
           int x =  raiseVolumeService.updateVserial(useRaise);
            log.info("奖卷" + useRaise.getVserial() + "使用成功,生效条数：" + x);
        }
        // 奖卷使用 end

        // 投注用户账户扣款 begin
        RaiseUser user = raiseUserService.QueryRaiseUserByUserid((String) userMap.get("userid"));
        user.setPrizenumber(user.getPrizenumber() - prizeNumber);
        user.setGoldnumber(user.getGoldnumber() - goldNumber + querySystemValueInt("投一注奖励金币数"));
        user.setSetNumber(user.getSetNumber() + 1);
        int j = raiseUserService.UpdateRaiseUserByUserid(user, 0f,- goldNumber + querySystemValueInt("投一注奖励金币数"), - prizeNumber);
        log.info("用户 "+ user.getUserid() +" 投注扣款成功,有效条数："+ j +",扣款金币数：" + goldNumber + " 扣款奖卷数: "+ prizeNumber);
        String key = Dict.RAISEUSER + session.getAttribute(Dict.SESSION_KEY);
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 投注用户账户扣款 end

        // 更新当前投注信息 begin
        RaisePeriods raisePeriods =  queryThisRaise();
        raisePeriods.setPamount(raisePeriods.getPamount() + prizeNumber * querySystemValueFloat("一奖卷等同金额"));
        raisePeriods.setPsum(raisePeriods.getPsum() + 1);
        int k = raisePeriodsService.updateByPrimaryKey(raisePeriods);
        log.info("当前期投注信息更新成功，执行条数：" + k);
        CacheFetchUtils.cleanRedisByKey(redisOperations,Dict.THISRAISEPERIODS);
        // 更新当前投注信息 end

        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRclient(user.getUserid());
        raiseRecord.setRtype("投注");
        raiseRecord.setRmonny(0f);
        raiseRecord.setRgoldnum(-goldNumber + querySystemValueInt("投一注奖励金币数"));
        raiseRecord.setRprizenum(-prizeNumber);
        raiseRecord.setRstate("交易成功");
        raiseRecord.setRdate(new Date());
        raiseRecord.setRdescribe("众筹第" + periodsMap.get("pserial") + "期第" + prizeNumber + "笔投注,奖励" + querySystemValueInt("投一注奖励金币数") + "金币");
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        //交易流水信息记录 end

        Map map = new HashMap();
        map.put("thisRaisePeriods", CacheFetchUtils.fromRedis(redisOperations, Dict.THISRAISEPERIODS, RaisePeriods.class, () -> queryThisRaise()));
        map.put("thisPserial",queryThisBetNumber(thisBetMap));
        map.put("myThisRaiseBetList",queryMyThis(request));
        map.put("thisUser", raiseUserService.QueryRaiseUserByPhone((String) session.getAttribute(Dict.SESSION_KEY)));
        return map;
    }

    /**
     * 获得一个奖卷编号
     * @return String UUID
     */
    public String getUUID(){
        String cardNnumer = Utils.getCard();//调用生成随机数的方法
        RaiseVolume raiseVolume = raiseVolumeService.selectByVserial("A" + cardNnumer);
        if(raiseVolume != null){//如果有相同的数据
            return getUUID();//再次调用方法，生成一个随机数
        }else{//否则
            return cardNnumer;//这个数据我就要
        }
    }

    /**
     * 金卷兑换
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/goldToPrize"})
    public Map goldToPrize(@RequestBody Map theMap,HttpServletRequest request) {
        Integer betNum = Integer.parseInt((String) theMap.get("betNum"));
        RaiseUser raiseUser = queryMe(request);
        HttpSession session = request.getSession();
        // 金卷兑换 begin
        RaiseVolume raiseVolume = new RaiseVolume();
        for(int z = 0, length = betNum; z < length; z++){
            raiseVolume.setVserial("A" + getUUID());
            raiseVolume.setVpserial(queryThisRaise().getPserial());
            raiseVolume.setVstate("未使用");
            raiseVolume.setVuserid(raiseUser.getUserid());
            raiseVolume.setVconversiontime(new Date());
            int i = raiseVolumeService.addRaiseVolume(raiseVolume);
            log.info("金卷兑换成功，有效条数："+ i +"当前奖卷编号：" + raiseVolume.getVserial());
        }
        // 金卷兑换 end

        // 账户扣款 begin
        int goldNumber = raiseUser.getGoldnumber() - betNum * querySystemValueInt("一奖卷等同金币");
        raiseUser.setGoldnumber(goldNumber);
        raiseUser.setPrizenumber(raiseUser.getPrizenumber() + betNum);
        raiseUser.setSetNumber(raiseUser.getSetNumber() + 1);
        int j = raiseUserService.UpdateRaiseUserByUserid(raiseUser,0f, - betNum * querySystemValueInt("一奖卷等同金币"), betNum);
        log.info("用户 "+ raiseUser.getUserid() +" 兑换奖卷成功,有效条数："+ j +",扣款金币数：" + betNum * querySystemValueInt("一奖卷等同金币") + " 新增奖卷数: "+ betNum);
        String key = Dict.RAISEUSER + session.getAttribute(Dict.SESSION_KEY);
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 账户扣款 end

        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRclient(raiseUser.getUserid());
        raiseRecord.setRtype("兑换");
        raiseRecord.setRmonny(0f);
        raiseRecord.setRgoldnum(-betNum * querySystemValueInt("一奖卷等同金币"));
        raiseRecord.setRprizenum(betNum);
        raiseRecord.setRstate("交易成功");
        raiseRecord.setRdate(new Date());
        raiseRecord.setRdescribe("众筹第" + queryThisRaise().getPserial() + "期金币兑换奖卷" + betNum + "卷");
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        //交易流水信息记录 end

        Map map = new HashMap();
        map.put("ThisUser",queryMe(request));
        map.put("goldNumber", querySystemValueInt("一奖卷等同金币"));
        map.put("moneyNumber", querySystemValueInt("一奖卷等同金额"));
        return map;
    }

    /**
     * 奖卷兑换
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/moneyToPrize"})
    public Map moneyToPrize(@RequestBody Map theMap,HttpServletRequest request) {
        Integer betNum = Integer.parseInt((String) theMap.get("betNum"));
        HttpSession session = request.getSession();
        RaiseUser raiseUser = queryMe(request);
        Map map = new HashMap();
        // 奖卷兑换 begin
        RaiseVolume raiseVolume = new RaiseVolume();
        for(int z = 0, length = betNum; z < length; z++){
            raiseVolume.setVserial("A" + getUUID());
            raiseVolume.setVstate("未使用");
            raiseVolume.setVuserid(raiseUser.getUserid());
            raiseVolume.setVpserial(queryThisRaise().getPserial());
            raiseVolume.setVconversiontime(new Date());
            int i = raiseVolumeService.addRaiseVolume(raiseVolume);
            log.info("奖卷兑换成功，有效条数："+ i +"当前奖卷编号：" + raiseVolume.getVserial());
        }
        // 将卷兑换 end

        // 账户扣款（现金） begin
        Float money = raiseUser.getSummoney() - betNum * querySystemValueFloat("一奖卷等同金额");
        raiseUser.setSummoney(money);
        raiseUser.setSetNumber(raiseUser.getSetNumber() + 1);
        raiseUser.setPrizenumber(raiseUser.getPrizenumber() + betNum);
        int i = raiseUserService.UpdateRaiseUserByUserid(raiseUser,- betNum * querySystemValueFloat("一奖卷等同金额"),0, betNum);
        String key = Dict.RAISEUSER + session.getAttribute(Dict.SESSION_KEY);
        log.info("用户 "+ raiseUser.getUserid() +" 兑换奖卷成功,有效条数："+ i +",扣款金额：" + betNum * querySystemValueFloat("一奖卷等同金额") + " 新增奖卷数: "+ betNum);
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 账户扣款（现金） end

        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRtype("兑换");
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRclient(raiseUser.getUserid());
        raiseRecord.setRmonny(-betNum * querySystemValueFloat("一奖卷等同金额"));
        raiseRecord.setRgoldnum(0);
        raiseRecord.setRprizenum(betNum);
        raiseRecord.setRdate(new Date());
        raiseRecord.setRstate("交易成功");
        raiseRecord.setRdescribe("众筹第" + queryThisRaise().getPserial() + "期现金兑换奖卷" + betNum + "卷");
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        //交易流水信息记录 end

        map.put("ThisUser",queryMe(request));
        map.put("goldNumber", querySystemValueInt("一奖卷等同金币"));
        map.put("moneyNumber", querySystemValueInt("一奖卷等同金额"));
        return map;
    }


    /**
     * 查询本期我的投注信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryMyThisRaiseBetList"})
    public Map queryMyThisRaiseBetList(HttpServletRequest request) {
        Map map = new HashMap();
        map.put("myThisRaiseBetList",queryMyThis(request));
        return map;
    }

    /**
     * 查询本期我的投注信息
     *
     * @return
     */
    private List<RaiseBet> queryMyThis(HttpServletRequest request){
        RaiseBet raiseBet = new RaiseBet();
        RaiseUser raiseUser = queryMe(request);
        raiseBet.setBuserid(raiseUser.getUserid());
        raiseBet.setBperiodsid(queryThisRaise().getPserial());
        return raiseBetService.queryMyThisRaiseBetList(raiseBet);
    }

    /**
     * 当前期数我的投注数
     * @param thisValue
     * @return
     */
    private Integer queryThisBetNumber(Map thisValue){
        int i = raiseBetService.queryThisBetNumber(thisValue);
        return i;
    }

    /**
     * 用户充值
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/topUpMoney"})
    public Map topUpMoney(@RequestBody Map theMap,HttpServletRequest request) {
        Float money  = Float.parseFloat((String) theMap.get("topUp"));
        RaiseUser raiseUser = queryMe(request);
        Map map = new HashMap();
        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRtype("充值");
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRclient(raiseUser.getUserid());
        raiseRecord.setRmonny(money);
        raiseRecord.setRgoldnum(0);
        raiseRecord.setRprizenum(0);
        raiseRecord.setRdate(new Date());
        raiseRecord.setRstate("尚在核实");
        raiseRecord.setRdescribe("用户" + raiseUser.getName() + "进行充值,充值金额" + money + "元,用户手机号："+ raiseUser.getPhone());
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        //交易流水信息记录 end
        map.put("ThisUser",queryMe(request));
        map.put("MyRaiseRecord", raiseRecordService.selectMyRaiseRecord(raiseRecord));
        return map;
    }

    /**
     * 查询当前期投注信息及用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectUser"})
    public Map selectUser(HttpServletRequest request) {
        Map map = new HashMap();
        String showImg = querySystemValueString("默认付款码");
        String imgUrl = querySystemValueString(showImg);
        List<String> imgUrlList = new ArrayList<String>();
        imgUrlList.add(imgUrl);
        map.put("imgUrl", imgUrl);
        map.put("imgUrlList", imgUrlList);
        map.put("thisUser", queryMe(request));
        map.put("poundage", querySystemValueFloat("提现收取手续费用占比"));
        return map;
    }

    /**
     * 查询联系方式信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selecWindows"})
    public Map selecWindows(HttpServletRequest request) {
        Map map = new HashMap();
        map.put("ServiceTel", querySystemValueString("客服电话"));
        map.put("Email", querySystemValueString("邮箱"));
        map.put("WeiXin", querySystemValueString("微信"));
        map.put("TxQQ", querySystemValueString("QQ"));
        map.put("DomainName", querySystemValueString("域名"));
        return map;
    }

    /**
     * 查询我的流水信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectMyRaiseRecord"})
    public Map selectMyRaiseRecord(HttpServletRequest request) {
        Map map = new HashMap();
        RaiseUser user = queryMe(request);
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRclient(user.getUserid());
        map.put("MyRaiseRecord", raiseRecordService.selectMyRaiseRecord(raiseRecord));
        return map;
    }

    /**
     * 查询当前期投注信息及用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectMyVolume"})
    public Map selectMyVolume(HttpServletRequest request) {
        Map map = new HashMap();
        RaiseUser user = queryMe(request);
        RaiseVolume raiseVolume = new RaiseVolume();
        raiseVolume.setVuserid(user.getUserid());
        map.put("myRaiseVolumeList", raiseVolumeService.queryMyVserial(raiseVolume));
        return map;
    }

    /**
     * 省代理申请
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/setProvince"})
    public Map setProvince(@RequestBody Map theMap,HttpServletRequest request) {
        List<String> formValue = (List<String>) theMap.get("province");
        RaiseUser user = queryMe(request);
        Map map = new HashMap();
        HttpSession session = request.getSession();
        if(user.getSummoney() < querySystemValueFloat("省代理购买金额")){
            map.put("error" , "账户余额不足，请先充值!");
            return map;
        }
        // 代理记录 begin
        RaiseAgency raiseAgency = new RaiseAgency();
        raiseAgency.setAcustomer(user.getUserid());
        raiseAgency.setAcity(null);
        raiseAgency.setAdate(new Date());
        raiseAgency.setAprovince(formValue.get(0));
        raiseAgency.setAprice(querySystemValueFloat("省代理购买金额"));
        raiseAgencyService.insertRaiseAgency(raiseAgency);
        // 代理记录 end

        // 本人名下账户扣款 begin
        user.setSummoney(user.getSummoney() - querySystemValueFloat("省代理购买金额"));
        user.setIdentity("1");
        user.setSetNumber(user.getSetNumber() + 1);
        raiseUserService.UpdateRaiseUserByUserid(user,- querySystemValueFloat("省代理购买金额"),0,0);
        String key = Dict.RAISEUSER + session.getAttribute(Dict.SESSION_KEY);
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 本人名下账户扣款 end

        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRtype("代理");
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRclient(user.getUserid());
        raiseRecord.setRmonny(-querySystemValueFloat("省代理购买金额"));
        raiseRecord.setRgoldnum(0);
        raiseRecord.setRdate(new Date());
        raiseRecord.setRprizenum(0);
        raiseRecord.setRstate("交易成功");
        raiseRecord.setRdescribe(formValue.get(0) + "代理购买");
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        //交易流水信息记录 end

        map.put("error" , null);
        map.put("raiseAgency" , raiseAgencyService.selectRaiseAgency(user.getUserid()));
        return map;
    }

    /**
     * 市代理申请
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/setCity"})
    public Map setCity(@RequestBody Map theMap,HttpServletRequest request) {
        List<String> formValue = (List<String>) theMap.get("city");
        Map map = new HashMap();
        RaiseUser user = queryMe(request);
        HttpSession session = request.getSession();
        if(user.getSummoney() < querySystemValueFloat("市代理购买金额")){
            map.put("error" , "账户余额不足，请先充值!");
            return map;
        }
        // 代理记录 begin
        RaiseAgency raiseAgency = new RaiseAgency();
        raiseAgency.setAcustomer(user.getUserid());
        raiseAgency.setAcity(formValue.get(1));
        raiseAgency.setAdate(new Date());
        raiseAgency.setAprovince(formValue.get(0));
        raiseAgency.setAprice(querySystemValueFloat("市代理购买金额"));
        raiseAgencyService.insertRaiseAgency(raiseAgency);
        // 代理记录 end

        // 本人名下账户扣款 begin
        user.setSummoney(user.getSummoney() - querySystemValueFloat("市代理购买金额"));
        user.setIdentity("1");
        user.setSetNumber(user.getSetNumber() + 1);
        String key = Dict.RAISEUSER + session.getAttribute(Dict.SESSION_KEY);
        raiseUserService.UpdateRaiseUserByUserid(user,- querySystemValueFloat("市代理购买金额"),0,0);
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 本人名下账户扣款 end

        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRtype("代理");
        raiseRecord.setRclient(user.getUserid());
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRmonny(-querySystemValueFloat("市代理购买金额"));
        raiseRecord.setRgoldnum(0);
        raiseRecord.setRprizenum(0);
        raiseRecord.setRstate("交易成功");
        raiseRecord.setRdate(new Date());
        raiseRecord.setRdescribe(formValue.get(1) + "代理购买");
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        //交易流水信息记录 end
        map.put("raiseAgency" , raiseAgencyService.selectRaiseAgency(user.getUserid()));
        map.put("error" , null);
        return map;
    }

    /**
     * 查询当前期投注信息及用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectUserAndMoney"})
    public Map selectUserAndMoney(HttpServletRequest request) {
        Map map = new HashMap();
        map.put("thisUser", queryMe(request));
        map.put("provincePrice", querySystemValueFloat("省代理购买金额"));
        map.put("cityPrice", querySystemValueFloat("市代理购买金额"));
        map.put("raiseAgency" , raiseAgencyService.selectRaiseAgency(queryMe(request).getUserid()));
        return map;
    }

    /**
     * 用户绑定银行卡
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/bindCard"})
    public Map bindCard(@RequestBody Map theMap,HttpServletRequest request) {
        String card = (String)theMap.get("card");
        Map map = new HashMap();
        RaiseUser user = queryMe(request);
        if(!Utils.checkBankCard(card)){
            map.put("thisUser", user);
            map.put("error" , "银行卡号输入有误！");
            return map;
        }
        user.setCard(card);
        user.setSetNumber(user.getSetNumber() + 1);
        raiseUserService.UpdateRaiseUserByUserid(user,0f,0,0);
        HttpSession session = request.getSession();
        String key = Dict.RAISEUSER + session.getAttribute(Dict.SESSION_KEY);
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        map.put("error" , null);
        map.put("thisUser", queryMe(request));
        return map;
    }

    /**
     * 提现申请
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/withdrawalMoney"})
    public Map withdrawalMoney(@RequestBody Map theMap,HttpServletRequest request) {
        Float withMoney = Float.parseFloat((String)theMap.get("withMoney"));
        Map map = new HashMap();
        //交易流水信息记录 begin
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRtype("提现");
        raiseRecord.setRclient(queryMe(request).getUserid());
        raiseRecord.setRno(Utils.getRecordNo());
        raiseRecord.setRmonny(-withMoney);
        raiseRecord.setRgoldnum(0);
        raiseRecord.setRprizenum(0);
        raiseRecord.setRstate("等待打款");
        raiseRecord.setRdate(new Date());
        raiseRecord.setRdescribe("用户申请提现：" + withMoney + "元,手续费用占比" + querySystemValueFloat("提现收取手续费用占比") + ",最终汇款金额：" + (withMoney - withMoney * querySystemValueFloat("提现收取手续费用占比")));
        raiseRecordService.insertRaiseRecord(raiseRecord);
        log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        map.put("thisUser", queryMe(request));
        map.put("poundage", querySystemValueFloat("提现收取手续费用占比"));
        map.put("MyWithdrawal", raiseRecordService.selectMyRaiseAgency(raiseRecord));
        return map;
    }

    /**
     * 查询我的代理收益记录
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectMyRaiseAgency"})
    public Map selectMyRaiseAgency(HttpServletRequest request) {
        Map map = new HashMap();
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRclient(queryMe(request).getUserid());
        raiseRecord.setRtype("收益");
        map.put("myRaiseAgency" ,raiseRecordService.selectMyRaiseAgency(raiseRecord));
        map.put("raiseAgency" , raiseAgencyService.selectRaiseAgency(queryMe(request).getUserid()));
        return map;
    }

    /**
     * 查询我的提现信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/selectMyWithdrawal"})
    public Map selectMyWithdrawal(HttpServletRequest request) {
        Map map = new HashMap();
        RaiseUser user = queryMe(request);
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRclient(user.getUserid());
        raiseRecord.setRtype("提现");
        map.put("MyWithdrawal", raiseRecordService.selectMyRaiseAgency(raiseRecord));
        return map;
    }

}
