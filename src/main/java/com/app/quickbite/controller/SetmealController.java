package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import com.app.quickbite.dto.SetmealDto;
import com.app.quickbite.entity.Setmeal;
import com.app.quickbite.service.CategoryService;
import com.app.quickbite.service.SetmealDishService;
import com.app.quickbite.service.SetmealService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Setmeal management
 */

 @Slf4j
 @RestController
 @RequestMapping("/setmeal")
 public class SetmealController {
 
     /**
      * Setmeal service
      */
     @Autowired
     private SetmealService setmealService;
 
     /**
      * Setmeal dish service
      */
     @Autowired
     private SetmealDishService setmealDishService;
 
     /**
      * Category service
      */
     @Autowired
     private CategoryService categoryService;
 
     /**
      * <h2>Add a New Setmeal<h2/>
      *
      * @param setmealDto Data Transfer Object (DTO) that includes setmeal information (`Setmeal`) 
      *                   and the associated dishes (`SetmealDish List`). 
      *                   The @RequestBody annotation is used to receive JSON data from the frontend.
      * @return Success message
      */
     @PostMapping
     public R<String> save(@RequestBody SetmealDto setmealDto) {
         log.info("Adding a new setmeal: {}", setmealDto);
         // Add the setmeal
         setmealService.saveWithDishes(setmealDto); // Custom method, see `SetmealServiceImpl`
 
         return R.success("Setmeal added successfully");
     }
 
     /**
      * <h2>Paginate and Query Setmeals<h2/>
      * <p>Dish images are downloaded to the page using {@link CommonController}.
      *
      * @param page     Current page number from the frontend
      * @param pageSize Number of records per page from the frontend
      * @param name     Query condition. If `name` is null, query all setmeals.
      * @return A `Page` object containing all pagination information
      */
     @GetMapping("/page")
     public R<Page> page(int page, int pageSize, String name) {
         // Paginate and query setmeals
         Page<SetmealDto> setmealDtoPage = setmealService.page(page, pageSize, name);
 
         return R.success(setmealDtoPage);
     }
 
     /**
      * <h2>Delete Setmeals<h2/>
      * <p>Unlike the `delete` method in {@link DishController}, this method uses a `List` 
      * to receive the IDs passed from the frontend. It first checks if the setmeals are in 
      * an active state. If active, they cannot be deleted.
      *
      * @param ids List of IDs passed from the frontend. If multiple IDs are provided, 
      *            they are separated by commas. The @RequestParam annotation is used to receive parameters.
      * @return Success message
      */
     @DeleteMapping
     public R<String> delete(@RequestParam List<Long> ids) {
         log.info("Deleting setmeals with IDs: {}", ids);
         setmealService.removeWithDishes(ids);
 
         return R.success("Setmeals deleted successfully");
     }
 
     /**
      * <h2>Change Setmeal Status<h2/>
      *
      * @param ids    List of IDs for the setmeals to be updated
      * @param status New status for the setmeals
      * @return Success message
      */
     @PostMapping("/status/{status}")
     public R<String> changeStatus(@RequestParam List<Long> ids, @PathVariable Integer status) {
         log.info("Changing status of setmeals with IDs: {}, New status: {}", ids, status);
         setmealService.changeStatus(ids, status);
 
         return R.success("Setmeal status updated successfully");
     }
 
     /**
      * <h2>Get Setmeal Details by ID<h2/>
      *
      * @param id Setmeal ID
      * @return A DTO containing setmeal details and associated dishes
      */
     @GetMapping("/{id}")
     public R<SetmealDto> get(@PathVariable Long id) {
         log.info("Fetching setmeal with ID: {}", id);
         SetmealDto setmealDto = setmealService.getByIdWithDishes(id);
 
         return R.success(setmealDto);
     }
 
     /**
      * <h2>Update and Save Setmeal<h2/>
      *
      * @param setmealDto Data Transfer Object (DTO) used for multi-table queries. 
      *                   It encapsulates setmeal information and the associated dishes into one object.
      *                   The @RequestBody annotation is used to receive JSON data from the frontend.
      * @return Success message
      */
     @PutMapping
     public R<String> put(@RequestBody SetmealDto setmealDto) {
         log.info("Updating setmeal: {}", setmealDto);
         // Update and save the setmeal
         setmealService.updateWithDishes(setmealDto);
 
         return R.success("Setmeal updated successfully");
     }
 
     /**
      * <h2>Frontend Query for Setmeals and Their Contents<h2/>
      *
      * @param setmeal Query condition from the frontend
      * @return List of setmeals with their details
      */
     @GetMapping("/list")
     public R<List<SetmealDto>> list(Setmeal setmeal) {
         log.info("Frontend query for setmeals: {}", setmeal);
         // Query all setmeal information
         List<SetmealDto> setmealDtoList = setmealService.listWithDishes(setmeal);
 
         return R.success(setmealDtoList);
     }
 
     /**
      * <h2>Check Setmeal Details<h2/>
      * <p>This method is under development.</p>
      *
      * @param id Setmeal ID
      * @return A DTO containing setmeal and dish details
      */
     @GetMapping("/dish/{id}")
     public R<SetmealDto> dish(@PathVariable Long id) {
         log.info("Frontend query for setmeal details with ID: {}", id);
         // Fetch basic setmeal information
         SetmealDto dishes = setmealService.getByIdWithDishes(id);
 
         return R.success(dishes);
     }
 }
 
