package edu.zhou.quantflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author Zhouyue
 * @since 2025-04-15
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class Positions implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "position_id", type = IdType.AUTO)
    private Integer positionId;

    private Integer simulationId;

    private String stockCode;

    private BigDecimal quantity;

    private BigDecimal costPrice;

    private BigDecimal marketValue;

    private BigDecimal floatingProfit;

    private LocalDateTime updatedAt;
    public Positions(){};

}
