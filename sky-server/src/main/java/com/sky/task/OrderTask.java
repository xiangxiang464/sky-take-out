package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单超时取消，每隔1分钟执行一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void orderCancel() {
        log.info("订单超时取消;{}", LocalDateTime.now());
        // 查询超时订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> lists= orderMapper.selectByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if (lists != null && lists.size() > 0) {
            for (Orders orders : lists) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 订单派送，每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void orderDelivery() {
        log.info("订单派送;{}", LocalDateTime.now());
        // 查询待派送的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> lists= orderMapper.selectByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (lists != null && lists.size() > 0) {
            for (Orders orders : lists) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
