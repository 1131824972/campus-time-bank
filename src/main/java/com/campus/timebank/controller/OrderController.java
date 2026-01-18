package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.Order;
import com.campus.timebank.entity.Task;
import com.campus.timebank.service.impl.OrderServiceImpl;
import com.campus.timebank.service.impl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private TaskServiceImpl taskService;

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
     */
    @PostMapping("/confirm")
    public Result<String> confirm(@RequestBody Map<String, Long> params) {
        Long orderId = params.get("orderId");
        Long taskId = params.get("taskId");

        try {
            if (orderId != null) {
                // 标准流程：通过 OrderId 确认
                orderService.confirmOrder(orderId);
            } else if (taskId != null) {
                // 兼容流程：通过 TaskId 确认 (需要修改 OrderService 逻辑以支持或在这里查找)
                // 鉴于 OrderService.confirmOrder 已经被修改为支持 TaskId 查找逻辑 (在之前的步骤中建议修改了 Service)
                // 我们直接调用 confirmOrder 并传入 taskId 即可
                // 注意：前提是你在 OrderServiceImpl.java 中实施了“按ID查不到Order就按TaskId查”的逻辑
                orderService.confirmOrder(taskId);
            } else {
                return Result.error("ID不能为空");
            }
            return Result.success("订单已完成，资金已结算");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我参与的任务 (我抢的单)
     */
    @GetMapping("/my")
    public Result<List<Task>> myOrders() {
        Long userId = UserContext.getUserId();

        // 1. 先查 Order 表，找到我接的所有单
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getReceiverId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();

        if (orders.isEmpty()) {
            return Result.success(List.of());
        }

        // 2. 拿到所有 taskId
        List<Long> taskIds = orders.stream().map(Order::getTaskId).collect(Collectors.toList());

        // 3. 查 Task 表，返回任务详情
        if (taskIds.isEmpty()) {
            return Result.success(List.of());
        }

        List<Task> tasks = taskService.listByIds(taskIds);
        return Result.success(tasks);
    }
}