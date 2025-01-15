package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.R;
import com.it.reggie.entity.ShoppingCart;
import com.it.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Shopping Cart
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * Add to shopping cart
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("Shopping cart data: {}", shoppingCart);

        // Set user ID to specify whose shopping cart data it is
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if(dishId != null){
            // Adding dish to the shopping cart
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // Adding setmeal to the shopping cart
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // Check if the current dish or setmeal is already in the shopping cart
        // SQL: select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne != null){
            // If it already exists, increase the quantity by one
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // If it doesn't exist, add to the shopping cart with default quantity of one
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * View shopping cart
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("Viewing shopping cart...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * Clear shopping cart
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        // SQL: delete from shopping_cart where user_id = ?

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("Shopping cart cleared successfully");
    }
}
