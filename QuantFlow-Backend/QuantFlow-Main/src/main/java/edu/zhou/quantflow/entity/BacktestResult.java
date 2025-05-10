package edu.zhou.quantflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 回测结果
 * </p>
 *
 * @author Zhouyue
 * @since 2025-04-14
 */
@Getter
@Setter
@ToString
@TableName("backtest_result")
public class BacktestResult implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 策略类型-预存或自定义
     */
    private String strategyType;

    private Integer strategyId;

    private String strategyName;

    /**
     * 回测开始日期
     */
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate startDate;

    /**
     * 回测结束日期
     */
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate endDate;

    /**
     * 股票代码
     */
    private String stockCode;

    private Integer userId;

    /**
     * 初始资金
     */
    private BigDecimal initialFunding;

    /**
     * 结束资金
     */
    private BigDecimal endFunding;

    /**
     * 单笔交易数量
     */
    private Integer tradingSize;

    /**
     * 交易记录日志
     */
    private String tradingLog;

    /**
     * 收益
     */
    private BigDecimal earnings;

    /**
     * 收益率
     */
    private BigDecimal earningsRate;

    /**
     * 年化收益率
     */
    private BigDecimal annualReturns;

    /**
     * 夏普比率
     */
    private BigDecimal sharpeRatio;

    /**
     * 最大回撤
     */
    private BigDecimal maxDrawdown;
}
