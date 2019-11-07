package com.the.raise.service;

import com.the.raise.model.RaiseSystem;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
public interface RaiseSystemService {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_system
     *
     * @mbggenerated 根据系统设置名查询系统设置值
     */
    String selectRaiseSystemBySysname(String sysname);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_system
     *
     * @mbggenerated 根据系统设置名更改系统设置值
     */
    int updateRaiseSystemBySysname(RaiseSystem record);

}