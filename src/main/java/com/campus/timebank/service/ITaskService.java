package com.campus.timebank.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.timebank.entity.Task;
import com.campus.timebank.entity.TaskDto;

public interface ITaskService {
    /**
     * 发布任务
     */
    void publish(TaskDto dto);

    /**
     * 分页获取任务列表
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param category 分类（可选）
     * @return 分页结果
     */
    Page<Task> getTaskList(int pageNum, int pageSize, String category);
}