package com.campus.timebank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

/**
 * 继承 ServiceImpl<TaskMapper, Task> 以自动实现 IService 中的方法
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private WalletLogMapper walletLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(TaskDto dto) {
        // 1. 获取当前登录用户
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

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
        log.setAmount(dto.getPrice().negate());
        log.setType(2); // 2: 发布冻结
        log.setRemark("发布任务冻结: " + dto.getTitle());
        log.setCreateTime(LocalDateTime.now());
        walletLogMapper.insert(log);
    }

    @Override
    public Page<Task> getTaskList(int pageNum, int pageSize, String category) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Task> query = new QueryWrapper<>();
        query.eq("status", 0);

        if (StringUtils.hasLength(category) && !"all".equals(category)) {
            query.eq("category", category);
        }

        query.orderByDesc("create_time");
        return taskMapper.selectPage(page, query);
    }
}