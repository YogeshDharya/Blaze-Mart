/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.exceptions.CartNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartRepositoryServiceImpl implements CartRepositoryService {

  @Override
  public String createCart(Cart cart) {
    return null;
  }

  @Override
  public Optional<Cart> findCartByUserId(String userId) {
    return Optional.empty();
  }

  @Override
  public Cart findCartByCartId(String cartId) throws CartNotFoundException {
    return null;
  }

  @Override
  public Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
    return null;
  }

  @Override
  public Cart removeItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
    return null;
  }
}