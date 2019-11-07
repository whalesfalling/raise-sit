package com.the.raise.dao;

import com.the.raise.model.RaiseUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RaiseUserMapper {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_user
     *
     * @mbggenerated 用户注册
     */
    int AddRaiseUser(RaiseUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_user
     *
     * @mbggenerated [登陆] 根据手机号查询密码
     */
    RaiseUser QueryRaiseUserByPhone(String phone);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_user
     *
     * @mbggenerated 根据客户号查询用户
     */
    RaiseUser QueryRaiseUserByUserid(String userid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_user
     *
     * @mbggenerated 根据身份查询用户
     */
    List<RaiseUser> QueryRaiseUserListByIdentity(String identity);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_user
     *
     * @mbggenerated 用户投注扣款
     */
    int UpdateRaiseUserByUserid(RaiseUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table raise_user
     *
     * @mbggenerated 根据用户类型查询用户总数
     */
    int QueryRaiseUserByIdentity(String identity);

}