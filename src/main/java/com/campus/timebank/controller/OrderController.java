package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 抢单
     * 入参: { "taskId": 101 }
     */
    @PostMapping("/accept")
    public Result<String> accept(@RequestBody Map<String, Long> params) {
        Long taskId = params.get("taskId");
        if (taskId == null) return Result.error("任务ID不能为空");

        try {
            orderService.grabOrder(taskId);
            return Result.success("抢单成功！请尽快联系发布者。");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认完成
     * 入参: { "orderId": 201 }
     */
    @PostMapping("/confirm")
    public Result<String> confirm(@RequestBody Map<String, Long> params) {
        Long orderId = params.get("orderId");
        if (orderId == null) return Result.error("订单ID不能为空");

        try {
            orderService.confirmOrder(orderId);
            return Result.success("订单已完成，资金已结算");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}