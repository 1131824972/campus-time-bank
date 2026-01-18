package com.campus.timebank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.Task;
import com.campus.timebank.entity.TaskDto;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.WalletLog;
import com.campus.timebank.mapper.TaskMapper;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.mapper.WalletLogMapper;
import com.campus.timebank.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private WalletLogMapper walletLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(TaskDto dto) {
        // ... (保持之前的代码不变) ...
        // 为了节省篇幅，这里省略之前的 publish 代码，请保留你原来的内容！
        // 只添加下面的新方法
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(dto.getPrice()) < 0) {
            throw new RuntimeException("余额不足，无法发布任务！");
        }
        user.setBalance(user.getBalance().subtract(dto.getPrice()));
        userMapper.updateById(user);
        Task task = new Task();
        task.setPublisherId(userId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPrice(dto.getPrice());
        task.setCategory(dto.getCategory());
        task.setLocation(dto.getLocation());
        task.setStatus(0);
        task.setCreateTime(LocalDateTime.now());
        taskMapper.insert(task);
        WalletLog log = new WalletLog();
        log.setUserId(userId);
        log.setAmount(dto.getPrice().negate());
        log.setType(2);
        log.setRemark("发布任务冻结: " + dto.getTitle());
        log.setCreateTime(LocalDateTime.now());
        walletLogMapper.insert(log);
    }

    @Override
    public Page<Task> getTaskList(int pageNum, int pageSize, String category) {
        // 1. 构建分页对象
        Page<Task> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件
        QueryWrapper<Task> query = new QueryWrapper<>();
        query.eq("status", 0); // 只查待接单的

        // 如果传了分类，就按分类查
        if (StringUtils.hasLength(category)) {
            query.eq("category", category);
        }

        // 按时间倒序（最新的在前面）
        query.orderByDesc("create_time");

        // 3. 查询
        return taskMapper.selectPage(page, query);
    }
}