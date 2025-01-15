package com.app.quickbite.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.app.quickbite.dto.SetmealDto;
import com.app.quickbite.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * Add a new set meal and save the association between the set meal and dishes.
     *
     * @param setmealDto Data transfer object for the set meal
     */
    void saveWithDishes(SetmealDto setmealDto);

    /**
     * Delete a set meal and also delete the associated data between the set meal and dishes.
     *
     * @param ids List of set meal IDs
     */
    void removeWithDishes(List<Long> ids);

    /**
     * Update the status of a set meal. If it is currently on sale, change it to off-sale, 
     * and if it is off-sale, change it to on sale.
     *
     * @param ids    List of set meal IDs
     * @param status Status to be updated
     */
    void changeStatus(List<Long> ids, Integer status);

    /**
     * Get the basic information of a set meal and the dishes included in it by ID.
     *
     * @param id Set meal ID
     */
    SetmealDto getByIdWithDishes(Long id);

    /**
     * Update set meal information and update the association between the set meal and dishes.
     *
     * @param setmealDto Data transfer object for the set meal
     */
    void updateWithDishes(SetmealDto setmealDto);

    /**
     * Get the basic information of all set meals and the dishes included in them.
     *
     * @param setmeal Query conditions for the set meal
     */
    List<SetmealDto> listWithDishes(Setmeal setmeal);

    /**
     * Paginated query for set meal information.
     *
     * @param page     Current page number
     * @param pageSize Number of items per page
     * @param name     Set meal name
     */
    Page<SetmealDto> page(int page, int pageSize, String name);
}
