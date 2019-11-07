package com.the.raise.model;

import lombok.Data;

@Data
public class RaiseUser {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.userid
     *
     * @mbggenerated 客户号
     */
    private String userid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.name
     *
     * @mbggenerated 客户姓名
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.sex
     *
     * @mbggenerated 客户性别
     */
    private String sex;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.phone
     *
     * @mbggenerated 客户手机号
     */
    private String phone;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.card
     *
     * @mbggenerated 客户银行卡
     */
    private String card;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.sumMoney
     *
     * @mbggenerated 账户总金额
     */
    private Float summoney;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.province
     *
     * @mbggenerated 客户所在省
     */
    private String province;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.city
     *
     * @mbggenerated 客户所在市
     */
    private String city;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.recommend
     *
     * @mbggenerated 推荐人工号
     */
    private String recommend;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.goldNumber
     *
     * @mbggenerated 金币数量
     */
    private Integer goldnumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.prizeNumber
     *
     * @mbggenerated 奖卷数量
     */
    private Integer prizenumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.identity
     *
     * @mbggenerated 客户身份
     */
    private String identity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.password
     *
     * @mbggenerated 登陆密码
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column raise_user.password
     *
     * @mbggenerated 修改次数
     */
    private int setNumber;

}