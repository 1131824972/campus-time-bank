package com.campus.timebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.timebank.entity.WalletLog; // 注意导入 WalletLog
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WalletLogMapper extends BaseMapper<WalletLog> { // 把 <User> 改为 <WalletLog>
}