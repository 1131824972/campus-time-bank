package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.User;
import com.campus.timebank.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取个人资料 (我的余额、信用分、昵称)
     */
    @GetMapping("/profile")
    public Result<User> getProfile() {
        // 1. 从 ThreadLocal 拿出当前登录的用户ID (拦截器放进去的)
        Long userId = UserContext.getUserId();

        // 2. 查数据库
        User user = userMapper.selectById(userId);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 脱敏处理 (密码不能返回给前端)
        user.setPassword(null);

        return Result.success(user);
    }
}