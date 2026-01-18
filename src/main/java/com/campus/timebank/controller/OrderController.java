package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.Order;
import com.campus.timebank.entity.Task;
import com.campus.timebank.service.IOrderService;
import com.campus.timebank.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ITaskService taskService;

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
     * 入参: { "orderId": 201 } 或 { "taskId": 101 }
     * 说明：我们在 Service 层实现了智能查找，这里只需要把非空的 ID 传进去即可
     */
    @PostMapping("/confirm")
    public Result<String> confirm(@RequestBody Map<String, Long> params) {
        // 优先取 orderId，没有则取 taskId
        Long id = params.get("orderId");
        if (id == null) {
            id = params.get("taskId");
        }

        if (id == null) return Result.error("订单ID或任务ID不能为空");

        try {
            orderService.confirmOrder(id);
            return Result.success("订单已完成，资金已结算");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我参与的任务 (我抢的单)
     * 用于前端“我的任务 - 我参与的”列表
     */
    @GetMapping("/my")
    public Result<List<Task>> myOrders() {
        Long userId = UserContext.getUserId();

        // 1. 先查 Order 表，找到我作为接收者(Receiver)的所有订单
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getReceiverId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();

        // 如果没有接单记录，直接返回空列表
        if (orders.isEmpty()) {
            return Result.success(List.of());
        }

        // 2. 提取所有关联的 taskId
        List<Long> taskIds = orders.stream()
                .map(Order::getTaskId)
                .collect(Collectors.toList());

        // 3. 查 Task 表，返回任务详情
        if (taskIds.isEmpty()) {
            return Result.success(List.of());
        }
        List<Task> tasks = taskService.listByIds(taskIds);

        return Result.success(tasks);
    }
}