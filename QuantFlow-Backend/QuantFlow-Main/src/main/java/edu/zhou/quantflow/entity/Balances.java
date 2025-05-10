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
public class Balances implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "balance_id", type = IdType.AUTO)
    private Integer balanceId;

    private Integer simulationId;

    private BigDecimal available;

    private BigDecimal frozen;

    private BigDecimal total;

    private LocalDateTime updatedAt;

    public Balances(){};
}
