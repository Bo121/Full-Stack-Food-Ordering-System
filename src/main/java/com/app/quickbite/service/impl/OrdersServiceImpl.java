package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.common.BaseContext;
import com.app.quickbite.common.CustomException;
import com.app.quickbite.dto.OrdersDto;
import com.app.quickbite.entity.*;
import com.app.quickbite.mapper.OrdersMapper;
import com.app.quickbite.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Slf4j
@EnableTransactionManagement  // Enable transaction management
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    /**
     * Shopping Cart Service
     */
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * User Service
     */
    @Autowired
    private UserService userService;

    /**
     * Address Book Service
     */
    @Autowired
    private AddressBookService addressBookService;

    /**
     * Order Detail Service
     */
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * Place an order
     * <p>@Transactional annotation will start a transaction before the method executes 
     * and commit it after the execution.
     *
     * @param orders Order information
     */
    @Transactional
    public void submit(Orders orders) {
        // Get the current user ID
        long currentId = BaseContext.getCurrentId();
        // Fetch shopping cart data for the current user
        LambdaQueryWrapper<ShoppingCart> cartQueryWrapper = new LambdaQueryWrapper<>();
        cartQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(cartQueryWrapper);

        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new CustomException("Shopping cart is empty");
        }

        // Fetch user data
        User user = userService.getById(currentId);

        // Fetch address data
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("Address does not exist");
        }

        // Generate order ID using MyBatis-Plus's IdWorker
        long orderId = IdWorker.getId();

        AtomicInteger totalAmount = new AtomicInteger(0);  // Thread-safe total amount

        // Generate order details from shopping cart data
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            totalAmount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // Save order data
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);  // Order status
        orders.setAmount(new BigDecimal(totalAmount.get()));
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() != null ? addressBook.getProvinceName() : "")
                + (addressBook.getCityName() != null ? addressBook.getCityName() : "")
                + (addressBook.getDistrictName() != null ? addressBook.getDistrictName() : "")
                + (addressBook.getDetail() != null ? addressBook.getDetail() : ""));
        this.save(orders);

        // Save order details
        orderDetailService.saveBatch(orderDetails);

        // Clear shopping cart
        shoppingCartService.remove(cartQueryWrapper);
    }

/**
 * Retrieve paginated user orders
 *
 * @param page     Current page number
 * @param pageSize Number of items per page
 * @return Paginated data
 */
public Page<OrdersDto> getUserPage(int page, int pageSize) {
    // Create a pagination object
    Page<Orders> ordersPage = new Page<>(page, pageSize);
    Page<OrdersDto> dtoPage = new Page<>(); // Used to store transformed data

    // Build query conditions
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId()); // Query by user ID
    queryWrapper.orderByDesc(Orders::getOrderTime); // Order by order time in descending order

    // Query basic order data
    this.page(ordersPage, queryWrapper);

    // Copy objects, excluding the 'records' attribute, which requires special handling
    BeanUtils.copyProperties(ordersPage, dtoPage, "records");

    // 'records' is an attribute of the Page object used to store the results of pagination queries. 
    // Since the data in ordersPage 'records' differs from dtoPage, it requires individual processing.
    List<Orders> ordersPageRecords = ordersPage.getRecords();

    // Transform each Orders object in 'records' to an OrdersDto object
    List<OrdersDto> dtoPageRecords = ordersPageRecords.stream().map(item -> {
        // 1. Create an OrdersDto object
        OrdersDto ordersDto = new OrdersDto();
        // 2. Copy basic data from the Orders object to the OrdersDto object
        BeanUtils.copyProperties(item, ordersDto);
        // 3. Query order item details
        LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailQueryWrapper);
        // 4. Set order item details in the OrdersDto object
        ordersDto.setOrderDetails(orderDetails);
        // 5. Query additional data and set it in the OrdersDto object
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getId, item.getUserId());
        User user = userService.getOne(userQueryWrapper);
        if (user != null) {
            ordersDto.setUserName(user.getName());
            ordersDto.setEmail(user.getEmail());
        }
        ordersDto.setAmount(item.getAmount());
        ordersDto.setConsignee(item.getConsignee());
        ordersDto.setAddress(item.getAddress());
        ordersDto.setUserName(item.getConsignee()); 

        return ordersDto;
    }).collect(Collectors.toList());

    // Set the transformed data in the dtoPage object
    dtoPage.setRecords(dtoPageRecords);

    return dtoPage;
}


    /**
     * Retrieve admin order details
     *
     * @param page      Current page number
     * @param pageSize  Number of items per page
     * @param number    Order number
     * @param beginTime Start time for filtering
     * @param endTime   End time for filtering
     * @return Paginated order data
     */
    public Page<OrdersDto> getPage(int page,
                                int pageSize,
                                String number,
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        // Create pagination objects
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> dtoPage = new Page<>(); // Used to store transformed data

        // Build query conditions
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime); // Order by order time in descending order
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number); // Filter by order number
        queryWrapper.between((beginTime != null && endTime != null), Orders::getOrderTime, beginTime, endTime); // Filter by time range

        // Query basic order data
        this.page(ordersPage, queryWrapper);

        // Copy objects, excluding the 'records' attribute, which requires special handling
        BeanUtils.copyProperties(ordersPage, dtoPage, "records");

        // 'records' is an attribute of the Page object used to store the results of pagination queries.
        List<Orders> ordersPageRecords = ordersPage.getRecords();

        // Transform each Orders object in 'records' into an OrdersDto object
        List<OrdersDto> dtoPageRecords = ordersPageRecords.stream().map(item -> {
            // 1. Create an OrdersDto object
            OrdersDto ordersDto = new OrdersDto();
            // 2. Copy basic data from the Orders object to the OrdersDto object
            BeanUtils.copyProperties(item, ordersDto);
            // 3. Query order item details
            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailQueryWrapper);
            // 4. Set order item details in the OrdersDto object
            ordersDto.setOrderDetails(orderDetails);
            // 5. Query additional user data and set it in the OrdersDto object
            LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.eq(User::getId, item.getUserId());
            User user = userService.getOne(userQueryWrapper);
            if (user != null) {
                ordersDto.setUserName(user.getName());
                ordersDto.setEmail(user.getEmail());
            }
            ordersDto.setAmount(item.getAmount());
            ordersDto.setConsignee(item.getConsignee());
            ordersDto.setAddress(item.getAddress());
            ordersDto.setUserName(item.getConsignee()); 

            return ordersDto;
        }).collect(Collectors.toList());

        // Set the transformed data in the dtoPage object
        dtoPage.setRecords(dtoPageRecords);

        return dtoPage;
    }
}
