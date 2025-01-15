package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.entity.DishFlavor;
import com.app.quickbite.mapper.DishFlavorMapper;
import com.app.quickbite.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
