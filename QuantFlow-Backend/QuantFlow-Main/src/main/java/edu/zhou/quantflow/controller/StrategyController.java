package edu.zhou.quantflow.controller;


import edu.zhou.quantflow.entity.CustomizedStrategy;
import edu.zhou.quantflow.entity.StoredStrategy;
import edu.zhou.quantflow.service.IStrategyService;
import edu.zhou.quantflow.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 3/31/2025
 */
@RestController
@RequestMapping("/strategy")
public class StrategyController {
    private final IStrategyService strategyService;
    @Autowired
    public StrategyController(IStrategyService strategyService){this.strategyService =strategyService;}

    @GetMapping("/getStoredStrategies")
    public Result<List<StoredStrategy>> getStoredStrategies() {
        return Result.success(strategyService.getStoredStrategy(-1));
    }

    @GetMapping("/getCustomStrategies/{userId}")
    public  Result<List<CustomizedStrategy>> getCustomStrategies(@PathVariable("userId") int userId){
        var res= strategyService.getCustomizedStrategyByUserId(userId);
        return Result.success(res);
    }

    @PostMapping("/saveAs/{userId}")
    public Result<String> saveCustomizedStrategy(@RequestBody CustomizedStrategy customizedStrategy, @PathVariable("userId") int userId) {
        customizedStrategy.setUserId(userId);
        customizedStrategy.setId(null);
        strategyService.saveCustomizedStrategy(customizedStrategy);
        return Result.success("保存成功");
    }
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteCustomizedStrategy(@PathVariable("id") int id) {
        strategyService.deleteCustomizedStrategy(id);
        return Result.success("删除成功");
    }

}
