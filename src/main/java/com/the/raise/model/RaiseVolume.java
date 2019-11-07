package com.the.raise.model;

import lombok.Data;

import java.util.Date;
@Data
public class RaiseVolume {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vserial
     *
     * @mbggenerated 奖卷编号
     */
    private String vserial;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vpserial
     *
     * @mbggenerated 奖卷所属期号
     */
    private Integer vpserial;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vstate
     *
     * @mbggenerated 奖卷使用状态
     */
    private String vstate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vuserid
     *
     * @mbggenerated 奖卷所属用户
     */
    private String vuserid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vusetime
     *
     * @mbggenerated 奖卷使用
     */
    private Date vusetime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vconversiontime
     *
     * @mbggenerated 兑换时间
     */
    private Date vconversiontime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_volume.vfaluretime
     *
     * @mbggenerated 失效时间
     */
    private Date vfaluretime;

}