package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.CustomException;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.entity.SetmealDish;
import com.it.reggie.mapper.SetmealMapper;
import com.it.reggie.service.SetmealDishService;
import com.it.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * Add new setmeal, and save the relationship between the setmeal and dishes
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // Save the basic information of the setmeal, operate setmeal, and perform insert operation
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // Save the relationship information between the setmeal and dishes, operate setmeal_dish, and perform insert operation
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * Delete setmeal, and delete the relationship data between the setmeal and dishes
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // select count(*) from setmeal where id in (1,2,3) and status = 1
        // Query the status of the setmeal to determine if it can be deleted
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            // If it cannot be deleted, throw a business exception
            throw new CustomException("Setmeal is being sold and cannot be deleted");
        }

        // If it can be deleted, first delete the data in the setmeal table --- setmeal
        this.removeByIds(ids);

        // delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        // Delete the data in the relationship table ---- setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
