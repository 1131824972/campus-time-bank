package com.campus.timebank.service;

import com.campus.timebank.entity.ShopItem;
import java.util.List;

public interface IMallService {
    /**
     * 获取商品列表
     */
    List<ShopItem> getShopList();

    /**
     * 兑换商品
     * @param itemId 商品ID
     * @return 核销码 (Code)
     */
    String exchange(Long itemId);
}