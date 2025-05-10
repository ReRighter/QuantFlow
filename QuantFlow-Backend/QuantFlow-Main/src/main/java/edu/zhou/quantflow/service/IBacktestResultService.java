package edu.zhou.quantflow.service;

import edu.zhou.quantflow.dto.StrategyInfo;
import edu.zhou.quantflow.entity.BacktestResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 回测结果 服务类
 * </p>
 *
 * @author Zhouyue
 * @since 2025-04-14
 */
public interface IBacktestResultService extends IService<BacktestResult> {
    public List<BacktestResult> getReportList(Integer userId);
    public String getLog(Integer id);
    public List<StrategyInfo> getReadyStrategies(Integer userId);
}
