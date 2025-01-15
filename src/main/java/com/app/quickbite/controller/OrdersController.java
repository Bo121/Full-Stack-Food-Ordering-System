package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import com.app.quickbite.dto.OrdersDto;
import com.app.quickbite.entity.Orders;
import com.app.quickbite.service.OrdersService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    /**
     * Orders service
     */
    @Autowired
    private OrdersService ordersService;

    /**
     * <h2>Submit an Order<h2/>
     *
     * @param orders Order information
     * @return {@link R}
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("Submit order: {}", orders);
        ordersService.submit(orders);
        return R.success("Order submitted successfully");
    }

    /**
     * <h2>Paginate and Query User's Order Details<h2/>
     *
     * @param page     Current page number
     * @param pageSize Number of records per page
     * @return {@link R}
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {
        // Retrieve paginated user orders
        Page<OrdersDto> userPage = ordersService.getUserPage(page, pageSize);

        return R.success(userPage);
    }

    /**
     * <h2>Paginate and Query All Orders<h2/>
     * <p>If an order number or date is provided, it considers both the order number and date for the query.</p>
     *
     * @param page      Current page number
     * @param pageSize  Number of records per page
     * @param number    Order number
     * @param beginTime Start time
     * @param endTime   End time
     * @return {@link R}
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> page(
            int page,
            int pageSize,
            String number,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        log.info(
                "Order pagination query: page={}，pageSize={}，number={}, beginTime={}, endTime={}",
                page,
                pageSize,
                number,
                beginTime,
                endTime);
        // Perform pagination query based on the provided information.
        // Create a pagination object
        Page<OrdersDto> pageInfo = ordersService.getPage(page, pageSize, number, beginTime, endTime);

        return R.success(pageInfo);
    }
}

