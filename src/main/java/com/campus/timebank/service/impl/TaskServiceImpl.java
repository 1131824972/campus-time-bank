package com.campus.timebank.service.impl;

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
    @Transactional(rollbackFor = Exception.class) // 开启事务：任何异常都回滚
    public void publish(TaskDto dto) {
        // 1. 获取当前登录用户
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);

        // 2. 校验余额是否充足
        if (user.getBalance().compareTo(dto.getPrice()) < 0) {
            throw new RuntimeException("余额不足，无法发布任务！");
        }

        // 3. 扣除余额 (冻结资金)
        user.setBalance(user.getBalance().subtract(dto.getPrice()));
        userMapper.updateById(user);

        // 4. 保存任务
        Task task = new Task();
        task.setPublisherId(userId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPrice(dto.getPrice());
        task.setCategory(dto.getCategory());
        task.setLocation(dto.getLocation());
        task.setStatus(0); // 0: 待接单
        task.setCreateTime(LocalDateTime.now());
        taskMapper.insert(task);

        // 5. 记录资金流水
        WalletLog log = new WalletLog();
        log.setUserId(userId);
        log.setAmount(dto.getPrice().negate()); // 记录为负数 (支出)
        log.setType(2); // 2: 发布冻结
        log.setRemark("发布任务冻结: " + dto.getTitle());
        log.setCreateTime(LocalDateTime.now());
        walletLogMapper.insert(log);
    }
}