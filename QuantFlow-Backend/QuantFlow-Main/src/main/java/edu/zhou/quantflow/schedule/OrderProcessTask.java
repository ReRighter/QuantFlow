package edu.zhou.quantflow.schedule;


import edu.zhou.quantflow.entity.Orders;
import edu.zhou.quantflow.service.ITradingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author Righter
 * @Description
 * @Date since 4/22/2025
 */
@Log4j2
@Component
public class OrderProcessTask {
    private final ITradingService tradingService;
    @Autowired
    public OrderProcessTask(ITradingService tradingService){
        this.tradingService = tradingService;
    }
    @Scheduled(cron = "0 * * * * *") //每分钟执行
    public void processMinuteOrders(){

        try {
            tradingService.checkAndProcessOrder("minute");
        }catch (Exception e){
            log.warn("分钟轮询错误:",e);
        }
    }

    @Scheduled(cron = "0 0 9 * * ?")//每天九点统一执行
    public void processDailyOrders(){
        try {
            tradingService.checkAndProcessOrder("day");
        }catch (Exception e){
            log.warn("每日订单轮询错误",e);
        }
    }


}
