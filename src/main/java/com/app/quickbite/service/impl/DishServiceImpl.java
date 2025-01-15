package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.dto.DishDto;
import com.app.quickbite.entity.Category;
import com.app.quickbite.entity.Dish;
import com.app.quickbite.entity.DishFlavor;
import com.app.quickbite.mapper.DishMapper;
import com.app.quickbite.service.CategoryService;
import com.app.quickbite.service.DishFlavorService;
import com.app.quickbite.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableTransactionManagement
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    // ServiceImpl<DishMapper, Dish> is a base implementation class provided by MyBatis-Plus.
    // <DishMapper, Dish> are generics where DishMapper is the Mapper interface and Dish is the entity class.

    /**
     * Flavor service
     */
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * Redis cache
     */
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * Add a dish and simultaneously insert flavor data.
     * <p>@Transactional annotation ensures that if adding a dish fails, flavor data will not be added either.
     * Ensure to add the @EnableTransactionManagement annotation in the startup class.
     *
     * @param dishDto Dish data
     */
    @Transactional
    @Override
    public void saveWithFlavors(DishDto dishDto) {
        // Save basic information to the dish table
        this.save(dishDto);

        // Retrieve the dish ID
        Long id = dishDto.getId(); // The getId method is inherited from Dish and returns the dish ID. It is used to link the dish ID with the dishFlavor's dishId.

        // Retrieve the dish flavors
        List<DishFlavor> flavors = dishDto.getFlavors();

        // Iterate through the flavors list, assigning the dish ID to each flavor
        flavors = flavors.stream().peek(item -> item.setDishId(id)).collect(Collectors.toList());

        // Save flavor information to the flavor table
        dishFlavorService.saveBatch(flavors); // saveBatch() is a batch insert method provided by MyBatis-Plus.

        // Clear Redis cache
        Set<Object> keys = redisTemplate.keys("dish_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Update a dish along with flavor data.
     * <p>@Transactional ensures atomicity in case of failure.
     *
     * @param dishDto Dish data
     */
    @Transactional
    @Override
    public void updateWithFlavors(DishDto dishDto) {
        // Update basic information in the dish table
        this.updateById(dishDto);

        // Retrieve the dish ID
        Long id = dishDto.getId();

        // Update flavor data
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id)); // Remove old flavors
        List<DishFlavor> flavors = dishDto.getFlavors(); 
        flavors = flavors.stream().peek(item -> item.setDishId(id)).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors); // Save new flavors

        // Clear Redis cache
        Set<Object> keys = redisTemplate.keys("dish_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Retrieve dish and flavor data by ID.
     *
     * @param id Dish ID
     * @return Dish data (including flavor data)
     */
    @Override
    public DishDto getByIdWithFlavors(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * Retrieve all dishes and their flavors.
     *
     * @param dish Dish filter
     * @return List of dishes (including flavor data)
     */
    @Override
    public List<DishDto> listWithFlavors(Dish dish) {
        List<DishDto> dtoList;

        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dtoList != null) {
            return dtoList;
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = this.list(queryWrapper);

        dtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            LambdaQueryWrapper<DishFlavor> flavorQuery = new LambdaQueryWrapper<>();
            flavorQuery.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(flavorQuery);

            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, dtoList, 1, TimeUnit.DAYS);
        return dtoList;
    }

    /**
     * Update dish status (e.g., on sale or off sale).
     *
     * @param ids    Comma-separated dish IDs
     * @param status New status
     */
    @Override
    public void updateDishStatus(String ids, Integer status) {
        String[] idArray = ids.split(",");

        for (String id : idArray) {
            LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Dish::getId, id);
            wrapper.set(Dish::getStatus, status);
            this.update(wrapper);
        }

        Set<Object> keys = redisTemplate.keys("dish_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Logically delete dishes by their IDs.
     *
     * @param ids Comma-separated dish IDs
     */
    @Override
    public void deleteByIds(String ids) {
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Dish::getId, id);
            wrapper.set(Dish::getStatus, 0);
            wrapper.set(Dish::getIsDeleted, 1);
            this.update(wrapper);
        }

        Set<Object> keys = redisTemplate.keys("dish_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Paginated query for dishes.
     *
     * @param page     Current page number
     * @param pageSize Number of items per page
     * @param name     Filter by dish name
     * @return Paginated dish data
     */
    public Page<DishDto> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        this.page(pageInfo, wrapper);

        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> dtoRecords = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dtoRecords);
        return dishDtoPage;
    }
}

