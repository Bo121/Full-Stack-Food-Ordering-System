package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.entity.SetmealDish;
import com.app.quickbite.mapper.SetmealDishMapper;
import com.app.quickbite.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper,SetmealDish> implements SetmealDishService {
}
