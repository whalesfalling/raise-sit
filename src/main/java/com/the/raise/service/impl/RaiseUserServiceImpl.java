package com.the.raise.service.impl;

import com.the.raise.config.CacheFetchUtils;
import com.the.raise.config.RedisOperations;
import com.the.raise.dao.RaiseUserMapper;
import com.the.raise.dict.Dict;
import com.the.raise.model.RaiseUser;
import com.the.raise.service.RaiseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseUserService")
@Transactional
public class RaiseUserServiceImpl implements RaiseUserService {

    @Autowired
    private RaiseUserMapper raiseUserMapper;
    @Autowired
    private RedisOperations redisOperations;

    @Override
    public int AddRaiseUser(RaiseUser record) {
        record.setSummoney(0.00f);          // 初始化总余额
        record.setGoldnumber(0);            // 初始化金币数量
        record.setPrizenumber(0);           // 初始化奖卷数量
        if(record.getIdentity()==null){
            record.setIdentity(Dict.IDENTITY0); // 初始化用户身份 0 - 普通用户
        }
        return raiseUserMapper.AddRaiseUser(record);
    }

    @Override
    public RaiseUser QueryRaiseUserByPhone(String phone) {
        RaiseUser raiseUser = CacheFetchUtils.fromRedis(redisOperations, Dict.RAISEUSER + phone,RaiseUser.class,()->getRaiseUser(phone));
        return raiseUser;
    }

    @Override
    public RaiseUser getRaiseUser(String phone){
        return raiseUserMapper.QueryRaiseUserByPhone(phone);
    }

    @Override
    public RaiseUser QueryRaiseUserByUserid(String userid) {
        return raiseUserMapper.QueryRaiseUserByUserid(userid);
    }

    @Override
    public synchronized int UpdateRaiseUserByUserid(RaiseUser record, Float money, Integer gold, Integer prize) {
        RaiseUser user = raiseUserMapper.QueryRaiseUserByUserid(record.getUserid());
        if(user.getSetNumber() != record.getSetNumber()-1){
            record.setSummoney(user.getSummoney() + money);
            record.setGoldnumber(user.getGoldnumber() + gold);
            record.setPrizenumber(user.getPrizenumber() + prize);
            record.setSetNumber(user.getSetNumber() + 1);
        }
        return raiseUserMapper.UpdateRaiseUserByUserid(record);
    }

    @Override
    public int QueryRaiseUserByIdentity(String identity) {
        return raiseUserMapper.QueryRaiseUserByIdentity(identity);
    }

    @Override
    public List<RaiseUser> QueryRaiseUserListByIdentity(String identity) {
        return raiseUserMapper.QueryRaiseUserListByIdentity(identity);
    }
}
