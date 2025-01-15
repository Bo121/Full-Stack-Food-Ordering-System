package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import com.app.quickbite.entity.Category;
import com.app.quickbite.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category management
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * <h2>Add a new category<h2/>
     *
     * @param category Category entity received from the frontend in JSON format.
     * @return Response with success message.
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("Adding new category, category={}", category);
        categoryService.save(category); // Save the category to the database.

        return R.success("Category added successfully");
    }

    /**
     * <h2>Display paginated list of categories<h2/>
     *
     * @param page     Current page number received from the frontend.
     * @param pageSize Number of records per page received from the frontend.
     * @return Paginated list of categories wrapped in the custom response class.
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        // Create a Page object for pagination.
        Page<Category> pageInfo = new Page<>();
        // Create a query wrapper to add sorting conditions.
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // Add sorting by 'sort' field in ascending order.
        queryWrapper.orderByAsc(Category::getSort);
        // Call the service method to retrieve paginated data.
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo); // Return the paginated data.
    }

    /**
     * <h2>Delete a category by its ID<h2/>
     * <p>If the category contains dishes or set meals, it throws a {@link CustomException}.
     *
     * @param id The ID of the category to be deleted.
     * @return Response with success or error message.
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("Deleting category, id={}", id);
        categoryService.remove(id); // Remove the category by ID.

        return R.success("Category deleted successfully");
    }

    /**
     * <h2>Update category information<h2/>
     *
     * @param category Category entity with updated information received in JSON format.
     * @return Response with success message.
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("Updating category information: {}", category);
        categoryService.updateById(category); // Update the category based on its ID.

        return R.success("Category updated successfully");
    }

    /**
     * <h2>Retrieve category list by type<h2/>
     *
     * @param category Category entity used to filter results by type. For example,
     *                 type = 1 for dishes and type = 2 for set meals. This method
     *                 can handle similar functionality as well.
     * @return List of categories filtered by type.
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        // Create a query wrapper to filter by type.
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // Add sorting conditions: ascending by 'sort', then descending by 'update_time'.
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // Retrieve the filtered list from the service layer.
        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list); // Return the filtered list.
    }
}