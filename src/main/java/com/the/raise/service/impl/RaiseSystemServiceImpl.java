package com.the.raise.service.impl;

import com.the.raise.dao.RaiseSystemMapper;
import com.the.raise.model.RaiseSystem;
import com.the.raise.service.RaiseSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseSystemService")
@Transactional
public class RaiseSystemServiceImpl implements RaiseSystemService {

    @Autowired
    private RaiseSystemMapper raiseSystemMapper;


    @Override
    public String selectRaiseSystemBySysname(String sysname) {
        return raiseSystemMapper.selectRaiseSystemBySysname(sysname);
    }

    @Override
    public int updateRaiseSystemBySysname(RaiseSystem record) {
        return raiseSystemMapper.updateRaiseSystemBySysname(record);
    }
}
