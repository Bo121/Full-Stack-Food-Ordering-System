package com.app.quickbite.dto;

import com.app.quickbite.entity.Dish;
import com.app.quickbite.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object, primarily used for multi-table queries. The query results are encapsulated into a single object for easier use by the frontend. 
 * For example, in the dish addition functionality of this project, the frontend needs to pass the basic dish information along with the dish flavor information. 
 * Since dishes and dish flavors are stored in two separate tables, and the backend has two corresponding entity classes, it is necessary to encapsulate these 
 * two entity classes into one object.<b/>
 */
@Data
public class DishDto extends Dish { // Inherits from the Dish entity class, so the DishDto object contains all the properties of the Dish entity class
    /**
     * Flavors
     */
    private List<DishFlavor> flavors = new ArrayList<>(); // A single ingredient can have multiple flavor options, so a List is used here

    /**
     * Category name
     */
    private String categoryName;

    private Integer copies;
}
