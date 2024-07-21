package com.it.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Orders
 */
@Data
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // Order number
    private String number;

    // Order status: 1 Pending payment, 2 Pending delivery, 3 Delivered, 4 Completed, 5 Cancelled
    private Integer status;

    // User ID who placed the order
    private Long userId;

    // Address ID
    private Long addressBookId;

    // Order time
    private LocalDateTime orderTime;

    // Checkout time
    private LocalDateTime checkoutTime;

    // Payment method: 1 WeChat, 2 Alipay
    private Integer payMethod;

    // Amount received
    private BigDecimal amount;

    // Remark
    private String remark;

    // Username
    private String userName;

    // Phone number
    private String phone;

    // Address
    private String address;

    // Consignee
    private String consignee;
}
