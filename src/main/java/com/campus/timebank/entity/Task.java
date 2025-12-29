package com.campus.timebank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long publisherId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer status; // 0:待接单 1:进行中 2:待确认 3:已完成 4:已取消
    private String location;
    private LocalDateTime createTime;
}