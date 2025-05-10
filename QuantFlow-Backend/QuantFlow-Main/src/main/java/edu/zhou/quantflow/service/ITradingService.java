package edu.zhou.quantflow.service;


import edu.zhou.quantflow.dto.AccountStatus;
import edu.zhou.quantflow.dto.OrderResponse;
import edu.zhou.quantflow.dto.SimulationDetailsDto;
import edu.zhou.quantflow.entity.Orders;
import edu.zhou.quantflow.entity.Simulation;
import edu.zhou.quantflow.entity.SimulationReport;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 4/21/2025
 */
public interface ITradingService {
    public void checkAndProcessOrder(String frequency);
    public OrderResponse executeOrder(Orders order);
    public AccountStatus getAccountStatus(Integer simulationId);
    public Orders getOrderStatus(Integer orderId);
    public List<Simulation> getSimulations(Integer userId);
    public void executeSimulation(Simulation simulation);
    public void updateSimulationReport( SimulationReport report);
    public void updateLog(List<String> logs,Integer simId);
    public void stopSimulation(Integer simId);
    public SimulationDetailsDto getSimReport(Integer simId);
}
