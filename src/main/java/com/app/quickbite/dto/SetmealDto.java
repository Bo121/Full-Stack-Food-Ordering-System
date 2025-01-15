package com.app.quickbite.dto;

import com.app.quickbite.entity.Setmeal;
import com.app.quickbite.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
