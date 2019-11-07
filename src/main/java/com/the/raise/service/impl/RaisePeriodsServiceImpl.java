package com.the.raise.service.impl;

import com.the.raise.dao.RaisePeriodsMapper;
import com.the.raise.model.RaisePeriods;
import com.the.raise.service.RaisePeriodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaisePeriodsService")
@Transactional
public class RaisePeriodsServiceImpl implements RaisePeriodsService {

    @Autowired
    private RaisePeriodsMapper raisePeriodsMapper;

    @Override
    public List<RaisePeriods> selectRaisePeriodsList() {
        return raisePeriodsMapper.selectRaisePeriodsList();
    }

    @Override
    public int updateByPrimaryKey(RaisePeriods record) {
        return raisePeriodsMapper.updateByPrimaryKey(record);
    }

    @Override
    public int QueryRaisePeriodsNumber() {
        return raisePeriodsMapper.QueryRaisePeriodsNumber();
    }

    @Override
    public int AddRaisePeriods(RaisePeriods record) {
        return raisePeriodsMapper.AddRaisePeriods(record);
    }
}
