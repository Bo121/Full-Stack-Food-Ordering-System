package com.it.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Employee entity
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber; // ID card number

    private Integer status;

    @TableField(fill = FieldFill.INSERT) // Field filled on insert
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) // Field filled on insert and update
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT) // Field filled on insert
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) // Field filled on insert and update
    private Long updateUser;

}
