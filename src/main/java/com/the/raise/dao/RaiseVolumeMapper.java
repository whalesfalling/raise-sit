package com.the.raise.dao;

import com.the.raise.model.RaiseVolume;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RaiseVolumeMapper {

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