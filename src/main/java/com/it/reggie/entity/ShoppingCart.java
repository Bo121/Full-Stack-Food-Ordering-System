package com.it.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shopping Cart
 */
@Data
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Name
    private String name;

    // User ID
    private Long userId;

    // Dish ID
    private Long dishId;

    // Setmeal ID
    private Long setmealId;

    // Flavor
    private String dishFlavor;

    // Quantity
    private Integer number;

    // Amount
    private BigDecimal amount;

    // Image
    private String image;

    private LocalDateTime createTime;
}
