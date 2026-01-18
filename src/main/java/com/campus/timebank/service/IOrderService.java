package com.campus.timebank.service;

public interface IOrderService {

    /**
     * 立即抢单
     * @param taskId 任务ID
     */
    void grabOrder(Long taskId);

    /**
     * 确认完成（验收与结算）
     * @param orderId 订单ID
     */
    void confirmOrder(Long orderId);
}