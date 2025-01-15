package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import com.app.quickbite.entity.ShoppingCart;
import com.app.quickbite.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Shopping Cart
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    /**
     * Shopping cart service
     */
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * <h2>View Shopping Cart<h2/>
     *
     * @return List of items in the shopping cart
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("Viewing shopping cart");
        List<ShoppingCart> shoppingCarts = shoppingCartService.showCart();

        return R.success(shoppingCarts);
    }

    /**
     * <h2>Add a Dish or Setmeal to the Shopping Cart<h2/>
     *
     * @param shoppingCart The item to add to the shopping cart. 
     *                     The @RequestBody annotation extracts data from JSON.
     * @return Updated shopping cart object
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("Adding to shopping cart: {}", shoppingCart); // Log the operation
        ShoppingCart shoppingCartOne = shoppingCartService.addToCart(shoppingCart); // Add to cart

        return R.success(shoppingCartOne);
    }

    /**
     * <h2>Remove a Dish or Setmeal from the Shopping Cart<h2/>
     *
     * @param shoppingCart The item to remove from the shopping cart. 
     *                     The @RequestBody annotation extracts data from JSON.
     * @return Updated shopping cart object
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("Removing from shopping cart: {}", shoppingCart); // Log the operation
        ShoppingCart shoppingCartOne = shoppingCartService.subInCart(shoppingCart); // Remove from cart

        return R.success(shoppingCartOne);
    }

    /**
     * <h2>Clear the Shopping Cart<h2/>
     *
     * @return Success message
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("Clearing shopping cart");
        shoppingCartService.cleanCart();

        return R.success("Shopping cart cleared successfully");
    }

}
