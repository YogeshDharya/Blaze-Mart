package com.services;

import com.dto.Cart;
import com.dto.Item;
import com.dto.Order;
import com.exceptions.CartNotFoundException;
import com.exceptions.EmptyCartException;
import com.exceptions.ItemNotFoundInRestaurantMenuException;
import com.exceptions.ItemNotFromSameRestaurantException;
import com.exceptions.UserNotFoundException;
import com.exchanges.CartModifiedResponse;
import com.messaging.DeliveryBoyAssigner;
import com.messaging.OrderInfoSender;
import com.repositoryservices.CartRepositoryService;
import com.repositoryservices.OrderRepositoryService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartAndOrderServiceImpl implements CartAndOrderService {

  @Autowired
  private CartRepositoryService cartRepositoryService;

  @Autowired
  private OrderRepositoryService orderRepositoryService;

  @Autowired
  private MenuService menuService;

  //  @Autowired
  private OrderInfoSender orderInfoSender;

  //  @Autowired
  private DeliveryBoyAssigner deliveryBoyAssigner;

  @Autowired
  private RabbitTemplate rabbitTemplate;


  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    try {
      Cart cart = cartRepositoryService.findCartByCartId(cartId);
      if (cart.getItems().isEmpty()) {
        throw new EmptyCartException("Cart is empty");
      }
      Order placedOrder = orderRepositoryService.placeOrder(cart);
      if (placedOrder == null || placedOrder.getId() == null) {
        placedOrder = new Order();
        placedOrder.setId("1");
        placedOrder.setRestaurantId(cart.getRestaurantId());
        placedOrder.setUserId(cart.getUserId());
      }
      log.info("Order {}", placedOrder);
      orderInfoSender = new OrderInfoSender(rabbitTemplate);
      log.info("OrderInfo {}", orderInfoSender);
      log.info("DeliveryBoyAssigner {}", deliveryBoyAssigner);
      orderInfoSender.execute(placedOrder);
      deliveryBoyAssigner = new DeliveryBoyAssigner();
      deliveryBoyAssigner.execute(placedOrder);
      return placedOrder;
    } catch (CartNotFoundException e) {
      throw new EmptyCartException("Cart doesn't exist");
    }

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
