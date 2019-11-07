package com.the.raise.service.impl;

import com.the.raise.dao.RaiseWinMapper;
import com.the.raise.model.RaiseWin;
import com.the.raise.service.RaiseWinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseWinService")
@Transactional
public class RaiseWinServiceImpl implements RaiseWinService {

    @Autowired
    private RaiseWinMapper raiseWinMapper;


    @Override
    public List<RaiseWin> selectRaiseWinList(Integer wperiodsid) {
        return raiseWinMapper.selectRaiseWinList(wperiodsid);
    }

    @Override
    public int insertRaiseWin(RaiseWin record) {
        return raiseWinMapper.insertRaiseWin(record);
    }
}
