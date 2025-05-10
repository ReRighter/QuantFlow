package edu.zhou.quantflow.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Righter
 * @Description
 * @Date since 4/28/2025
 */
@Getter
@Setter
@Builder
public class SimulationReport {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer simulationId;
    private BigDecimal value;
    private BigDecimal available;
    private BigDecimal earningsRate;
    private BigDecimal annualReturns;
    private BigDecimal sharpeRatio;
    private BigDecimal maxDrawdown;
    private LocalDateTime updateAt;
}
