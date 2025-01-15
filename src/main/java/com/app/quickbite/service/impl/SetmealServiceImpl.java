package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.common.CustomException;
import com.app.quickbite.dto.SetmealDto;
import com.app.quickbite.entity.Category;
import com.app.quickbite.entity.Setmeal;
import com.app.quickbite.entity.SetmealDish;
import com.app.quickbite.mapper.SetmealMapper;
import com.app.quickbite.service.CategoryService;
import com.app.quickbite.service.SetmealDishService;
import com.app.quickbite.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableTransactionManagement // Enable transaction management
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    // ServiceImpl<SetmealMapper, Setmeal> is a base implementation class provided by MyBatis-Plus.
    // <SetmealMapper, Setmeal> are generic types where SetmealMapper is the Mapper interface, and Setmeal is the entity class.

    /**
     * Service for managing the relationship between set meals and dishes.
     */
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * Service for managing dish categories.
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * Redis cache.
     */
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * Add a new set meal while saving its associated dish relationships.
     *
     * @param setmealDto Data transfer object for the set meal.
     */
    @Override
    @Transactional // Enable transaction management for operations involving multiple tables.
    public void saveWithDishes(SetmealDto setmealDto) {
        // Save basic set meal information.
        this.save(setmealDto);

        // Retrieve the List<SetmealDish> for the set meal. Note that the `setmealId` in the list is null
        // because the front-end does not pass this value. Assign the ID manually.
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().peek(item -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());

        // Save relationships between the set meal and dishes.
        setmealDishService.saveBatch(setmealDishes);

        // Clear Redis cache.
        Set<Object> keys = redisTemplate.keys("setmeal_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Delete set meals and their associated dish relationships.
     * Only set meals that are not being sold can be deleted.
     *
     * @param ids List of set meal IDs to delete.
     */
    @Override
    @Transactional
    public void removeWithDishes(List<Long> ids) {
        // Check if any set meals are currently being sold.
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        int count = this.count(setmealQueryWrapper);

        // Throw an exception if deletion is not allowed.
        if (count > 0) {
            throw new CustomException("Set meals are being sold and cannot be deleted.");
        }

        // Delete set meal data from the setmeal table.
        this.removeByIds(ids);

        // Delete relationships from the setmeal_dish table.
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishQueryWrapper);

        // Clear Redis cache.
        Set<Object> keys = redisTemplate.keys("setmeal_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Update the status of set meals. If the set meal is on sale, mark it as discontinued; if it is discontinued, mark it as on sale.
     *
     * @param ids    List of set meal IDs.
     * @param status Status to update.
     */
    @Override
    public void changeStatus(List<Long> ids, Integer status) {
        for (Long id : ids) {
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Setmeal::getId, id);
            updateWrapper.set(Setmeal::getStatus, status);
            this.update(updateWrapper);
        }

        // Clear Redis cache.
        Set<Object> keys = redisTemplate.keys("setmeal_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Retrieve a set meal's basic information and associated dishes by ID.
     *
     * @param id Set meal ID.
     * @return SetmealDto containing the set meal's details and associated dishes.
     */
    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        // Get basic set meal information.
        Setmeal setmeal = this.getById(id);

        // Create a SetmealDto object to encapsulate details.
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        // Get associated dishes.
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> dishes = setmealDishService.list(setmealDishQueryWrapper);

        // Add dish details to the DTO.
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    /**
     * Update set meal information and its associated dish relationships.
     *
     * @param setmealDto Data transfer object for the set meal.
     */
    @Transactional
    @Override
    public void updateWithDishes(SetmealDto setmealDto) {
        // Update basic set meal information.
        this.updateById(setmealDto);

        // Update relationships between the set meal and dishes.
        Long setmealId = setmealDto.getId();
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmealId));

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().peek(item -> item.setSetmealId(setmealId)).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

        // Clear Redis cache.
        Set<Object> keys = redisTemplate.keys("setmeal_*");
        assert keys != null;
        redisTemplate.delete(keys);
    }

    /**
     * Retrieve a list of set meals along with their associated dishes.
     *
     * @param setmeal Search criteria.
     * @return List of SetmealDto containing the set meals and their associated dishes.
     */
    @Override
    public List<SetmealDto> listWithDishes(Setmeal setmeal) {

        // Declare the list to store the results
        List<SetmealDto> setmealDtoList = null;

        // Dynamically construct the key for Redis caching
        String key = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus(); // Generate Redis key

        // Attempt to retrieve data from Redis cache
        setmealDtoList = (List<SetmealDto>) redisTemplate.opsForValue().get(key);

        // If the data is found in Redis, return it directly
        if (setmealDtoList != null) {
            return setmealDtoList;
        }

        // If data is not in Redis, fetch it from the database
        // Create a query wrapper to set the query conditions
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        // Add query conditions: where category_id = ? and status = 1, order by update_time desc
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, 1)
                .orderByDesc(Setmeal::getUpdateTime);

        // Query the basic setmeal information from the database
        List<Setmeal> setmealList = this.list(queryWrapper);

        // For each setmeal, query the associated dishes and convert to SetmealDto
        setmealDtoList = setmealList.stream().map(item -> {

            // Create a SetmealDto to hold the setmeal information and associated dishes
            SetmealDto setmealDto = new SetmealDto();

            // Copy basic setmeal information to the SetmealDto
            BeanUtils.copyProperties(item, setmealDto);

            // Query the associated dishes for the current setmeal
            LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishQueryWrapper.eq(SetmealDish::getSetmealId, item.getId()); // where setmeal_id = ?

            // Fetch the associated dishes
            List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishQueryWrapper);

            // Add the list of dishes to the SetmealDto
            setmealDto.setSetmealDishes(setmealDishList);

            return setmealDto;

        }).collect(Collectors.toList());

        // Store the fetched data in Redis for future use
        redisTemplate.opsForValue().set(key, setmealDtoList);

        // Return the list of SetmealDto
        return setmealDtoList;
    }


    /**
     * Paginate set meal information.
     *
     * @param page     Current page.
     * @param pageSize Number of items per page.
     * @param name     Set meal name.
     * @return Page of SetmealDto containing set meal details.
     */
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        // Create a pagination object.
        Page<Setmeal> setmealPage = new Page<>(page, pageSize); // `page` is the current page number, and `pageSize` is the number of items per page.
        Page<SetmealDto> setmealDtoPage = new Page<>(); // `SetmealDto` has an additional `categoryName` attribute to store the category name of the set meal. This information cannot be retrieved with the `Setmeal` page object, so a separate `SetmealDto` page object is created.

        // Create a query wrapper for filtering conditions.
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // Add a filter condition: where `name` like '%name%' (if `name` is provided by the front end).
        queryWrapper.like(name != null, Setmeal::getName, name); // Only add the filter condition if `name` is not null.
        // Add a sorting condition: order by `update_time` descending.
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        // Perform the paginated query.
        this.page(setmealPage, queryWrapper); // `setmealPage` is the pagination object, and `queryWrapper` is the filter condition.

        // Copy pagination object data from `setmealPage` to `setmealDtoPage`.
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records"); // Skip copying the `records` field, as it needs to be handled separately.

        // The `records` field in the Page object stores the paginated query results. Since the `records` field in `setmealDtoPage` is empty, it needs to be manually populated.
        List<Setmeal> records = setmealPage.getRecords();

        // For each `Setmeal` object in the records, retrieve the `categoryId`, query the `categoryName`, and populate the corresponding `SetmealDto` object. Other fields are also copied to `SetmealDto`.
        List<SetmealDto> setmealDtoRecordsList = records.stream().map(item -> {
            // Create a new `SetmealDto` object to store the converted data.
            SetmealDto setmealDto = new SetmealDto();
            // Copy the basic fields from the `Setmeal` object to the `SetmealDto` object.
            BeanUtils.copyProperties(item, setmealDto);

            // Retrieve the category name using the `categoryId`.
            Long categoryId = item.getCategoryId(); // Get the `categoryId` from the `Setmeal` object.
            Category category = categoryService.getById(categoryId); // Query the category using the `categoryId`.
            if (category != null) { // Ensure the category is not null.
                String categoryName = category.getName(); // Get the `categoryName` from the category object.
                setmealDto.setCategoryName(categoryName); // Assign the `categoryName` to the `SetmealDto` object.
            }

            return setmealDto;
        }).collect(Collectors.toList()); // Convert the stream back to a List.

        // Assign the `SetmealDto` records to the `records` field of `setmealDtoPage`.
        setmealDtoPage.setRecords(setmealDtoRecordsList);

        return setmealDtoPage; // Return the paginated `SetmealDto` data.
    }

}

