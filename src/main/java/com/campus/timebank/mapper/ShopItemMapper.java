package com.campus.timebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.timebank.entity.ShopItem; // 注意导入 ShopItem
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopItemMapper extends BaseMapper<ShopItem> { // 把 <User> 改为 <ShopItem>
}