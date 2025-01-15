package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Category;
import com.it.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category management
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * Add new category
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("Category added successfully");
    }

    /**
     * Pagination query
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        // Page constructor
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // Condition constructor
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // Add sorting condition, sort by 'sort' field
        queryWrapper.orderByAsc(Category::getSort);

        // Pagination query
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * Delete category by ID
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("Deleting category, ID: {}",id);

        // categoryService.removeById(id);
        categoryService.remove(id);

        return R.success("Category deleted successfully");
    }

    /**
     * Update category by ID
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("Updating category: {}",category);

        categoryService.updateById(category);

        return R.success("Category updated successfully");
    }

    /**
     * Query category data based on conditions
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        // Condition constructor
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // Add condition
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // Add sorting conditions
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
