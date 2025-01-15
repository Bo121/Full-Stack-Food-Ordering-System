package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.common.BaseContext;
import com.app.quickbite.entity.ShoppingCart;
import com.app.quickbite.mapper.ShoppingCartMapper;
import com.app.quickbite.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    /**
     * Add a dish or set meal to the shopping cart. If the same item is added, the quantity is incremented.
     *
     * @param shoppingCart The dish to add
     * @return The updated shopping cart
     */
    public ShoppingCart addToCart(ShoppingCart shoppingCart) {
        // Set the user ID to specify the current user's shopping cart
        long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // Check if the current dish or set meal is already in the shopping cart. Dish flavors must match.
        Long dishId = shoppingCart.getDishId(); // Dish ID passed from the frontend

        // Construct query conditions
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId); // where user_id = ?

        // If dishId is present, the request refers to a dish; otherwise, it refers to a set meal.
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId); // where dish_id = ?
//            queryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor()); // where dish_flavor = ?
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId()); // where setmeal_id = ?
        }

        ShoppingCart existingCartItem = this.getOne(queryWrapper);

        // If the item is already in the shopping cart, increment the quantity
        if (existingCartItem != null) {
            Integer quantity = existingCartItem.getNumber();
            existingCartItem.setNumber(quantity + 1); // Increment quantity
            existingCartItem.setCreateTime(LocalDateTime.now());
            this.updateById(existingCartItem);
        } else {
            // If the item is not in the shopping cart, add it
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            existingCartItem = shoppingCart; // After saving, `existingCartItem` is the same as `shoppingCart`
        }

        return existingCartItem;
    }

    /**
     * Reduce the quantity of a dish or set meal in the shopping cart. If the last item is removed, delete it from the cart.
     *
     * @param shoppingCart The dish to remove
     * @return The updated shopping cart
     */
    public ShoppingCart subInCart(ShoppingCart shoppingCart) {
        // Set the user ID to specify the current user's shopping cart
        long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // Check if the current dish or set meal is already in the shopping cart. Dish flavors must match.
        Long dishId = shoppingCart.getDishId(); // Dish ID passed from the frontend

        // Construct query conditions
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId); // where user_id = ?

        // If dishId is present, the request refers to a dish; otherwise, it refers to a set meal.
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId); // where dish_id = ?
            // queryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor()); // where dish_flavor = ?
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId()); // where setmeal_id = ?
        }

        ShoppingCart existingCartItem = this.getOne(queryWrapper);

        // If the quantity in the cart is greater than 2, decrement it
        if (existingCartItem.getNumber() >= 2) {
            Integer quantity = existingCartItem.getNumber();
            existingCartItem.setNumber(quantity - 1);
            this.updateById(existingCartItem);
        } else {
            // If it is the last item, set the quantity to 0 and remove it from the cart
            existingCartItem.setNumber(0);
            this.remove(queryWrapper);
        }

        return existingCartItem;
    }

    /**
     * Display the shopping cart.
     *
     * @return The list of items in the shopping cart
     */
    @Override
    public List<ShoppingCart> showCart() {
        // Create a query condition
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId()); // Get the current user ID
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime); // Sort by creation time in ascending order
        return this.list(queryWrapper);
    }

    /**
     * Clear the shopping cart.
     */
    @Override
    public void cleanCart() {
        // Create a query condition
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId()); // Get the current user ID

        this.remove(queryWrapper); // Remove all items matching the condition
    }
}

