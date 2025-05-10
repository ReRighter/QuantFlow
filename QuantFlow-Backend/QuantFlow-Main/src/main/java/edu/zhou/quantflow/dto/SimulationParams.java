package edu.zhou.quantflow.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Righter
 * @Description
 * @Date since 4/29/2025
 */
@Getter
@Setter
@Builder
public class SimulationParams {
    private String stockCode;
    private String strategyCode;
    private Integer simulationId;
    private String frequency;

}
