package com.the.raise.service;

import com.the.raise.model.RaiseBet;
import com.the.raise.model.RaiseWin;

import java.util.List;
import java.util.Map;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
public interface RaiseBetService {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_bet
     *
     * @mbggenerated 用户投注
     */
    int insertBet(RaiseBet record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_bet
     *
     * @mbggenerated 当前期投注编号
     */
    int queryThisBetNumber(Map thisValue);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_bet
     *
     * @mbggenerated 根据期数和客户号查询投注信息
     */
    List<RaiseBet> queryMyThisRaiseBetList(RaiseBet record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_bet
     *
     * @mbggenerated 根据期数查询投注信息
     */
    List<RaiseBet> queryMyThisRaiseBetListByBperiodsid(RaiseBet record);
}