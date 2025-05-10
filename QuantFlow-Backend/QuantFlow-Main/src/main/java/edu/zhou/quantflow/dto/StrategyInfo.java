package edu.zhou.quantflow.dto;


import lombok.Builder;
import lombok.Data;

/**
 * @Author Righter
 * @Description
 * @Date since 4/26/2025
 */
@Data
@Builder
public class StrategyInfo {
    String name;
    String type;
    Integer id;
}
