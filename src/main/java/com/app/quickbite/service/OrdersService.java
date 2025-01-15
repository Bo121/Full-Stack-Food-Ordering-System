package com.app.quickbite.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.app.quickbite.dto.OrdersDto;
import com.app.quickbite.entity.Orders;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface OrdersService extends IService<Orders> {

    /**
     * User places an order
     *
     * @param orders Order information
     */
    void submit(Orders orders);

    /**
     * Get paginated user orders
     *
     * @param page     Page number
     * @param pageSize Number of items per page
     * @return Paginated order data
     */
    Page<OrdersDto> getUserPage(int page, int pageSize);

    /**
     * Get detailed order information for administrators
     *
     * @param page      Page number
     * @param pageSize  Number of items per page
     * @param number    Order number
     * @param beginTime Start time
     * @param endTime   End time
     */
    Page<OrdersDto> getPage(int page,
                            int pageSize,
                            String number,
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime);
}

