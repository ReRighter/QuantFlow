package edu.zhou.quantflow.controller;


import edu.zhou.quantflow.dto.AccountStatus;
import edu.zhou.quantflow.dto.OrderResponse;
import edu.zhou.quantflow.dto.ReportDto;
import edu.zhou.quantflow.dto.SimulationDetailsDto;
import edu.zhou.quantflow.entity.Orders;
import edu.zhou.quantflow.entity.Simulation;
import edu.zhou.quantflow.entity.SimulationReport;
import edu.zhou.quantflow.service.ITradingService;
import edu.zhou.quantflow.util.ResourceUrlUtil;
import edu.zhou.quantflow.util.Result;
import org.apache.ibatis.javassist.Loader;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 4/15/2025
 */
@RestController
@RequestMapping("/trading")
public class TradingController {
    private final ITradingService tradingService;
    private final ResourceUrlUtil resourceUrlUtil;

    @Autowired
    public TradingController(ITradingService tradingService, ResourceUrlUtil resourceUrlUtil){
        this.tradingService = tradingService;
        this.resourceUrlUtil = resourceUrlUtil;
    }

    @PostMapping("/place_order")
    public Result<OrderResponse> placeOrder(@RequestBody Orders order) {
        OrderResponse response =   tradingService.executeOrder(order);
        assert response.getOrderId()!=null;
        assert response.getStatus()!=null;
        return Result.success(response);
    }

    @GetMapping("/{orderId}/status")
    public Result<Orders> getOrderStatus(@PathVariable("orderId") Integer orderId){
        Orders orders =  tradingService.getOrderStatus(orderId);
        return Result.success(orders);
    }

    @GetMapping("/sync_status/{simulation_id}")
    public Result<AccountStatus> syncAccountStatus(@PathVariable("simulation_id") Integer id){
        return Result.success(tradingService.getAccountStatus(id));
    }

    @GetMapping("/get_simulations/{userId}")
    public Result<List<Simulation>> getSimulations(@PathVariable("userId") Integer id){
        return Result.success(tradingService.getSimulations(id));
    }
    @PostMapping("/simulation")
    public Result<?> executeSimulation(@RequestBody Simulation simulation){
        tradingService.executeSimulation(simulation);
        return Result.success();
    }
    @PostMapping("/stop_simulation/{sim_id}")
    public Result<Void> stopSimulation(@PathVariable("sim_id") Integer simId){
        tradingService.stopSimulation(simId);
        return Result.success();
    }

    @PostMapping("/update_stats")
    public Result<?> updateSimulationReport( @RequestBody ReportDto report){
        tradingService.updateSimulationReport(SimulationReport.builder().simulationId(report.getSimulationId())
                .value(report.getValue()).available(report.getAvailable()).earningsRate(report.getEarningsRate())
                .annualReturns(report.getAnnualReturns()).sharpeRatio(report.getSharpeRatio()).maxDrawdown(report.getMaxDrawdown()).build());
        tradingService.updateLog(report.getLog(),report.getSimulationId());
        return Result.success();
    }

    @GetMapping("/get_report/{sim_id}")
    public Result<SimulationDetailsDto> getSimReport(@PathVariable("sim_id") Integer simId){
        return Result.success(tradingService.getSimReport(simId));
    }

    @PostMapping("/apitest")
    public Result<String> apiTest(){
        return Result.success("API test successful");
    }
}
