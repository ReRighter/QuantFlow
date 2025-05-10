package edu.zhou.quantflow.dto;


import edu.zhou.quantflow.entity.Balances;
import edu.zhou.quantflow.entity.Positions;
import lombok.Builder;
import lombok.Data;



/**
 * @Author Righter
 * @Description
 * @Date since 4/24/2025
 */
@Data
@Builder
public class AccountStatus {
    Balances balances;
    Positions positions;
}
