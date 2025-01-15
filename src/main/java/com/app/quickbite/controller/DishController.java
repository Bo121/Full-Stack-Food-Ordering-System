package com.app.quickbite.controller;


import com.app.quickbite.common.R;
import com.app.quickbite.dto.DishDto;
import com.app.quickbite.entity.Dish;
import com.app.quickbite.service.CategoryService;
import com.app.quickbite.service.DishFlavorService;
import com.app.quickbite.service.DishService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Dish management
 */
/**
 * Dish Management
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    /**
     * Dish service
     */
    @Autowired
    private DishService dishService;

    /**
     * Dish flavor service
     */
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * <h2>Add a new dish<h2/>
     *
     * @param dishDto Data Transfer Object (DTO): primarily used for multi-table queries to encapsulate the results into one object for frontend use. 
     *                In this project, the frontend needs to send both the dish's basic information and flavor information. 
     *                Since dishes and flavors are in two separate tables and represented by two entity classes in the backend, 
     *                these two entity classes are encapsulated into one object. The @RequestBody annotation converts the JSON data sent by the frontend into an object.
     * @return Generic response object
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("Adding a new dish, dishDto: {}", dishDto.toString()); // Log the operation
        // Save the dish
        dishService.saveWithFlavors(dishDto); // saveWithFlavors is a custom method to save both the dish and its flavors

        return R.success("Dish added successfully");
    }

    /**
     * <h2>Paginate and query dishes<h2/>
     * <p>The images for the dishes are downloaded to the page using the {@link CommonController}.
     *
     * @param page     The current page number sent by the frontend
     * @param pageSize The number of items per page sent by the frontend
     * @param name     Query condition; if `name` is null, all dishes are queried
     * @return A Page object containing all pagination information provided by MyBatis-Plus
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("Paginate query for dishes, page: {}, pageSize: {}, name: {}", page, pageSize, name);

        // Query the dishDtoPage object
        Page<DishDto> dishDtoPage = dishService.page(page, pageSize, name);

        // Return the dishDtoPage object
        return R.success(dishDtoPage);
    }

    /**
     * <h2>Get dish information and flavor information by ID<h2/>
     *
     * @param id Dish ID
     * @return Dish information (including flavors)
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        // Query the dishDto object
        DishDto dishDto = dishService.getByIdWithFlavors(id);

        return R.success(dishDto);
    }

    /**
     * <h2>Save modified dish<h2/>
     *
     * @param dishDto Data Transfer Object (DTO): primarily used for multi-table queries to encapsulate the results into one object for frontend use. 
     *                In this project, the frontend needs to send both the dish's basic information and flavor information. 
     *                Since dishes and flavors are in two separate tables and represented by two entity classes in the backend, 
     *                these two entity classes are encapsulated into one object. The @RequestBody annotation converts the JSON data sent by the frontend into an object.
     * @return Generic response object
     */
    @PutMapping
    public R<String> put(@RequestBody DishDto dishDto) {
        log.info("Modifying dish, dishDto: {}", dishDto.toString()); // Log the operation

        // Save the modified dish
        dishService.updateWithFlavors(dishDto);

        return R.success("Dish modified successfully");
    }

    /**
     * <h2>Change the status of a dish<h2/>
     * <p>If the dish is currently unavailable, mark it as available, and vice versa. 
     * Supports batch status modification.
     *
     * @param ids    The IDs of the dishes to modify, passed from the frontend as a comma-separated string
     * @param status The new status to apply to the dishes, passed as a path variable
     * @return Generic response object containing the result message
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(String ids, @PathVariable Integer status) {
        log.info("Changing dish status, ids: {}, status: {}", ids, status);

        // Change the status of the dishes
        dishService.updateDishStatus(ids, status);

        return R.success("Status updated successfully");
    }

    /**
     * <h2>Delete dishes (logical deletion)<h2/>
     * <p>Supports batch deletion.
     *
     * @param ids The IDs of the dishes to delete, passed from the frontend as a comma-separated string
     * @return Generic response object containing the result message
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        log.info("Deleting dishes, ids: {}", ids);

        // Delete the dishes
        dishService.deleteByIds(ids);

        return R.success("Dishes deleted successfully");
    } 

    /**
     * <h2>Query dishes based on conditions<h2/>
     * <p>For example, if the frontend sends a categoryId (a parameter in the Dish entity representing the category ID), 
     * this method queries all dishes in that category. 
     * Primarily used for adding dishes to a set meal or querying dish lists in the frontend (including flavor information).
     *
     * @param dish Dish entity
     * @return List of dishes, including flavor information (used for frontend display)
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        log.info("Querying dish list, dish: {}", dish);

        // Query the list of dishes
        List<DishDto> dishDtoList = dishService.listWithFlavors(dish);

        return R.success(dishDtoList);
    }
}

