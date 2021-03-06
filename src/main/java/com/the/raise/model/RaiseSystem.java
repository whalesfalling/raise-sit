package com.the.raise.model;

import lombok.Data;

@Data
public class RaiseSystem {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_system.sysid
     *
     * @mbggenerated 系统设置编号
     */
    private Integer sysid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_system.sysname
     *
     * @mbggenerated 系统设置名称
     */
    private String sysname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_system.sysvalue
     *
     * @mbggenerated 系统设置值
     */
    private String sysvalue;
}