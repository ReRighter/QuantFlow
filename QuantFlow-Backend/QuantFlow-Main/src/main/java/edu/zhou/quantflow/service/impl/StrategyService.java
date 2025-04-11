package edu.zhou.quantflow.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.zhou.quantflow.entity.CustomizedStrategy;
import edu.zhou.quantflow.entity.StoredStrategy;
import edu.zhou.quantflow.mapper.CustomizedStrategyMapper;
import edu.zhou.quantflow.mapper.StoredStrategyMapper;
import edu.zhou.quantflow.service.IStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 3/31/2025
 */
@Service
public class StrategyService implements IStrategyService {
    private final StoredStrategyMapper storedStrategyMapper;
    private final CustomizedStrategyMapper customizedStrategyMapper;

    @Autowired
    public StrategyService(StoredStrategyMapper storedStrategyMapper, CustomizedStrategyMapper customizedStrategyMapper){
        this.storedStrategyMapper = storedStrategyMapper;
        this.customizedStrategyMapper = customizedStrategyMapper;
    }
    @Override
    public List<StoredStrategy> getStoredStrategy(int id) {
        if(id == -1){
            return storedStrategyMapper.selectList(null);
        } else {
            return storedStrategyMapper.selectList(new LambdaQueryWrapper<StoredStrategy>().eq(StoredStrategy::getId,id));
        }
    }
    @Override
    public List<CustomizedStrategy> getCustomizedStrategy(int id){
        if(id == -1){
            return customizedStrategyMapper.selectList(null);
        } else {
            return customizedStrategyMapper.selectList(new LambdaQueryWrapper<CustomizedStrategy>().eq(CustomizedStrategy::getId,id));
        }
    }

    @Override
    public List<CustomizedStrategy> getCustomizedStrategyByUserId(int userId) {
        return customizedStrategyMapper.selectList(new LambdaQueryWrapper<CustomizedStrategy>().eq(CustomizedStrategy::getUserId,userId));
    }

    @Override
    public void saveCustomizedStrategy(CustomizedStrategy customizedStrategy) {
        customizedStrategyMapper.insert(customizedStrategy);
    }

    @Override
    public void deleteCustomizedStrategy(int id) {
        customizedStrategyMapper.deleteById(id);
    }
}
