package com.campus.timebank.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.timebank.common.Result;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.ExchangeRecord;
import com.campus.timebank.entity.ShopItem;
import com.campus.timebank.mapper.ExchangeRecordMapper;
import com.campus.timebank.mapper.ShopItemMapper;
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

    @Autowired
    private ShopItemMapper shopItemMapper;

    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;

    /**
     * 商品列表
     */
    @GetMapping("/list")
    public Result<List<ShopItem>> list() {
        // 查询所有库存 > 0 的商品 (这里为了演示查所有)
        return Result.success(shopItemMapper.selectList(null));
    }

    /**
     * 兑换商品
     */
    @PostMapping("/exchange")
    public Result<String> exchange(@RequestBody Map<String, Long> params) {
        Long itemId = params.get("itemId");
        if (itemId == null) return Result.error("商品ID不能为空");
        try {
            mallService.exchange(itemId);
            // 简单的核销码生成逻辑，实际可以用 UUID
            return Result.success("E" + System.currentTimeMillis());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 【新增】我的兑换记录
     */
    @GetMapping("/my")
    public Result<List<ExchangeRecord>> myExchanges() {
        Long userId = UserContext.getUserId();
        // 查询当前用户的兑换记录，按时间倒序
        QueryWrapper<ExchangeRecord> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("create_time");

        List<ExchangeRecord> list = exchangeRecordMapper.selectList(query);
        return Result.success(list);
    }
}