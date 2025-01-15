package com.app.quickbite.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dish
 */
@Data
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Dish name
    private String name;

    // Dish category ID
    private Long categoryId;

    // Dish price
    private BigDecimal price;

    // Product code
    private String code;

    // Image
    private String image;

    // Description
    private String description;

    // 0 Stopped sale 1 On sale
    private Integer status;

    // Order
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    // Is deleted: logical deletion annotation, value for not deleted, delval for deleted
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
