package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
