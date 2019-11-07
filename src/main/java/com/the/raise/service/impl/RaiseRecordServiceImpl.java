package com.the.raise.service.impl;

import com.the.raise.dao.RaiseRecordMapper;
import com.the.raise.model.RaiseRecord;
import com.the.raise.service.RaiseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseRecordService")
@Transactional
public class RaiseRecordServiceImpl implements RaiseRecordService {

    @Autowired
    private RaiseRecordMapper raiseRecordMapper;

    @Override
    public int insertRaiseRecord(RaiseRecord record) {
        return raiseRecordMapper.insertRaiseRecord(record);
    }

    @Override
    public List<RaiseRecord> selectMyRaiseRecord(RaiseRecord record) {
        return raiseRecordMapper.selectMyRaiseRecord(record);
    }

    @Override
    public List<RaiseRecord> selectMyRaiseAgency(RaiseRecord record) {
        return raiseRecordMapper.selectMyRaiseAgency(record);
    }

    @Override
    public List<RaiseRecord> selectTopUpMoneyList(RaiseRecord record) {
        return raiseRecordMapper.selectTopUpMoneyList(record);
    }

    @Override
    public RaiseRecord selectTopUpMoneyByRno(String rno) {
        return raiseRecordMapper.selectTopUpMoneyByRno(rno);
    }

    @Override
    public int updateByPrimaryKey(RaiseRecord record) {
        return raiseRecordMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<RaiseRecord> selectAgencyMoneyList(RaiseRecord record) {
        return raiseRecordMapper.selectAgencyMoneyList(record);
    }

    @Override
    public List<RaiseRecord> selectMyWithdrawal(RaiseRecord record) {
        return raiseRecordMapper.selectMyWithdrawal(record);
    }
}
