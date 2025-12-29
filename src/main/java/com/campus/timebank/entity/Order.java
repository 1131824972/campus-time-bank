package com.campus.timebank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long receiverId;
    private Integer status; // 1:进行中 2:待确认 3:已完成
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}