package com.crio.qeats.services;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.repositoryservices.CartRepositoryService;
import com.crio.qeats.repositoryservices.OrderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartAndOrderServiceImpl implements CartAndOrderService {

  @Autowired
  private CartRepositoryService cartRepositoryService;

  @Autowired
  private OrderRepositoryService orderRepositoryService;

  @Autowired
  private MenuService menuService;

  @Override
  public Cart findOrCreateCart(String userId) {
    return null;
  }

  @Override
  public CartModifiedResponse addItemToCart(String itemId, String cartId, String restaurantId) throws ItemNotFromSameRestaurantException {
    return null;
  }

  @Override
  public CartModifiedResponse removeItemFromCart(String itemId, String cartId,
                                                 String restaurantId) {
    return null;
  }

  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    return null;
  }
}
