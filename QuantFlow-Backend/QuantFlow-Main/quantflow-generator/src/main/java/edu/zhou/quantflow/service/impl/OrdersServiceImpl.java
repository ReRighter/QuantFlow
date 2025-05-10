package edu.zhou.quantflow.service.impl;

import edu.zhou.quantflow.entity.Orders;
import edu.zhou.quantflow.mapper.OrdersMapper;
import edu.zhou.quantflow.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Zhouyue
 * @since 2025-04-15
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
