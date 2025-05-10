package edu.zhou.quantflow.dto;


import edu.zhou.quantflow.entity.Positions;
import edu.zhou.quantflow.entity.Simulation;
import edu.zhou.quantflow.entity.SimulationLog;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 5/2/2025
 */
@Getter
@Setter
@Builder
public class SimulationDetailsDto {
    private BigDecimal value;
    private BigDecimal available;
    private BigDecimal earningsRate;
    private BigDecimal annualReturns;
    private BigDecimal sharpeRatio;
    private BigDecimal maxDrawdown;
    private List<SimulationLog> log;
    private Positions positions;
}
