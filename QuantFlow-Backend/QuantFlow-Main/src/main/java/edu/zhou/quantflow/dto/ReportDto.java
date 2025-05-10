package edu.zhou.quantflow.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 5/2/2025
 */
@Data
@Builder
public class ReportDto {
    private Integer simulationId;
    private BigDecimal value;
    private BigDecimal available;
    private BigDecimal earningsRate;
    private BigDecimal annualReturns;
    private BigDecimal sharpeRatio;
    private BigDecimal maxDrawdown;
    private List<String> log;
}
