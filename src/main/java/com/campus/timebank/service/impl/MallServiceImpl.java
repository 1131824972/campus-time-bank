package com.campus.timebank.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.timebank.common.UserContext;
import com.campus.timebank.entity.ExchangeRecord;
import com.campus.timebank.entity.ShopItem;
import com.campus.timebank.entity.User;
import com.campus.timebank.entity.WalletLog;
import com.campus.timebank.mapper.ExchangeRecordMapper;
import com.campus.timebank.mapper.ShopItemMapper;
import com.campus.timebank.mapper.UserMapper;
import com.campus.timebank.mapper.WalletLogMapper;
import com.campus.timebank.service.IMallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MallServiceImpl implements IMallService {

    @Autowired
    private ShopItemMapper shopItemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;
    @Autowired
    private WalletLogMapper walletLogMapper;

    @Override
    public List<ShopItem> getShopList() {
        // 只展示库存 > 0 的商品
        return shopItemMapper.selectList(new QueryWrapper<ShopItem>().gt("stock", 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exchange(Long itemId) {
        Long userId = UserContext.getUserId();

        // 1. 校验商品和库存
        ShopItem item = shopItemMapper.selectById(itemId);
        if (item == null) throw new RuntimeException("商品不存在");
        if (item.getStock() <= 0) throw new RuntimeException("库存不足");

        // 2. 校验余额
        User user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(item.getPrice()) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 3. 扣减库存
        item.setStock(item.getStock() - 1);
        shopItemMapper.updateById(item);

        // 4. 扣减余额
        user.setBalance(user.getBalance().subtract(item.getPrice()));
        userMapper.updateById(user);

        // 5. 生成兑换记录 (含核销码)
        // 使用 Hutool 工具包生成一个不带横线的 UUID
        String code = IdUtil.simpleUUID().toUpperCase();

        ExchangeRecord record = new ExchangeRecord();
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setCode(code);
        record.setStatus(0); // 0: 未核销
        record.setCreateTime(LocalDateTime.now());
        exchangeRecordMapper.insert(record);

        // 6. 记录流水
        WalletLog log = new WalletLog();
        log.setUserId(userId);
        log.setAmount(item.getPrice().negate()); // 支出为负
        log.setType(3); // 3: 商城兑换
        log.setRemark("兑换商品: " + item.getName());
        log.setCreateTime(LocalDateTime.now());
        walletLogMapper.insert(log);

        return code;
    }
}