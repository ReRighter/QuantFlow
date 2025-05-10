package edu.zhou.quantflow.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import edu.zhou.quantflow.dto.AccountStatus;
import edu.zhou.quantflow.dto.OrderResponse;
import edu.zhou.quantflow.dto.SimulationDetailsDto;
import edu.zhou.quantflow.dto.SimulationParams;
import edu.zhou.quantflow.entity.*;
import edu.zhou.quantflow.mapper.*;
import edu.zhou.quantflow.service.ITradingService;
import edu.zhou.quantflow.util.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @Author Righter
 * @Description
 * @Date since 4/21/2025
 */
@Service
@Log4j2
public class TradingService implements ITradingService {
    private final BalancesMapper balancesMapper;
    private final PositionsMapper positionsMapper;
    private final OrdersMapper ordersMapper;
    private final TransactionTemplate transactionTemplate;
    private final RestTemplate restTemplate;
    private final SimulationMapper simulationMapper;
    private final SimulationReportMapper simulationReportMapper;
    private final StrategyService strategyService;
    private final SimulationLogMapper simulationLogMapper;

    @Value("${trading-service.url}")
    String tradingServiceUrl;

    @Autowired
    public TradingService(BalancesMapper balancesMapper,PositionsMapper positionsMapper
            ,OrdersMapper ordersMapper,SimulationMapper simulationMapper,SimulationReportMapper simulationReportMapper,
                          TransactionTemplate transactionTemplate,RestTemplate restTemplate,
                          StrategyService strategyService,SimulationLogMapper simulationLogMapper) {
        this.balancesMapper = balancesMapper;
        this.ordersMapper = ordersMapper;
        this.positionsMapper= positionsMapper;
        this.simulationMapper = simulationMapper;
        this.simulationReportMapper = simulationReportMapper;
        this.transactionTemplate = transactionTemplate;
        this.restTemplate = restTemplate;
        this.strategyService = strategyService;
        this.simulationLogMapper = simulationLogMapper;
    }

    @Override
    public AccountStatus getAccountStatus(Integer simulationId) {

        Balances balances = balancesMapper.selectOne(new LambdaQueryWrapper<Balances>()
                .eq(Balances::getSimulationId,simulationId));
        if(balances == null) throw new RuntimeException("当前用户未创建资金账户!");
        Positions positions = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>()
                .eq(Positions::getSimulationId,simulationId)
        );
        return AccountStatus.builder().balances(balances).positions(positions).build();
    }
    @Override
    public Orders getOrderStatus(Integer orderId){
         Orders order =  ordersMapper.selectOne(new LambdaQueryWrapper<Orders>().eq(Orders::getOrderId,orderId));
         if(order==null) throw new RuntimeException("订单不存在!");
         else return order;
    }

    @Override
    public List<Simulation> getSimulations(Integer userId) {
        return simulationMapper.selectList(new LambdaQueryWrapper<Simulation>().eq(Simulation::getUserId,userId));
    }

    @Override
    public void executeSimulation(Simulation simulation) {
        simulation.setStatus("running");
        //创建资金账户和持仓账户
        Balances balances =  Balances.builder().simulationId(simulation.getId())
                .available(simulation.getInitialFunding())
                .total(simulation.getInitialFunding()).updatedAt(LocalDateTime.now()).build();
        Positions positions = Positions.builder().simulationId(simulation.getId())
                .stockCode(simulation.getStockCode())
                .updatedAt(LocalDateTime.now()).build();

        //获取策略代码
        String code = strategyService.getStrategyCode(simulation.getStrategyType(),simulation.getStrategyId());



        //更新数据库
        transactionTemplate.execute(status -> {
            try {
                simulationMapper.insert(simulation);
                //插入simulation表后可得到id
                balances.setSimulationId(simulation.getId());
                positions.setSimulationId(simulation.getId());
                balancesMapper.insert(balances);
                positionsMapper.insert(positions);
                //远程调用策略引擎开始模拟
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                SimulationParams params = SimulationParams.builder().simulationId(simulation.getId())
                        .strategyCode(code).stockCode(simulation.getStockCode()).frequency(simulation.getFrequency()).simulationId(simulation.getId())
                        .build();
                HttpEntity<SimulationParams> req = new HttpEntity<>(params,headers);

                Result<Void> result=restTemplate.exchange(tradingServiceUrl + "/start_simulation", HttpMethod.POST, req, new ParameterizedTypeReference<Result<Void>>() {}).getBody();
                assert result != null;
                if( result.getCode()== null || result.getCode()!=200){
                    throw new RuntimeException("远程调用策略引擎错误:"+result.getMessage());
                }

                return null;
            }catch (Exception e){
                status.setRollbackOnly();
                throw e;
            }
        });



    }

    @Override
    public void updateSimulationReport(SimulationReport report){
        SimulationReport r = simulationReportMapper.selectOne(new LambdaQueryWrapper<SimulationReport>().eq(SimulationReport::getSimulationId,report.getSimulationId()));
        balancesMapper.update(new LambdaUpdateWrapper<Balances>().set(Balances::getAvailable,report.getAvailable()).eq(Balances::getSimulationId,report.getSimulationId()));
        report.setUpdateAt(LocalDateTime.now());
        if(r == null) {
            simulationReportMapper.insert(report);
        }else {
            report.setId(r.getId());
            simulationReportMapper.updateById(report);
        }
    }

    @Override
    public OrderResponse executeOrder(Orders order) {
        //市价单处理
        if (order.getOrderType().equals("market")) {
            //买入
            if (order.getSide().equals("buy")) {
                BigDecimal cost = order.getPrice().multiply(order.getQuantity());
                Balances balances = balancesMapper.selectOne(new LambdaQueryWrapper<Balances>().eq(Balances::getSimulationId, order.getSimulationId()));
                if (balances == null) throw new RuntimeException("用户资金账户不存在!");
                if (cost.compareTo(balances.getAvailable()) > 0) throw new RuntimeException("资金不足!");

                //查询是否已有持仓
                Positions positions = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>()
                        .eq(Positions::getSimulationId, order.getSimulationId())
                        .eq(Positions::getStockCode, order.getStockCode()));
                //新建持仓
                if (positions == null) {
                    positions = new Positions();
                    positions.setSimulationId(order.getSimulationId());
                    positions.setCostPrice(order.getPrice());//成本价
                    positions.setStockCode(order.getStockCode());
                    positions.setQuantity(order.getQuantity());
                    //positions.setMarketValue(cost);//购买的瞬间,市值等于成本
                    positions.setFloatingProfit(new BigDecimal(0));//盈亏为0
                } else {//加仓
                    BigDecimal totalQuantity = positions.getQuantity().add(order.getQuantity());
                    BigDecimal totalCost = positions.getQuantity().multiply(positions.getCostPrice()).add(cost);
                    //新的平均成本
                    positions.setCostPrice(totalCost.divide(totalQuantity, RoundingMode.HALF_UP));
                    positions.setQuantity(totalQuantity);
                    //positions.setMarketValue();
                }

                //更新资金账户

                balances.setAvailable(balances.getAvailable().subtract(cost));
                balances.setTotal(balances.getAvailable()); //总金额等于余额加冻结资金;市价单不冻结资金

                order.setStatus("filled");

                Positions finalPositions = positions;
                transactionTemplate.execute((status) -> {
                    try {
                        ordersMapper.insert(order);
                        positionsMapper.insertOrUpdate(finalPositions);
                        balancesMapper.updateById(balances);
                        return null;
                    } catch (Exception e) {
                        status.setRollbackOnly();//标记事务回滚
                        throw e;
                    }
                });

                return OrderResponse.builder().orderId(order.getOrderId()).status(order.getStatus()).build();

            }
            //卖出
            else if (order.getSide().equals("sell")) {
                Positions positions = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>()
                        .eq(Positions::getSimulationId, order.getSimulationId())
                        .eq(Positions::getStockCode, order.getStockCode()));
                if (positions == null) throw new RuntimeException("用户持仓不存在!");
                if (positions.getQuantity().compareTo(order.getQuantity()) < 0)
                    throw new RuntimeException("卖出数量大于持仓数量!");
                BigDecimal value = order.getPrice().multiply(order.getQuantity());
                Balances balances = balancesMapper.selectOne(new LambdaQueryWrapper<Balances>().eq(Balances::getSimulationId, order.getSimulationId()));
                if (balances == null) throw new RuntimeException("用户资金账户不存在!");
                //更新资金账户
                balances.setAvailable(balances.getAvailable().add(value));
                balances.setTotal(balances.getAvailable());

                //更新持仓
                positions.setQuantity(positions.getQuantity().subtract(order.getQuantity()));
                //更新订单
                order.setStatus("filled");

                transactionTemplate.execute(status -> {
                    try {
                        balancesMapper.updateById(balances);
                        ordersMapper.insert(order);
                        positionsMapper.updateById(positions);
                        return null;
                    } catch (Exception e) {
                        status.setRollbackOnly();//标记事务回滚
                        throw e;
                    }
                });

                return OrderResponse.builder().orderId(order.getOrderId()).status(order.getStatus()).build();

            } else throw new RuntimeException("订单错误!未指定买卖方向");
        } else if (order.getOrderType().equals("limit")) {//限价单处理
            if (order.getSide().equals("buy")) {
                Balances balances = balancesMapper.selectOne(new LambdaQueryWrapper<Balances>().eq(Balances::getSimulationId, order.getSimulationId()));
                BigDecimal cost = order.getPrice().multiply(order.getQuantity());
                if (balances == null) throw new RuntimeException("用户资金账户不存在!");
                if (cost.compareTo(balances.getAvailable()) > 0) throw new RuntimeException("资金不足!");
                //减少可用资金,添加冻结资金
                balances.setAvailable(balances.getAvailable().subtract(cost));
                balances.setFrozen(balances.getFrozen().add(cost));
                //修改订单为pending存入数据库,等待轮询
                order.setStatus("pending");
                ordersMapper.insert(order);

                return OrderResponse.builder().orderId(order.getOrderId()).status(order.getStatus()).build();
            } else {//卖出
                Positions existingPositions = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>()
                        .eq(Positions::getSimulationId, order.getSimulationId())
                        .eq(Positions::getStockCode, order.getStockCode())
                );
                //不存在持仓/ 持仓不足, 订单失败
                if (existingPositions == null || existingPositions.getQuantity().compareTo(order.getQuantity()) < 0) {
                    //order.setStatus("cancelled");
                    //ordersMapper.insert(order);
                    throw new RuntimeException("持仓不足,创建订单失败");
                }
                order.setStatus("pending");
                ordersMapper.insert(order);
                return OrderResponse.builder().orderId(order.getOrderId()).status(order.getStatus()).build();
            }
        }else throw new RuntimeException("订单类型错误:"+order.getOrderType());

    }
    @Override
    public void checkAndProcessOrder(String frequency){
        List<Orders> pendingOrders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getStatus,"pending")
                .eq(Orders::getOrderType,"limit")
                .eq(Orders::getFrequency,frequency)
        );
        if(pendingOrders.isEmpty()) return;
        List<String> stock_codes = pendingOrders.stream().map(Orders::getStockCode).toList();
        String stocks = String.join(",", stock_codes);
        HttpEntity<String> req = new HttpEntity<>(stocks);
        //批量获取实时价格
        List<BigDecimal> price = restTemplate.exchange(tradingServiceUrl+"/realtime_quote", HttpMethod.POST, req,new ParameterizedTypeReference<List<BigDecimal>>(){}).getBody();
        assert price != null;
        if(pendingOrders.size()!= price.size()) throw new RuntimeException("订单数量与股票数量不匹配!");
        //遍历订单判断,满足要求则执行
        for (int i = 0; i < pendingOrders.size(); i++) {
            boolean isSell = pendingOrders.get(i).getSide().equals("sell");
            if(isSell && isLimitOrderMet(true,price.get(i),pendingOrders.get(i).getPrice())){
                executeSellLimit(pendingOrders.get(i));
            }else if(!isSell && isLimitOrderMet(false,price.get(i),pendingOrders.get(i).getPrice())){
                executeBuyLimit(pendingOrders.get(i));
            }
        }

    }
    public boolean isLimitOrderMet(boolean isSell, BigDecimal price, BigDecimal limitPrice){
        if(isSell) return price.compareTo(limitPrice) >= 0;
        else return price.compareTo(limitPrice) <= 0;

    }

    public void executeBuyLimit(Orders order){
        Balances balances = balancesMapper.selectOne(new LambdaQueryWrapper<Balances>().eq(Balances::getSimulationId,order.getSimulationId()));
        Positions existingPosition = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>()
                .eq(Positions::getSimulationId,order.getSimulationId())
                .eq(Positions::getStockCode,order.getStockCode())
        );
        boolean isExist = existingPosition == null;
        Positions position = isExist ? new Positions():existingPosition;
        //买入成本
        BigDecimal cost = order.getPrice().multiply(order.getQuantity());

        if(!isExist){
            position.setSimulationId(order.getSimulationId());
            position.setStockCode(order.getStockCode());
            position.setQuantity(order.getQuantity());
            position.setCostPrice(order.getPrice());


        }else { //加仓
            //所有持仓总成本
            BigDecimal totalCost = cost.add(position.getCostPrice().multiply(position.getQuantity()));
            //新的平均成本
            BigDecimal totalQuantity = position.getQuantity().add(order.getQuantity());
            BigDecimal newCostPrice = totalCost.divide(totalQuantity, RoundingMode.HALF_UP);
            position.setCostPrice(newCostPrice);
            position.setQuantity(position.getQuantity().add(order.getQuantity()));
            position.setUpdatedAt(LocalDateTime.now());
        }
        //解冻买入部分的资金,更新总金额
        balances.setFrozen(balances.getFrozen().subtract(cost));
        balances.setTotal(balances.getFrozen().add(balances.getAvailable()));
        order.setStatus("filled");
        transactionTemplate.execute(status -> {
            try {
                balancesMapper.updateById(balances);
                ordersMapper.updateById(order);
                positionsMapper.insertOrUpdate(position);
                return null;
            }catch (Exception e){
                status.setRollbackOnly();
                throw e;
            }

        });
    }

    public void executeSellLimit(Orders orders){
        Positions exsitingPositions = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>()
                .eq(Positions::getSimulationId,orders.getSimulationId())
                .eq(Positions::getStockCode,orders.getStockCode())
        );
        //没有持仓/持仓不足 无法卖出,订单失败
        if(exsitingPositions == null || (exsitingPositions.getQuantity().compareTo(orders.getQuantity()) < 0) ) {
            orders.setStatus("cancelled");
            ordersMapper.updateById(orders);
            return;
        }
        Balances balances = balancesMapper.selectOne(new LambdaQueryWrapper<Balances>().eq(Balances::getSimulationId,orders.getSimulationId()));
        BigDecimal value = orders.getPrice().multiply(orders.getQuantity());
        //更新持仓和资金
        exsitingPositions.setQuantity(exsitingPositions.getQuantity().subtract(orders.getQuantity()));
        balances.setAvailable(balances.getAvailable().add(value));
        balances.setTotal(balances.getFrozen().add(balances.getTotal()));

        exsitingPositions.setUpdatedAt(LocalDateTime.now());
        balances.setUpdatedAt(LocalDateTime.now());
        orders.setStatus("filled");
        orders.setUpdatedAt(LocalDateTime.now());
        transactionTemplate.execute(status -> {
            try {
                ordersMapper.updateById(orders);
                balancesMapper.updateById(balances);
                positionsMapper.updateById(exsitingPositions);
                return null;
            }catch (Exception e){
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    @Override
    public void updateLog(List<String> logs,Integer simId) {
        List<SimulationLog> simLogs = logs.stream().map(l->{
              return  SimulationLog.builder().log(l).simulationId(simId).build();
        }).toList();
        simulationLogMapper.insert(simLogs);
    }

    @Override
    public void stopSimulation(Integer sim_id) {
        String result=restTemplate.exchange(tradingServiceUrl+"/stop_simulation/{sim_id}",HttpMethod.POST,null, String.class,sim_id).getBody();
        assert result != null;
        if(!result.contains("ok")) throw new RuntimeException("停止策略运行失败!");
    }

    @Override
    public SimulationDetailsDto getSimReport(Integer simId) {
        SimulationReport report = simulationReportMapper.selectOne(new LambdaQueryWrapper<SimulationReport>().eq(SimulationReport::getSimulationId,simId));
        List<SimulationLog> log = simulationLogMapper.selectList(new LambdaQueryWrapper<SimulationLog>().eq(SimulationLog::getSimulationId,simId));
        Positions positions = positionsMapper.selectOne(new LambdaQueryWrapper<Positions>().eq(Positions::getSimulationId,simId));
        return  SimulationDetailsDto.builder()
                .value(report.getValue())
                .available(report.getAvailable())
                .earningsRate(report.getEarningsRate())
                .annualReturns(report.getAnnualReturns())
                .sharpeRatio(report.getSharpeRatio())
                .maxDrawdown(report.getMaxDrawdown())
                .log(log)
                .positions(positions)
                .build();
    }
}
