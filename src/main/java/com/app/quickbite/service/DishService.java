package com.app.quickbite.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.app.quickbite.dto.DishDto;
import com.app.quickbite.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    /**
     * Add a dish and insert flavor data simultaneously
     *
     * @param dishDto Dish data
     */
    void saveWithFlavors(DishDto dishDto);

    /**
     * Update a dish and update flavor data simultaneously
     *
     * @param dishDto Dish data
     */
    void updateWithFlavors(DishDto dishDto);

    /**
     * Get dish and flavor data by ID
     *
     * @param id Dish ID
     * @return Dish data (including flavor data)
     */
    DishDto getByIdWithFlavors(Long id);

    /**
     * Get all dishes and their flavor data
     *
     * @return List of dish data (including flavor data)
     */
    List<DishDto> listWithFlavors(Dish dish);

    /**
     * Update dish status. If it is on sale, change it to off sale; 
     * if it is off sale, change it to on sale.
     *
     * @param ids    Dish IDs
     * @param status Status to be updated
     */
    void updateDishStatus(String ids, Integer status);

    /**
     * Delete dishes (logical deletion)
     *
     * @param ids Dish IDs passed from the frontend, which can be one or multiple IDs, 
     *            with multiple IDs separated by commas
     */
    void deleteByIds(String ids);

    /**
     * Paginate query for dishes
     * <p>The dish images are downloaded to the page using functionality provided by 
     * {@link CommonController}.
     *
     * @param page     Pagination parameter passed from the frontend, indicating the current page number
     * @param pageSize Pagination parameter passed from the frontend, indicating the number of items per page
     * @param name     Query condition. If the name is empty, query all dishes
     * @return Page object provided by MyBatis-Plus, containing all pagination information
     */
    Page<DishDto> page(int page, int pageSize, String name);
}

