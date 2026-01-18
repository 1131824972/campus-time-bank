package com.campus.timebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.timebank.entity.Order; // 注意导入 Order
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> { // 把 <User> 改为 <Order>
}