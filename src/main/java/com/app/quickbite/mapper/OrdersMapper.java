package com.app.quickbite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.app.quickbite.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}