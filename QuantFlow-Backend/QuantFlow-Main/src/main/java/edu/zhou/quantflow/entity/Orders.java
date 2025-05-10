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
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    private Integer simulationId;

    private String stockCode;

    private String side;

    private String orderType;

    private BigDecimal price;

    private BigDecimal quantity;

    private String status;

    private String frequency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
