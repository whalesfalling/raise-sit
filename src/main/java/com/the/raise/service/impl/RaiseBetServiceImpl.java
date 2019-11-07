package com.the.raise.service.impl;

import com.the.raise.dao.RaiseBetMapper;
import com.the.raise.model.RaiseBet;
import com.the.raise.service.RaiseBetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseBetService")
@Transactional
public class RaiseBetServiceImpl implements RaiseBetService {

    @Autowired
    private RaiseBetMapper raiseBetMapper;

    @Override
    public int insertBet(RaiseBet record) {
        return raiseBetMapper.insertBet(record);
    }

    @Override
    public int queryThisBetNumber(Map thisValue) {
        return raiseBetMapper.queryThisBetNumber(thisValue);
    }

    @Override
    public List<RaiseBet> queryMyThisRaiseBetList(RaiseBet record) {
        return raiseBetMapper.queryMyThisRaiseBetList(record);
    }

    @Override
    public List<RaiseBet> queryMyThisRaiseBetListByBperiodsid(RaiseBet record) {
        return raiseBetMapper.queryMyThisRaiseBetListByBperiodsid(record);
    }
}
