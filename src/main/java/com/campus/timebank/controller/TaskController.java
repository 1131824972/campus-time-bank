package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.entity.TaskDto;
import com.campus.timebank.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody @Validated TaskDto dto) {
        try {
            taskService.publish(dto);
            return Result.success("发布成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}