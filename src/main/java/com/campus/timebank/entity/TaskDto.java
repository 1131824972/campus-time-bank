package com.campus.timebank.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskDto {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "悬赏金额不能为空")
    @DecimalMin(value = "0.1", message = "悬赏金额不能少于0.1")
    private BigDecimal price;

    @NotBlank(message = "分类不能为空")
    private String category; // study, run, life

    private String location;
}