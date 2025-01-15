package com.it.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Setmeal
 */
@Data
public class Setmeal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Category ID
    private Long categoryId;

    // Setmeal name
    private String name;

    // Setmeal price
    private BigDecimal price;

    // Status 0: Disabled 1: Enabled
    private Integer status;

    // Code
    private String code;

    // Description
    private String description;

    // Image
    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
