package com.app.quickbite.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Employee Entity Class
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Employee ID
     */
    private Long id;

    /** Username */
    private String username;

    /** Full name */
    private String name;

    /** Password */
    private String password;

    /** Phone number */
    private String phone;

    /** Gender */
    private String sex;

    /**
     * ID number
     * <p>Uses camel case, different from the id_number field in the employee table in the database.
     * <p>By setting the mybatis-plus.configuration.map-underscore-to-camel-case property to true in application.yml,
     * MyBatis Plus automatically converts camel case to underline style.
     * <p>The same applies to the following variables.
     */
    private String idNumber;

    /** Employee status: 1 for employed */
    private Integer status;

    /** Creation time */
    @TableField(fill = FieldFill.INSERT) // Automatically filled during insertion (MyBatis Plus feature)
    private LocalDateTime createTime;

    /** Update time */
    @TableField(fill = FieldFill.INSERT_UPDATE) // Automatically filled during insertion and update
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT) // Automatically filled during insertion
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) // Automatically filled during insertion and update
    private Long updateUser;
}
