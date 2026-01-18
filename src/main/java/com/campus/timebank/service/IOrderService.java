package com.campus.timebank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.timebank.entity.Order;

/**
 * 继承 IService<Order>
 */
public interface IOrderService extends IService<Order> {

    void grabOrder(Long taskId);

    void confirmOrder(Long orderId);
}