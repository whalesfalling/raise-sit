package com.the.raise.service;

import com.the.raise.model.RaisePeriods;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
public interface RaisePeriodsService {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_periods
     *
     * @mbggenerated 查询期数信息
     */
    List<RaisePeriods> selectRaisePeriodsList();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_periods
     *
     * @mbggenerated 用户下注更新总金额，投注总数
     */
    int updateByPrimaryKey(RaisePeriods record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_periods
     *
     * @mbggenerated 查询众筹总期数
     */
    int QueryRaisePeriodsNumber();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_periods
     *
     * @mbggenerated 新开期数
     */
    int AddRaisePeriods(RaisePeriods record);

}
