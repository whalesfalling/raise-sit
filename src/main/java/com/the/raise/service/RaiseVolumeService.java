package com.the.raise.service;

import com.the.raise.model.RaiseVolume;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
public interface RaiseVolumeService {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 新增奖卷
     */
    int addRaiseVolume(RaiseVolume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 根据奖卷编号查询奖卷
     */
    RaiseVolume selectByVserial(String vserial);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 奖卷使用，每次只使用第一条
     */
    List<RaiseVolume> useVserial(RaiseVolume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 修改奖卷状态
     */
    int updateVserial(RaiseVolume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 查询我的所有奖卷
     */
    List<RaiseVolume> queryMyVserial(RaiseVolume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 查询兑换奖卷总数
     */
    int queryVserialNumber();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 本期投注奖卷总数
     */
    int queryBetVserialNumber(Integer vserial);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_volume
     *
     * @mbggenerated 本期投注奖卷集合
     */
    List<RaiseVolume> queryBetVserialList(RaiseVolume record);

}
