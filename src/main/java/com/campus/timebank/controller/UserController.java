package com.campus.timebank.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.timebank.common.Result;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.WalletLog;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.mapper.WalletLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WalletLogMapper walletLogMapper;

    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error("用户不存在");
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 【新增】获取我的资金/信用流水
     */
    @GetMapping("/logs")
    public Result<List<WalletLog>> getLogs() {
        Long userId = UserContext.getUserId();
        // 查询我的流水，倒序
        QueryWrapper<WalletLog> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("create_time");

        List<WalletLog> list = walletLogMapper.selectList(query);
        return Result.success(list);
    }
}