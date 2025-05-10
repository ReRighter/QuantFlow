package edu.zhou.quantflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
public class Trades implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "trade_id", type = IdType.AUTO)
    private Integer tradeId;

    private Integer orderId;

    private Integer userId;

    private String stockCode;

    private String side;

    private BigDecimal price;

    private BigDecimal quantity;

    private BigDecimal fee;

    private LocalDateTime executedAt;
}
