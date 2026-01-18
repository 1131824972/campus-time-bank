package com.campus.timebank.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.timebank.common.Result;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.Task;
import com.campus.timebank.entity.TaskDto;
import com.campus.timebank.service.impl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskServiceImpl taskService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody @Validated TaskDto dto) {
        try {
            taskService.publish(dto);
            return Result.success("发布成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取任务列表 (大厅)
     * @param page 页码 (默认1)
     * @param size 条数 (默认10)
     * @param category 分类 (可选)
     */
    @GetMapping("/list")
    public Result<Page<Task>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category
    ) {
        Page<Task> result = taskService.getTaskList(page, size, category);
        return Result.success(result);
    }

    /**
     * 获取我发布的任务
     */
    @GetMapping("/my")
    public Result<List<Task>> myTasks() {
        Long userId = UserContext.getUserId();
        // 查询 publisher_id = 当前用户的任务，按时间倒序
        List<Task> list = taskService.lambdaQuery()
                .eq(Task::getPublisherId, userId)
                .orderByDesc(Task::getCreateTime)
                .list();
        return Result.success(list);
    }
}