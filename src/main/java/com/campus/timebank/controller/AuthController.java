package com.campus.timebank.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.timebank.common.Result;
import com.campus.timebank.entity.LoginDto;
import com.campus.timebank.entity.User;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Validated LoginDto dto) {
        // 1. 检查学号是否重复
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", dto.getUsername());
        if (userMapper.selectCount(query) > 0) {
            return Result.error("该学号已被注册");
        }

        // 2. 初始化用户信息
        User user = new User();
        user.setUsername(dto.getUsername());
        // 密码加密 (MD5)
        user.setPassword(DigestUtil.md5Hex(dto.getPassword()));
        // 默认昵称：同学+学号后4位
        String suffix = dto.getUsername().length() > 4 ? dto.getUsername().substring(dto.getUsername().length() - 4) : dto.getUsername();
        user.setNickname("同学" + suffix);
        user.setBalance(BigDecimal.ZERO);
        user.setCreditScore(100);
        user.setIsCertified(0);
        user.setCreateTime(LocalDateTime.now());

        // 3. 写入数据库
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginDto dto) {
        // 1. 查询用户
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", dto.getUsername());
        User user = userMapper.selectOne(query);

        // 2. 校验账号是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验密码 (数据库存的是密文，所以要把输入的也加密一下再比对)
        if (!user.getPassword().equals(DigestUtil.md5Hex(dto.getPassword()))) {
            return Result.error("密码错误");
        }

        // 4. 生成 Token
        String token = jwtUtil.createToken(user.getId());

        // 5. 返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("userId", user.getId());
        map.put("nickname", user.getNickname());
        map.put("balance", user.getBalance());

        return Result.success(map);
    }
}