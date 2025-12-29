package com.campus.timebank.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_shop_item")
public class ShopItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String image;
    private BigDecimal price;
    private Integer stock;
    private String shopName;
    private LocalDateTime createTime;
}