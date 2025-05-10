package edu.zhou.quantflow.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Righter
 * @Description
 * @Date since 4/25/2025
 */
@Getter
@Setter
@ToString
public class Simulation {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    private String stockCode;
    private String strategyType;
    private Integer strategyId;
    private Integer userId;
    private BigDecimal initialFunding;
    private String status;
    private String frequency;
    private LocalDateTime createAt;


}
