package com.app.quickbite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.app.quickbite.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
