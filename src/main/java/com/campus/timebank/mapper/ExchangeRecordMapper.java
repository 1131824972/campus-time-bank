package com.campus.timebank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.timebank.entity.ExchangeRecord; // 注意导入 ExchangeRecord
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExchangeRecordMapper extends BaseMapper<ExchangeRecord> { // 把 <User> 改为 <ExchangeRecord>
}