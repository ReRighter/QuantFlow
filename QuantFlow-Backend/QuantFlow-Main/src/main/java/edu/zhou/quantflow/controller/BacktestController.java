package edu.zhou.quantflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.zhou.quantflow.dto.StrategyInfo;
import edu.zhou.quantflow.entity.BacktestResult;
import edu.zhou.quantflow.service.IBacktestResultService;
import edu.zhou.quantflow.util.ResourceUrlUtil;
import edu.zhou.quantflow.util.Result;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 回测结果 前端控制器
 * </p>
 *
 * @author Zhouyue
 * @since 2025-04-14
 */
@RestController
@RequestMapping("/backtest")
public class BacktestController {
    private final IBacktestResultService backtestResultService;
    private final ResourceUrlUtil resourceUrlUtil;

    @Autowired
    public BacktestController(IBacktestResultService backtestResultService, ResourceUrlUtil resourceUrlUtil){
        this.backtestResultService = backtestResultService;
        this.resourceUrlUtil = resourceUrlUtil;
    }

    @PostMapping("/savereport")
    public Result<String> saveReport(@RequestBody BacktestResult result){
        backtestResultService.save(result);
        return Result.success("保存成功");
    }

    @GetMapping("/listreport/{id}")
    public ResponseEntity<String> listReport(@PathVariable("id")Integer id) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        //将驼峰格式变量转换为前端所需要的蛇形变量
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule()); // 注册 JavaTimeModule
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳格式
        List<BacktestResult> results = backtestResultService.getReportList(id);
        String data  =  objectMapper.writeValueAsString(Result.success(results));
        return ResponseEntity.ok(data);
    }
    @GetMapping ("/getlog/{resultId}")
    Result<String> getLog(@PathVariable("resultId")Integer id){
        return Result.success(backtestResultService.getLog(id));
    }
    @GetMapping("/getreadystrategies/{userId}")
    Result<List<StrategyInfo>> getReadyStrategies(@PathVariable("userId")Integer userId){
        return Result.success(backtestResultService.getReadyStrategies(userId));
    }
}
