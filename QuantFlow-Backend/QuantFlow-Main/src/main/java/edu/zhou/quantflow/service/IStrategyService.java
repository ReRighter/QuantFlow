package edu.zhou.quantflow.service;


import edu.zhou.quantflow.entity.CustomizedStrategy;
import edu.zhou.quantflow.entity.StoredStrategy;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 3/31/2025
 */
public interface IStrategyService {
    public List<StoredStrategy> getStoredStrategy(int id);
    public List<CustomizedStrategy> getCustomizedStrategy(int id);
    public List<CustomizedStrategy> getCustomizedStrategyByUserId(int userId);
    public void saveCustomizedStrategy(CustomizedStrategy customizedStrategy);
    public void deleteCustomizedStrategy(int id);
}
