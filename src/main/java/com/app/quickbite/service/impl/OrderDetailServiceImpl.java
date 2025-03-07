package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.entity.OrderDetail;
import com.app.quickbite.mapper.OrderDetailMapper;
import com.app.quickbite.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}