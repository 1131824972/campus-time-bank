package com.campus.timebank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.timebank.entity.Task;
import com.campus.timebank.entity.TaskDto;

/**
 * 继承 IService<Task> 以获得 MyBatis-Plus 的通用 CRUD 能力
 */
public interface ITaskService extends IService<Task> {

    // 自定义业务方法
    void publish(TaskDto dto);

    Page<Task> getTaskList(int page, int size, String category);
}