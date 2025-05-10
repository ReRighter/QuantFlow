package edu.zhou.quantflow.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Righter
 * @Description
 * @Date since 4/24/2025
 */
@Data
@Builder
public class OrderResponse {

    private Integer orderId;
    private String status;

}

