package com.the.raise.service.impl;

import com.the.raise.dao.RaiseVolumeMapper;
import com.the.raise.model.RaiseVolume;
import com.the.raise.service.RaiseVolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther: T-luot
 * @Date: 2019/10/21 17:43
 * @Description:
 */
@Service(value = "RaiseVolumeService")
@Transactional
public class RaiseVolumeServiceImpl implements RaiseVolumeService {

    @Autowired
    private RaiseVolumeMapper raiseVolumeMapper;

    @Override
    public int addRaiseVolume(RaiseVolume record) {
        return raiseVolumeMapper.addRaiseVolume(record);
    }

    @Override
    public RaiseVolume selectByVserial(String vserial) {
        return raiseVolumeMapper.selectByVserial(vserial);
    }

    @Override
    public List<RaiseVolume> useVserial(RaiseVolume record) {
        return raiseVolumeMapper.useVserial(record);
    }

    @Override
    public int updateVserial(RaiseVolume record) {
        return raiseVolumeMapper.updateVserial(record);
    }

    @Override
    public List<RaiseVolume> queryMyVserial(RaiseVolume record) {
        return raiseVolumeMapper.queryMyVserial(record);
    }

    @Override
    public int queryVserialNumber() {
        return raiseVolumeMapper.queryVserialNumber();
    }

    @Override
    public int queryBetVserialNumber(Integer vserial) {
        return raiseVolumeMapper.queryBetVserialNumber(vserial);
    }

    @Override
    public List<RaiseVolume> queryBetVserialList(RaiseVolume record) {
        return raiseVolumeMapper.queryBetVserialList(record);
    }
}
