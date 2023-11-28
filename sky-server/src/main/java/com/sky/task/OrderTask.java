package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类 定时处理订单状态
 * @Author jinwang
 * @Date 2023/11/28 14:48
 * @Version 1.0 （版本号）
 */

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时处理超时订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单： {}", LocalDateTime.now());

        List<Orders> orders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));

        if (orders != null && orders.size() > 0) {
            orders.forEach(order -> {
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("timeout");
                orderMapper.update(order);
            });
        }
    }

    /**
     * 定时处理派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder() {
        log.info("定时处理待派送订单: {}", LocalDateTime.now());
        List<Orders> orders = orderMapper.getByStatus(Orders.DELIVERY_IN_PROGRESS);
        if (orders != null && orders.size() > 0) {
            orders.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}
