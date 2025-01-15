package com.app.quickbite.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.app.quickbite.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

  /**
   * Add a dish or set meal to the shopping cart. If the same item is added, the quantity increases.
   *
   * @param shoppingCart The dish or set meal to be added
   * @return The shopping cart after adding the item
   */
  ShoppingCart addToCart(ShoppingCart shoppingCart);

  /**
   * Remove a dish or set meal from the shopping cart. If it is the last one, the dish or set meal is removed.
   *
   * @param shoppingCart The dish or set meal to be removed
   * @return The shopping cart after removing the item
   */
  ShoppingCart subInCart(ShoppingCart shoppingCart);

  /**
   * Display the shopping cart
   *
   * @return The list of items in the shopping cart
   */
  List<ShoppingCart> showCart();

  /**
   * Clear the shopping cart
   */
  void cleanCart();
}

