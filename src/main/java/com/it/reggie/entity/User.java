package com.it.reggie.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * User information
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Name
    private String name;

    // Phone number
    private String phone;

    // Gender 0 Female 1 Male
    private String sex;

    // ID card number
    private String idNumber;

    // Avatar
    private String avatar;

    // Status 0: Disabled, 1: Active
    private Integer status;
}
