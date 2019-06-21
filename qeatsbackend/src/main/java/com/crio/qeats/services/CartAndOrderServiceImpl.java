package com.crio.qeats.services;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exceptions.UserNotFoundException;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.messaging.DeliveryBoyAssigner;
import com.crio.qeats.messaging.OrderInfoSender;
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

  @Autowired
  private OrderInfoSender orderInfoSender;

  @Autowired
  private DeliveryBoyAssigner deliveryBoyAssigner;


  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    Cart cart = cartRepositoryService.findCartByCartId(cartId);
    Order placedOrder = orderRepositoryService.placeOrder(cart);

    // COMPLETED: CRIO_TASK_MODULE_RABBITMQ - Implement postorder actions asynchronously.
    // After the order is placed you have to do 2 actions
    // 1). Send order information over email  - orderInfoSender.execute(placedOrder)
    // 2). Assign a delivery boy against the order - deliveryBoyAssigner.execute(placedOrder)
    // Both these functions are called synchronously in the stubs given for this module.
    // Synchronous execution of post order actions results in high user latency.
    // Your job is to address this problem by making the post order actions asynchronous
    // using RabbitMQ.

    orderInfoSender.execute(placedOrder);
    deliveryBoyAssigner.execute(placedOrder);

    return placedOrder;
  }

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
    Cart cart;
    CartModifiedResponse cartModifiedResponse;

    try {
      cart = cartRepositoryService.findCartByCartId(cartId);
    } catch (CartNotFoundException e) {
      cart = new Cart();
      cart.setRestaurantId(restaurantId);
      cart.setId(cartId);
      cartRepositoryService.createCart(cart);
    }
    Item item = menuService.findItem(itemId, restaurantId);

    if (cart.getRestaurantId().equals(restaurantId)) {
      cart = cartRepositoryService.addItem(item, cartId, restaurantId);
      cartModifiedResponse = new CartModifiedResponse(cart, 0);
    } else {
      cartModifiedResponse = new CartModifiedResponse(cart, 102);
    }
    return cartModifiedResponse;
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
}
