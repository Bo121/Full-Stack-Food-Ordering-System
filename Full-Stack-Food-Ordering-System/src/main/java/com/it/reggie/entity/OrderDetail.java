package com.it.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Order Detail
 */
@Data
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Name
    private String name;

    // Order ID
    private Long orderId;

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
}
