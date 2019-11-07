package com.the.raise.service.impl;

import com.the.raise.dao.RaiseAgencyMapper;
import com.the.raise.model.RaiseAgency;
import com.the.raise.service.RaiseAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseAgencyService")
@Transactional
public class RaiseAgencyServiceImpl implements RaiseAgencyService {

    @Autowired
    private RaiseAgencyMapper raiseAgencyMapper;


    @Override
    public int insertRaiseAgency(RaiseAgency record) {
        return raiseAgencyMapper.insertRaiseAgency(record);
    }

    @Override
    public RaiseAgency selectRaiseAgency(String acustomer) {
        return raiseAgencyMapper.selectRaiseAgency(acustomer);
    }

    @Override
    public List<RaiseAgency> selectRaiseAgencyList() {
        return raiseAgencyMapper.selectRaiseAgencyList();
    }
}
