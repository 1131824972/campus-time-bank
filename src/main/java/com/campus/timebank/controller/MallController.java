package com.campus.timebank.controller;

import com.campus.timebank.common.Result;
import com.campus.timebank.entity.ShopItem;
import com.campus.timebank.service.IMallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mall")
public class MallController {

    @Autowired
    private IMallService mallService;

    /**
     * 商品列表
     */
    @GetMapping("/list")
    public Result<List<ShopItem>> list() {
        return Result.success(mallService.getShopList());
    }

    /**
     * 兑换商品
     * 入参: { "itemId": 1 }
     */
    @PostMapping("/exchange")
    public Result<String> exchange(@RequestBody Map<String, Long> params) {
        Long itemId = params.get("itemId");
        if (itemId == null) return Result.error("商品ID不能为空");

        try {
            String code = mallService.exchange(itemId);
            // 返回核销码给前端展示
            return Result.success(code);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}