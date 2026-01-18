package com.campus.timebank.service;

import com.campus.timebank.entity.TaskDto;

public interface ITaskService {
    /**
     * 发布任务
     * @param dto 前端参数
     */
    void publish(TaskDto dto);
}