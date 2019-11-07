package com.the.raise.dao;

import com.the.raise.model.RaiseAgency;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RaiseAgencyMapper {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_agency
     *
     * @mbggenerated 新增代理信息
     */
    int insertRaiseAgency(RaiseAgency record);


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_agency
     *
     * @mbggenerated 查询本人代理记录
     */
    RaiseAgency selectRaiseAgency(String acustomer);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_agency
     *
     * @mbggenerated 查询所有的代理信息
     */
    List<RaiseAgency> selectRaiseAgencyList();


}