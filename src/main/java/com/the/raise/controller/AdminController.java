package com.the.raise.controller;

import com.the.raise.config.CacheFetchUtils;
import com.the.raise.config.RedisOperations;
import com.the.raise.dict.Dict;
import com.the.raise.model.*;
import com.the.raise.service.*;
import com.the.raise.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Auther: T-luot
 * @Date: 2019/10/29 14:34
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping(value = "/Admin")
public class AdminController {

    @Autowired
    private RaiseUserService raiseUserService;

    @Autowired
    private RaisePeriodsService raisePeriodsService;

    @Autowired
    private RaiseVolumeService raiseVolumeService;

    @Autowired
    private RaiseRecordService raiseRecordService;

    @Autowired
    private RedisOperations redisOperations;

    @Autowired
    private RaiseSystemService raiseSystemService;

    @Autowired
    private RaiseBetService raiseBetService;

    @Autowired
    private RaiseWinService raiseWinService;

    @Autowired
    private RaiseAgencyService raiseAgencyService;

    @Value("${web.upload-path}")
    private String path;

    /**
     * 查询当前期投注信息及用户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/adminContext"})
    public Map adminContext(HttpServletRequest request) {
        Map map = new HashMap();
        // 普通用户数量
        map.put("commonUser", QueryRaiseUserByIdentity("0"));
        // 代理用户数量
        map.put("agencyUser", QueryRaiseUserByIdentity("1"));
        // 众筹总期数
        map.put("periodsNumber", QueryRaisePeriodsNumber());
        // 奖卷兑换总数
        map.put("volumeNumber", QueryVserialNumber());
        // 本期众筹信息
        map.put("thisRaise", queryThisRaise());
        // 本期投注奖卷总数
        map.put("betVserialNumber", QueryBetVserialNumber(queryThisRaise().getPserial()));
        return map;
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
     * 开奖
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/lottery"})
    public Map lottery(HttpServletRequest request) {
        Map map = new HashMap();
        Integer one = querySystemValueInt("一等奖人数");
        Integer two = querySystemValueInt("二等奖人数");
        Integer three = querySystemValueInt("三等奖人数");
        Integer sum = querySystemValueInt("每期中奖人数");
        Integer fen = three * 1 + two * 2 + one * 3;
        if(one + two + three != sum){
            map.put("error","开奖人数设定有误，请重新设定！");
            return map;
        }
        // 获取本期定投信息
        RaisePeriods raisePeriods = queryThisRaise();
        // 计算开奖的金额
        Float lotteryMoney =  raisePeriods.getPamount() *  querySystemValueFloat("奖金总额占比");
        log.info("本期开奖总金额：" + lotteryMoney + "元");

        // 获取本期所有的投注信息 begin
        RaiseBet raisebet = new RaiseBet();
        raisebet.setBperiodsid(raisePeriods.getPserial());
        raisebet.setState("已生效");
        List<RaiseBet> raiseBet = raiseBetService.queryMyThisRaiseBetListByBperiodsid(raisebet);
        // 获取本期所有的投注信息 end

        // 获取本期所有投注用户 begin
        HashSet<String> userList = new HashSet<String>();
        for (Iterator<RaiseBet> iterList = raiseBet.iterator(); iterList.hasNext();) {
            userList.add(iterList.next().getBuserid());
        }
        // 获取本期所有投注用户 end

        // 中奖编号
        String str = "";

        // 一等奖开奖 begin
        if(userList.size()!=0){
            Random random = new Random();
            for(int i = 0; i < one; i++){
                if(userList.size()==0){
                    break;
                }
                int n = random.nextInt(raiseBet.size());
                RaiseBet bet = raiseBet.get(n);
                // 中奖人信息 begin
                String userid = bet.getBuserid();
                Integer betid = bet.getBetid();
                str += betid + "-";
                // 中奖人信息 end

                // 获取中奖用户打钱 begin
                RaiseUser user = raiseUserService.QueryRaiseUserByUserid(userid);
                Float money = lotteryMoney / fen * 3; // 一等奖获奖金额
                money = (float)Math.round(money*100)/100;
                user.setSummoney(user.getSummoney() + money);
                user.setSetNumber(user.getSetNumber()+1);
                raiseUserService.UpdateRaiseUserByUserid(user,money,0,0);
                log.info("一等奖用户" + user.getName() + "获奖" + money + "元");
                // 获取中奖用户打钱 end

                // 记录中奖人信息 begin
                RaiseWin raiseWin = new RaiseWin();
                raiseWin.setWmoney(money);
                raiseWin.setWnoun("一等奖");
                raiseWin.setWperiodsid(raisePeriods.getPserial());
                raiseWin.setWuserid(userid);
                raiseWinService.insertRaiseWin(raiseWin);
                // 记录中奖人信息 end

                //交易流水信息记录 begin
                RaiseRecord raiseRecord = new RaiseRecord();
                raiseRecord.setRno(Utils.getRecordNo());
                raiseRecord.setRclient(user.getUserid());
                raiseRecord.setRtype("获奖");
                raiseRecord.setRmonny(money);
                raiseRecord.setRgoldnum(0);
                raiseRecord.setRprizenum(0);
                raiseRecord.setRstate("汇款成功");
                raiseRecord.setRdate(new Date());
                raiseRecord.setRdescribe("众筹第" + raisePeriods.getPserial() + "期中一等奖，奖金" + money + "元");
                raiseRecordService.insertRaiseRecord(raiseRecord);
                log.info("流水记录成功，流水号：" + raiseRecord.getRno());

                //清除该用户奖卷信息
                for (Iterator<RaiseBet> iter = raiseBet.iterator(); iter.hasNext();) {
                    if(iter.next().getBuserid().equals(userid)){
                        iter.remove();
                    }
                }

                //清除该用户列表信息
                for (Iterator<String> iter = userList.iterator(); iter.hasNext();) {
                    if(iter.next().equals(userid)){
                        iter.remove();
                    }
                }
            }
        }
        // 一等奖开奖 end

        // 二等奖开奖 begin
        if(userList.size()!=0){
            Random random = new Random();
            for(int i = 0; i < two; i++){
                if(userList.size()==0){
                    break;
                }
                int n = random.nextInt(raiseBet.size());
                RaiseBet bet = raiseBet.get(n);
                // 中奖人信息 begin
                Integer betid = bet.getBetid();
                String userid = bet.getBuserid();
                str += betid + "-";
                // 中奖人信息 end

                // 获取中奖用户打钱 begin
                RaiseUser user = raiseUserService.QueryRaiseUserByUserid(userid);
                Float money = lotteryMoney / fen * 2; // 二等奖获奖金额
                money = (float)Math.round(money*100)/100;
                user.setSetNumber(user.getSetNumber()+1);
                user.setSummoney(user.getSummoney() + money);
                raiseUserService.UpdateRaiseUserByUserid(user,money,0,0);
                log.info("二等奖用户" + user.getName() + "获奖" + money + "元");
                // 获取中奖用户打钱 end

                // 记录中奖人信息 begin
                RaiseWin raiseWin = new RaiseWin();
                raiseWin.setWmoney(money);
                raiseWin.setWnoun("二等奖");
                raiseWin.setWuserid(userid);
                raiseWin.setWperiodsid(raisePeriods.getPserial());
                raiseWinService.insertRaiseWin(raiseWin);
                // 记录中奖人信息 end

                //交易流水信息记录 begin
                RaiseRecord raiseRecord = new RaiseRecord();
                raiseRecord.setRclient(user.getUserid());
                raiseRecord.setRno(Utils.getRecordNo());
                raiseRecord.setRtype("获奖");
                raiseRecord.setRgoldnum(0);
                raiseRecord.setRprizenum(0);
                raiseRecord.setRmonny(money);
                raiseRecord.setRstate("汇款成功");
                raiseRecord.setRdate(new Date());
                raiseRecord.setRdescribe("众筹第" + raisePeriods.getPserial() + "期中二等奖，奖金" + money + "元");
                raiseRecordService.insertRaiseRecord(raiseRecord);
                log.info("流水记录成功，流水号：" + raiseRecord.getRno());

                //清除该用户奖卷信息
                for (Iterator<RaiseBet> iter = raiseBet.iterator(); iter.hasNext();) {
                    if(iter.next().getBuserid().equals(userid)){
                        iter.remove();
                    }
                }

                //清除该用户列表信息
                for (Iterator<String> iter = userList.iterator(); iter.hasNext();) {
                    if(iter.next().equals(userid)){
                        iter.remove();
                    }
                }
            }
        }
        // 二等奖开奖 end

        // 三等奖开奖 begin
        if(userList.size()!=0){
            Random random = new Random();
            for(int i = 0; i < three; i++){
                if(userList.size()==0){
                    break;
                }
                int n = random.nextInt(raiseBet.size());
                RaiseBet bet = raiseBet.get(n);
                // 中奖人信息 begin
                Integer betid = bet.getBetid();
                str += betid + "-";
                String userid = bet.getBuserid();
                // 中奖人信息 end

                // 获取中奖用户打钱 begin
                RaiseUser user = raiseUserService.QueryRaiseUserByUserid(userid);
                Float money = lotteryMoney / fen * 1; // 三等奖获奖金额
                money = (float)Math.round(money*100)/100;
                user.setSummoney(user.getSummoney() + money);
                user.setSetNumber(user.getSetNumber()+1);
                raiseUserService.UpdateRaiseUserByUserid(user,money,0,0);
                log.info("三等奖用户" + user.getName() + "获奖" + money + "元");
                // 获取中奖用户打钱 end

                // 记录中奖人信息 begin
                RaiseWin raiseWin = new RaiseWin();
                raiseWin.setWmoney(money);
                raiseWin.setWnoun("三等奖");
                raiseWin.setWperiodsid(raisePeriods.getPserial());
                raiseWin.setWuserid(userid);
                raiseWinService.insertRaiseWin(raiseWin);
                // 记录中奖人信息 end

                //交易流水信息记录 begin
                RaiseRecord raiseRecord = new RaiseRecord();
                raiseRecord.setRclient(user.getUserid());
                raiseRecord.setRno(Utils.getRecordNo());
                raiseRecord.setRtype("获奖");
                raiseRecord.setRprizenum(0);
                raiseRecord.setRgoldnum(0);
                raiseRecord.setRmonny(money);
                raiseRecord.setRdate(new Date());
                raiseRecord.setRstate("汇款成功");
                raiseRecord.setRdescribe("众筹第" + raisePeriods.getPserial() + "期中三等奖，奖金" + money + "元");
                raiseRecordService.insertRaiseRecord(raiseRecord);
                log.info("流水记录成功，流水号：" + raiseRecord.getRno());

                //清除该用户奖卷信息
                for (Iterator<RaiseBet> iter = raiseBet.iterator(); iter.hasNext();) {
                    if(iter.next().getBuserid().equals(userid)){
                        iter.remove();
                    }
                }

                //清除该用户列表信息
                for (Iterator<String> iter = userList.iterator(); iter.hasNext();) {
                    if(iter.next().equals(userid)){
                        iter.remove();
                    }
                }
            }
        }
        // 三等奖开奖 end

        // 未中奖用户发放金币 begin
        Integer gold = querySystemValueInt("未中奖奖励金币数");
        //清除该用户奖卷信息
        for (Iterator<String> iter = userList.iterator(); iter.hasNext();) {
            String userid = iter.next();
            RaiseUser user = raiseUserService.QueryRaiseUserByUserid(userid);
            user.setSetNumber(user.getSetNumber() + 1);
            user.setGoldnumber(user.getGoldnumber() + gold);
            raiseUserService.UpdateRaiseUserByUserid(user,0f,gold,0);
            log.info("未中奖用户" + user.getName() + "获奖" + gold + "金币");
            //交易流水信息记录 begin
            RaiseRecord raiseRecord = new RaiseRecord();
            raiseRecord.setRclient(user.getUserid());
            raiseRecord.setRno(Utils.getRecordNo());
            raiseRecord.setRtype("奖励");
            raiseRecord.setRprizenum(0);
            raiseRecord.setRgoldnum(gold);
            raiseRecord.setRmonny(0f);
            raiseRecord.setRdate(new Date());
            raiseRecord.setRstate("发放成功");
            raiseRecord.setRdescribe("众筹第" + raisePeriods.getPserial() + "期未中奖，奖励" + gold + "金币，望再接再励！");
            raiseRecordService.insertRaiseRecord(raiseRecord);
            log.info("流水记录成功，流水号：" + raiseRecord.getRno());
        }
        // 未中奖用户发放金币 end

        // 代理收益 begin
        List<RaiseAgency> raiseAgencyList = raiseAgencyService.selectRaiseAgencyList();
        for (Iterator<RaiseAgency> iters= raiseAgencyList.iterator(); iters.hasNext();) {
            if(userList.size()==0){
                break;
            }
            RaiseAgency raiseAgency =  iters.next();
            Integer goldNumber = 0;
            for (Iterator<String> iter = userList.iterator(); iter.hasNext();) {
                String userid =  iter.next();
                RaiseUser user = raiseUserService.QueryRaiseUserByUserid(userid);
                if(user.getProvince().equals(raiseAgency.getAcustomer())){
                    goldNumber += gold;
                }
            }
            // 省代理
            if(raiseAgency.getAcity()!=null){
                goldNumber = (int)(goldNumber * querySystemValueFloat("市代理获取金币占比"));
            }else{
                goldNumber = (int)(goldNumber * querySystemValueFloat("省代理获取金币占比"));
            }
            RaiseUser user = raiseUserService.QueryRaiseUserByUserid(raiseAgency.getAcustomer());
            user.setGoldnumber(user.getGoldnumber() + goldNumber);
            user.setSetNumber(user.getSetNumber()+1);
            raiseUserService.UpdateRaiseUserByUserid(user,0f,goldNumber,0);
            RaiseRecord raiseRecord = new RaiseRecord();
            raiseRecord.setRclient(user.getUserid());
            raiseRecord.setRtype("收益");
            raiseRecord.setRno(Utils.getRecordNo());
            raiseRecord.setRprizenum(0);
            raiseRecord.setRgoldnum(goldNumber);
            raiseRecord.setRmonny(0f);
            raiseRecord.setRdate(new Date());
            if(goldNumber==0){
                raiseRecord.setRstate("未有收益");
                raiseRecord.setRdescribe("众筹第" + raisePeriods.getPserial() + "期未有" + raiseAgency.getAprovince() + "的用户投注");
            }else{
                raiseRecord.setRstate("收益到账");
                raiseRecord.setRdescribe("众筹第" + raisePeriods.getPserial() + "期代理收益金币" + goldNumber + "个");
            }
            raiseRecordService.insertRaiseRecord(raiseRecord);
            log.info("流水记录成功，流水号：" + raiseRecord.getRno());
            // 代理收益 end

        }

        // 获取本期所有的奖卷 begin
        RaiseVolume raiseVolume = new RaiseVolume();
        raiseVolume.setVpserial(raisePeriods.getPserial());
        raiseVolume.setVstate("未使用");
        List<RaiseVolume> raiseVolumeList =  raiseVolumeService.queryBetVserialList(raiseVolume);
        for (Iterator<RaiseVolume> iter = raiseVolumeList.iterator(); iter.hasNext();) {
            RaiseVolume raise =  iter.next();
            raise.setVstate("已过期");
            raise.setVfaluretime(new Date());
            raiseVolumeService.updateVserial(raise);
            RaiseUser user = raiseUserService.QueryRaiseUserByUserid(raise.getVuserid());
            user.setSetNumber(user.getSetNumber() + 1);
            user.setPrizenumber(user.getPrizenumber() - 1);
            raiseUserService.UpdateRaiseUserByUserid(user,0f,0,-1);
        }
        // 获取本期所有的奖卷 end

        // 更期换代
        raisePeriods.setPstate("已开注");
        raisePeriods.setPdetails(str);
        raisePeriods.setPsubjection("往期");
        raisePeriodsService.updateByPrimaryKey(raisePeriods);
        raisePeriods.setPamount(0f);
        raisePeriods.setPstate("待开注");
        raisePeriods.setPsum(0);
        raisePeriods.setPdetails("未开奖");
        raisePeriods.setPdescribe("众筹第"+ (raisePeriods.getPserial() + 1) +"期");
        raisePeriods.setPsubjection("当期");
        raisePeriodsService.AddRaisePeriods(raisePeriods);

        CacheFetchUtils.cleanRedisByKey(redisOperations, Dict.RAISEUSERLIST);
        CacheFetchUtils.cleanALLRedisByKey(redisOperations, Dict.RAISEUSER);
        CacheFetchUtils.cleanRedisByKey(redisOperations, Dict.RELECTRAISEWINLIST);
        CacheFetchUtils.cleanRedisByKey(redisOperations, Dict.THISRAISEPERIODS);

        List<RaiseWin> raiseWinList = CacheFetchUtils.fromRedisList(redisOperations, Dict.RELECTRAISEWINLIST, RaiseWin.class, () -> queryRaiseWinList());
        List<RaiseUser> raiseUserList = CacheFetchUtils.fromRedisList(redisOperations, Dict.RAISEUSERLIST, RaiseUser.class, () -> queryWinUserList(raiseWinList));
        map.put("raiseWinList", raiseWinList);
        map.put("winUserList", raiseUserList);
        // 普通用户数量
        map.put("commonUser", QueryRaiseUserByIdentity("0"));
        // 代理用户数量
        map.put("agencyUser", QueryRaiseUserByIdentity("1"));
        // 众筹总期数
        map.put("periodsNumber", QueryRaisePeriodsNumber());
        // 奖卷兑换总数
        map.put("volumeNumber", QueryVserialNumber());
        // 本期众筹信息
        map.put("thisRaise", queryThisRaise());
        // 本期投注奖卷总数
        map.put("betVserialNumber", QueryBetVserialNumber(queryThisRaise().getPserial()));
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
     * 根据客户类型查找客户总数
     * @param identity
     * @return
     */
    private int QueryRaiseUserByIdentity (String identity){
        return raiseUserService.QueryRaiseUserByIdentity(identity);
    }

    /**
     * 查询众筹总期数
     * @return
     */
    private int QueryRaisePeriodsNumber (){
        return raisePeriodsService.QueryRaisePeriodsNumber();
    }

    /**
     * 查询兑换奖卷总数
     * @return
     */
    private int QueryVserialNumber (){
        return raiseVolumeService.queryVserialNumber();
    }

    /**
     * 查询兑换奖卷总数
     * @return
     */
    private int QueryBetVserialNumber (Integer periods){
        return raiseVolumeService.queryBetVserialNumber(periods);
    }

    /**
     * 查询所有待确认的充值信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryTopUpMoneyList"})
    public Map queryTopUpMoneyList() {
        Map map = new HashMap();
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRstate("尚在核实");
        map.put("topUpMoneyList", raiseRecordService.selectTopUpMoneyList(raiseRecord));
        return map;
    }

    /**
     * 查询所有待确认的充值信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryAgencyMoneyList"})
    public Map queryAgencyMoneyList() {
        Map map = new HashMap();
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRstate("交易成功");
        map.put("agencyMoneyList", raiseRecordService.selectAgencyMoneyList(raiseRecord));
        return map;
    }

    /**
     * 查看客户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryUser"})
    public Map queryUser(@RequestBody Map theMap) {
        String rclient = (String) theMap.get("rclient");
        Map map = new HashMap();
        map.put("user", raiseUserService.QueryRaiseUserByUserid(rclient));
        return map;
    }

    /**
     * 充值
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/topUpMoney"})
    public synchronized Map topUpMoney(@RequestBody Map theMap) {
        String rno = (String) theMap.get("rno");
        Map map = new HashMap();
        RaiseRecord raiseRecord = raiseRecordService.selectTopUpMoneyByRno(rno);
        if(raiseRecord.getSetdate()!=null){
            raiseRecord.setRstate("尚在核实");
            map.put("error", "该记录已被办理,系统将为您刷新记录！");
            map.put("topUpMoneyList", raiseRecordService.selectTopUpMoneyList(raiseRecord));
            return map;
        }
        raiseRecord.setRstate("确认核实");
        raiseRecord.setSetdate(new Date());
        raiseRecordService.updateByPrimaryKey(raiseRecord);

        // 账户充值 begin
        RaiseUser user = raiseUserService.QueryRaiseUserByUserid(raiseRecord.getRclient());
        log.info("用户充值前余额：" + user.getSummoney());
        user.setSummoney(user.getSummoney() + raiseRecord.getRmonny());
        user.setSetNumber(user.getSetNumber() + 1);
        raiseUserService.UpdateRaiseUserByUserid(user,raiseRecord.getRmonny(),0,0);
        log.info("用户充值后余额：" + user.getSummoney());
        String key = Dict.RAISEUSER + user.getPhone();
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 账户充值 end

        raiseRecord.setRstate("尚在核实");
        map.put("topUpMoneyList", raiseRecordService.selectTopUpMoneyList(raiseRecord));
        return map;
    }

    /**
     * 查询所有已确认的充值信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryTopUpMoneyHistoryList"})
    public Map queryTopUpMoneyHistoryList() {
        Map map = new HashMap();
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRstate("确认核实");
        map.put("topUpMoneyList", raiseRecordService.selectTopUpMoneyList(raiseRecord));
        return map;
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
     * 查询所有待确认的提现信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryWithdrawalDispose"})
    public Map queryWithdrawalDispose() {
        Map map = new HashMap();
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRstate("等待打款");
        map.put("poundage", querySystemValueFloat("提现收取手续费用占比"));
        map.put("withdrawalDispose", raiseRecordService.selectMyWithdrawal(raiseRecord));
        return map;
    }

    /**
     * 查询系统设置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/querySystem"})
    public Map querySystem() {
        Map map = new HashMap();
        map.put("value1", querySystemValueString("每期中奖人数"));
        map.put("value2", querySystemValueString("一等奖人数"));
        map.put("value3", querySystemValueString("二等奖人数"));
        map.put("value4", querySystemValueString("三等奖人数"));
        map.put("value5", querySystemValueString("未中奖奖励金币数"));
        map.put("value6", querySystemValueString("提现收取手续费用占比"));
        map.put("value7", querySystemValueString("奖金总额占比"));
        map.put("value8", querySystemValueString("省代理购买金额"));
        map.put("value9", querySystemValueString("市代理购买金额"));
        map.put("value10", querySystemValueString("省代理获取金币占比"));
        map.put("value11", querySystemValueString("市代理获取金币占比"));
        map.put("value12", querySystemValueString("投一注奖励金币数"));
        map.put("value13", querySystemValueString("一奖卷等同金额"));
        map.put("value14", querySystemValueString("一奖卷等同金币"));
        return map;
    }

    /**
     * 查询系统设置
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryWindows"})
    public Map queryWindows() {
        Map map = new HashMap();
        map.put("value1", querySystemValueString("客服电话"));
        map.put("value2", querySystemValueString("邮箱"));
        map.put("value3", querySystemValueString("微信"));
        map.put("value4", querySystemValueString("QQ"));
        map.put("value5", querySystemValueString("域名"));
        return map;
    }



    /**
     * 查询用户类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryLoginUser"})
    public Map queryLoginUser(HttpServletRequest request) {
        Map map = new HashMap();
        map.put("user", queryMe(request));
        return map;
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
     * 查询所有已确认的提现信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryWithdrawalHistory"})
    public Map queryWithdrawalHistory() {
        Map map = new HashMap();
        RaiseRecord raiseRecord = new RaiseRecord();
        raiseRecord.setRstate("汇款成功");
        map.put("poundage", querySystemValueFloat("提现收取手续费用占比"));
        map.put("withdrawalHistory", raiseRecordService.selectMyWithdrawal(raiseRecord));
        return map;
    }

    /**
     * 查看客户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryUser2"})
    public Map queryUser2(@RequestBody Map theMap) {
        String rclient = (String) theMap.get("rclient");
        Map map = new HashMap();
        map.put("poundage", querySystemValueFloat("提现收取手续费用占比"));
        map.put("user", raiseUserService.QueryRaiseUserByUserid(rclient));
        return map;
    }

    /**
     * 查看普通客户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryUseUserList"})
    public Map queryUseUserList() {
        Map map = new HashMap();
        map.put("userList", raiseUserService.QueryRaiseUserListByIdentity("0"));
        return map;
    }

    /**
     * 查看代理客户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/queryAgencyUserList"})
    public Map queryAgencyUserList() {
        Map map = new HashMap();
        map.put("userList", raiseUserService.QueryRaiseUserListByIdentity("1"));
        return map;
    }

    /**
     * 查看后台用户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/querySystemUserList"})
    public Map querySystemUserList() {
        Map map = new HashMap();
        map.put("userList", raiseUserService.QueryRaiseUserListByIdentity("2"));
        return map;
    }





    /**
     * 修改系统信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/editSystem"})
    public Map editSystem(@RequestBody Map theMap) {
        String key = (String) theMap.get("key");
        String value = (String) theMap.get("value");
        Map map = new HashMap();
        RaiseSystem system = new RaiseSystem();
        system.setSysname(key);
        system.setSysvalue(value);
        raiseSystemService.updateRaiseSystemBySysname(system);
        return map;
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
     * 修改密码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/setPassword"})
    public Map setPassword(@RequestBody Map theMap,HttpServletRequest request) {
        String password1 = (String) theMap.get("password1");
        String password2 = (String) theMap.get("password2");
        RaiseUser raiseUser = queryMe(request);
        Map map = new HashMap();
        if(!raiseUser.getPassword().equals(password1)){
            map.put("error", "初始密码输入有误！");
            return map;
        }
        raiseUser.setPassword(password2);
        raiseUser.setSetNumber(raiseUser.getSetNumber()+1);
        raiseUserService.UpdateRaiseUserByUserid(raiseUser,0f,0,0);
        String key = Dict.RAISEUSER + raiseUser.getPhone();
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        map.put("error", null);
        return map;
    }

    /**
     * 文件图片上传
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Map<String,String> map) throws IOException {
        if(!file.isEmpty()){
            String fileName = file.getOriginalFilename();
            File update = new File(path);
            if (!update.exists()){
                update.mkdirs();
            }
            File dest = new File(path + fileName);
            file.transferTo(dest);
            RaiseSystem system = new RaiseSystem();
            system.setSysname(map.get("img"));
            system.setSysvalue("/opt/img/" + fileName);
            raiseSystemService.updateRaiseSystemBySysname(system);
        }
    }

    /**
     * 修改默认展示图片
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/showImg"})
    public Map showImg(@RequestBody Map theMap) {
        String thisImg = (String) theMap.get("thisImg");
        Map map = new HashMap();
        RaiseSystem system = new RaiseSystem();
        system.setSysname("默认付款码");
        system.setSysvalue(thisImg);
        raiseSystemService.updateRaiseSystemBySysname(system);
        return map;
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
     * 查看客户信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/uploadImg"})
    public Map uploadImg() {
        Map map = new HashMap();
        String system = querySystemValueString("默认付款码");
        map.put("imgUrl", querySystemValueString(system));
        map.put("imgUrl1", querySystemValueString("付款二维码1"));
        map.put("imgUrl2", querySystemValueString("付款二维码2"));
        map.put("imgUrl3", querySystemValueString("付款二维码3"));
        map.put("imgUrl4", querySystemValueString("付款二维码4"));
        map.put("imgUrl5", querySystemValueString("付款二维码5"));
        map.put("imgUrl6", querySystemValueString("付款二维码6"));
        map.put("thisImg2", system);
        return map;
    }


    /**
     * 新增工作人员
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/addAdmin"})
    public Map addAdmin(@RequestBody Map theMap) {
        String name = (String) theMap.get("name");
        String phone = (String) theMap.get("phone");
        String six = (String) theMap.get("six");
        String recommend = (String) theMap.get("recommend");
        Map map = new HashMap();
        if (phone.length() != 11 && phone.length() != 8 && !Utils.isChinaPhoneLegal(phone) && !Utils.isChinaPhoneLegal(phone)) {
            map.put("error","手机号码输入有误!");
            return map;
        }
        RaiseUser user = new RaiseUser();
        user.setName(name);
        user.setSex(six);
        user.setPhone(phone);
        user.setRecommend(recommend);
        user.setPassword("1234");
        user.setProvince("中国");
        user.setCity("中国");
        user.setUserid(getUUID());
        user.setIdentity(Dict.IDENTITY2);
        user.setSetNumber(0);
        int i = raiseUserService.AddRaiseUser(user);
        map.put("error", null);
        return map;
    }

    /**
     * 提现
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/withdrawalMoney"})
    public synchronized Map withdrawalMoney(@RequestBody Map theMap) {
        String rno = (String) theMap.get("rno");
        Map map = new HashMap();
        RaiseRecord raise = raiseRecordService.selectTopUpMoneyByRno(rno);
        if(raise.getSetdate()!=null){
            raise.setRstate("等待打款");
            map.put("error", "该记录已被办理,系统将为您刷新记录！");
            map.put("withdrawalDispose", raiseRecordService.selectMyWithdrawal(raise));
            return map;
        }
        raise.setRstate("汇款成功");
        raise.setSetdate(new Date());
        raiseRecordService.updateByPrimaryKey(raise);

        // 账户提现 begin
        RaiseUser user = raiseUserService.QueryRaiseUserByUserid(raise.getRclient());
        log.info("用户提现前余额：" + user.getSummoney());
        user.setSummoney(user.getSummoney() + raise.getRmonny());
        user.setSetNumber(user.getSetNumber() + 1);
        raiseUserService.UpdateRaiseUserByUserid(user,raise.getRmonny(),0,0);
        log.info("用户提现后余额：" + user.getSummoney());
        String key = Dict.RAISEUSER + user.getPhone();
        CacheFetchUtils.cleanRedisByKey(redisOperations,key);
        // 账户提现 end

        raise.setRstate("等待打款");
        map.put("withdrawalDispose", raiseRecordService.selectMyWithdrawal(raise));
        return map;
    }
}
