package com.campus.timebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.timebank.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}