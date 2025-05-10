package edu.zhou.quantflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.zhou.quantflow.dto.StrategyInfo;
import edu.zhou.quantflow.entity.BacktestResult;
import edu.zhou.quantflow.mapper.BacktestResultMapper;
import edu.zhou.quantflow.service.IBacktestResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 回测结果 服务实现类
 * </p>
 *
 * @author Zhouyue
 * @since 2025-04-14
 */
@Service
public class BacktestResultServiceImpl extends ServiceImpl<BacktestResultMapper, BacktestResult> implements IBacktestResultService {
    @Override
    public List<BacktestResult> getReportList(Integer userId) {
       return list(new LambdaQueryWrapper<BacktestResult>().eq(BacktestResult::getUserId,userId));
    }

    @Override
    public String getLog(Integer id) {
        return getById(id).getTradingLog();
    }

    @Override
    public List<StrategyInfo> getReadyStrategies(Integer userId) {
        List<BacktestResult> results =  list(new LambdaQueryWrapper<BacktestResult>().eq(BacktestResult::getUserId,userId));
        return results.stream().map(result -> {
            return StrategyInfo.builder().type(result.getStrategyType()).name(result.getStrategyName()).id(result.getStrategyId()).build();
        }).toList();
    }
}
