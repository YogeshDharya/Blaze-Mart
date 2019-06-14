package com.crio.qeats.services;

import static com.crio.qeats.exceptions.QEatsException.ITEM_NOT_FOUND_IN_RESTAURANT_MENU;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exceptions.UserNotFoundException;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.repositoryservices.CartRepositoryService;
import com.crio.qeats.repositoryservices.OrderRepositoryService;
import java.util.Optional;
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
  public Cart findOrCreateCart(String userId) throws UserNotFoundException {
    Optional<Cart> cartByUserId = cartRepositoryService.findCartByUserId(userId);

    if (cartByUserId.isPresent()) {
      return cartByUserId.get();
    } else {
      Cart cart = new Cart();
      cart.setUserId(userId);
      cart.setRestaurantId("");
      String cartId = cartRepositoryService.createCart(cart);
      try {
        cart = cartRepositoryService.findCartByCartId(cartId);
      } catch (CartNotFoundException e) {
        throw new UserNotFoundException("User not found");
      }
      return cart;
    }
  }

  @Override
  public CartModifiedResponse addItemToCart(String itemId, String cartId, String restaurantId)
      throws ItemNotFromSameRestaurantException {
    Item item = null;
    try {
      item = menuService.findItem(itemId, restaurantId);
      return new CartModifiedResponse(cartRepositoryService.addItem(item, cartId,
          restaurantId), 0);
    } catch (ItemNotFoundInRestaurantMenuException e) {
      Cart cart = cartRepositoryService.findCartByCartId(cartId);
      return new CartModifiedResponse(cart, ITEM_NOT_FOUND_IN_RESTAURANT_MENU);
    } catch (CartNotFoundException e) {
      return new CartModifiedResponse(new Cart(), ITEM_NOT_FOUND_IN_RESTAURANT_MENU);
    }
  }

  @Override
  public CartModifiedResponse removeItemFromCart(String itemId, String cartId,
                                                 String restaurantId) {
    CartModifiedResponse cartModifiedResponse;
    try {
      Item item = menuService.findItem(itemId, restaurantId);
      cartModifiedResponse = new CartModifiedResponse(cartRepositoryService.removeItem(item, cartId,
          restaurantId), 0);
    } catch (ItemNotFoundInRestaurantMenuException e) {
      try {
        cartModifiedResponse =
            new CartModifiedResponse(cartRepositoryService.findCartByCartId(cartId), 0);
      } catch (CartNotFoundException e2) {
        cartModifiedResponse = new CartModifiedResponse(new Cart(), 0);
      }
    } catch (CartNotFoundException e) {
      cartModifiedResponse = new CartModifiedResponse(new Cart(), 0);
    }
    return cartModifiedResponse;
  }

  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    try {
      Cart cart = cartRepositoryService.findCartByCartId(cartId);
      if (cart.getItems().isEmpty()) {
        throw new EmptyCartException("Cart is empty");
      }
      return orderRepositoryService.placeOrder(cart);
    } catch (CartNotFoundException e) {
      throw new EmptyCartException("Cart doesn't exist");
    }
  }
}
