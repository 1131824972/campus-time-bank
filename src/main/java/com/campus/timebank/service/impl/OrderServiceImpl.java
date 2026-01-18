package com.campus.timebank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

@Service
public class OrderServiceImpl implements IOrderService {

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

        // 1. 检查任务是否存在
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 2. 不能抢自己发的任务
        if (task.getPublisherId().equals(userId)) {
            throw new RuntimeException("不能抢自己发布的任务");
        }

        // 3. 核心：乐观锁抢单
        // 只有当 status = 0 (待接单) 时才更新为 1 (进行中)
        // SQL: UPDATE tb_task SET status=1, receiver_id=userId WHERE id=taskId AND status=0
        UpdateWrapper<Task> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", taskId).eq("status", 0);
        updateWrapper.set("status", 1);
        // 注意：Task实体类如果没receiverId字段，这里暂时只改状态。
        // 实际上我们在设计ER图时Task表没设计receiverId，接收人存在Order表里，这符合规范。
        // 所以这里只更新状态。

        int rows = taskMapper.update(null, updateWrapper);
        if (rows == 0) {
            throw new RuntimeException("手慢了，任务已被抢！");
        }

        // 4. 生成订单
        Order order = new Order();
        order.setTaskId(taskId);
        order.setReceiverId(userId);
        order.setStatus(1); // 1: 进行中
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        Long userId = UserContext.getUserId(); // 当前操作人（应该是发布者）

        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getStatus() != 1) throw new RuntimeException("订单状态异常");

        // 2. 查询任务信息
        Task task = taskMapper.selectById(order.getTaskId());

        // 3. 权限校验：只有发布者才能确认完成
        if (!task.getPublisherId().equals(userId)) {
            throw new RuntimeException("您无权确认此订单");
        }

        // 4. 更新订单和任务状态
        order.setStatus(3); // 已完成
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);

        task.setStatus(3); // 已完成
        taskMapper.updateById(task);

        // 5. 资金结算：给接单人加钱
        User receiver = userMapper.selectById(order.getReceiverId());
        receiver.setBalance(receiver.getBalance().add(task.getPrice()));
        userMapper.updateById(receiver);

        // 6. 记流水
        WalletLog log = new WalletLog();
        log.setUserId(receiver.getId());
        log.setAmount(task.getPrice());
        log.setType(1); // 1: 任务收入
        log.setRemark("完成任务收入: " + task.getTitle());
        log.setCreateTime(LocalDateTime.now());
        walletLogMapper.insert(log);
    }
}