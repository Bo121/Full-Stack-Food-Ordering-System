package com.app.quickbite.dto;

import com.app.quickbite.entity.OrderDetail;
import com.app.quickbite.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String email;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}
