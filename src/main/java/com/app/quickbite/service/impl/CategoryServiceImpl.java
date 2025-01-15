package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.common.CustomException;
import com.app.quickbite.entity.Category;
import com.app.quickbite.entity.Dish;
import com.app.quickbite.entity.Setmeal;
import com.app.quickbite.mapper.CategoryMapper;
import com.app.quickbite.service.CategoryService;
import com.app.quickbite.service.DishService;
import com.app.quickbite.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * Delete category by ID, need to check before deletion
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // Add query condition to query by category ID
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // Check if the current category is associated with any dishes, if so, throw a business exception
        if (count1 > 0) {
            // Associated with dishes, throw a business exception
            throw new CustomException("The current category is associated with dishes and cannot be deleted");
        }

        // Check if the current category is associated with any setmeals, if so, throw a business exception
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // Add query condition to query by category ID
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            // Associated with setmeals, throw a business exception
            throw new CustomException("The current category is associated with setmeals and cannot be deleted");
        }

        // Delete the category normally
        super.removeById(id);
    }
}
