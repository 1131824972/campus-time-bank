package com.campus.timebank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.Order;
import com.campus.timebank.entity.Task;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.WalletLog;
import com.campus.timebank.mapper.OrderMapper;
import com.campus.timebank.mapper.TaskMapper;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.mapper.WalletLogMapper;
import com.campus.timebank.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 继承 ServiceImpl<OrderMapper, Order>
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WalletLogMapper walletLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grabOrder(Long taskId) {
        Long userId = UserContext.getUserId();

        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        if (task.getPublisherId().equals(userId)) {
            throw new RuntimeException("不能抢自己发布的任务");
        }

        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", taskId).eq("status", 0);
        updateWrapper.set("status", 1);

        int rows = taskMapper.update(null, updateWrapper);
        if (rows == 0) {
            throw new RuntimeException("手慢了，任务已被抢！");
        }

        Order order = new Order();
        order.setTaskId(taskId);
        order.setReceiverId(userId);
        order.setStatus(1); // 1: 进行中
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long id) {
        Long userId = UserContext.getUserId();

        // 1. 优先作为 OrderId 查询
        Order order = orderMapper.selectById(id);

        // 2. 如果查不到，作为 TaskId 查询 (查找该任务的进行中订单)
        if (order == null) {
            QueryWrapper<Order> query = new QueryWrapper<>();
            query.eq("task_id", id).eq("status", 1);
            order = orderMapper.selectOne(query);
        }

        if (order == null) throw new RuntimeException("订单不存在或未在进行中");
        if (order.getStatus() != 1) throw new RuntimeException("订单状态异常");

        Task task = taskMapper.selectById(order.getTaskId());

        if (!task.getPublisherId().equals(userId)) {
            throw new RuntimeException("您无权确认此订单");
        }

        order.setStatus(3); // 已完成
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);

        task.setStatus(3); // 已完成
        taskMapper.updateById(task);

        User receiver = userMapper.selectById(order.getReceiverId());
        receiver.setBalance(receiver.getBalance().add(task.getPrice()));
        userMapper.updateById(receiver);

        WalletLog log = new WalletLog();
        log.setUserId(receiver.getId());
        log.setAmount(task.getPrice());
        log.setType(1);
        log.setRemark("完成任务收入: " + task.getTitle());
        log.setCreateTime(LocalDateTime.now());
        walletLogMapper.insert(log);
    }
}