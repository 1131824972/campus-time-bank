package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.entity.User;
import com.campus.timebank.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        // selectList(null) 意味着查询所有，无条件
        List<User> users = userMapper.selectList(null);
        return Result.success(users);
    }
}