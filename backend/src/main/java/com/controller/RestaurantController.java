package com.controller;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exchanges.AddCartRequest;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.exchanges.DeleteCartRequest;
import com.crio.qeats.exchanges.GetCartRequest;
import com.crio.qeats.exchanges.GetMenuResponse;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.exchanges.PostOrderRequest;
import com.crio.qeats.services.CartAndOrderService;
import com.crio.qeats.services.MenuService;
import com.crio.qeats.services.RestaurantService;
import java.time.LocalTime;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping(RestaurantController.RESTAURANT_API_ENDPOINT)
public class RestaurantController {

  public static final String RESTAURANT_API_ENDPOINT = "/qeats/v1";
  public static final String RESTAURANTS_API = "/restaurants";
  public static final String MENU_API = "/menu";
  public static final String CART_API = "/cart";
  public static final String CART_ITEM_API = "/cart/item";
  public static final String CART_CLEAR_API = "/cart/clear";
  public static final String POST_ORDER_API = "/order"; 
  public static final String GET_ORDERS_API = "/orders";

  @Autowired
  private RestaurantService restaurantService;

  @Autowired
  private MenuService menuService;

  @Autowired
  private CartAndOrderService cartAndOrderService;

@GetMapping(RESTAURANTS_API)
public ResponseEntity<GetRestaurantsResponse> getRestaurants(
      GetRestaurantsRequest getRestaurantsRequest) {
    log.info("getRestaurants called with {}", getRestaurantsRequest);
    if (!getRestaurantsRequest.isValid()) {
      return ResponseEntity.badRequest().build();
    }

    GetRestaurantsResponse getRestaurantsResponse;
    final LocalTime now = LocalTime.now();

    if (getRestaurantsRequest.getSearchFor() != null) {
      getRestaurantsResponse =
          restaurantService.findRestaurantsBySearchQuery(getRestaurantsRequest, now);
    } else {
      getRestaurantsResponse = restaurantService
          .findAllRestaurantsCloseBy(getRestaurantsRequest, now);
    }
    log.info("getRestaurants returned {}", getRestaurantsResponse);

    return ResponseEntity.ok().body(getRestaurantsResponse);
  }


  @GetMapping(MENU_API)
  public ResponseEntity<GetMenuResponse> getMenu(
      @RequestParam("restaurantId") Optional<String> restaurantId) {
    log.info("getMenu parameter {}", restaurantId);
    if (!restaurantId.isPresent() || StringUtils.isEmpty(restaurantId.get())) {
      return ResponseEntity.badRequest().build();
    }

    GetMenuResponse getMenuResponse = menuService.findMenu(restaurantId.get());

    log.info("getMenu returned with {}", getMenuResponse);

    if (getMenuResponse == null || getMenuResponse.getMenu() == null) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().body(getMenuResponse);
  }


  @GetMapping(CART_API)
  public ResponseEntity<Cart> getCart(GetCartRequest getCartRequest) {
    log.info("getCart {}", getCartRequest);
    if (StringUtils.isEmpty(getCartRequest.getUserId())) {
      return ResponseEntity.badRequest().build();
    }

    Cart cart;
    try {
      cart = cartAndOrderService.findOrCreateCart(getCartRequest.getUserId());
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(cart);
  }



  @PostMapping(CART_ITEM_API)
  public ResponseEntity<CartModifiedResponse> addItem(@RequestBody AddCartRequest addCartRequest) {
    log.info("addItem {}", addCartRequest);
    if (StringUtils.isEmpty(!addCartRequest.isValidRequest())) {
      return ResponseEntity.badRequest().build();
    }


    try {
      String itemId = addCartRequest.getItemId();
      String restaurantId = addCartRequest.getRestaurantId();
      String cartId = addCartRequest.getCartId();
      CartModifiedResponse cart = cartAndOrderService.addItemToCart(itemId, cartId, restaurantId);

      return ResponseEntity.ok(cart);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }


  }


  @DeleteMapping(CART_ITEM_API)
  public ResponseEntity<CartModifiedResponse> deleteItem(
      @RequestBody DeleteCartRequest deleteCartRequest) {
    log.info("deleteItem {}", deleteCartRequest);
    if (StringUtils.isEmpty(!deleteCartRequest.isValidRequest())) {
      return ResponseEntity.badRequest().build();
    }

    String itemId = deleteCartRequest.getItemId();
    String restaurantId = deleteCartRequest.getRestaurantId();
    String cartId = deleteCartRequest.getCartId();

    CartModifiedResponse response = cartAndOrderService.removeItemFromCart(itemId, cartId,
        restaurantId);

    return ResponseEntity.ok(response);
  }


  @PostMapping(POST_ORDER_API)
  public ResponseEntity<Order> placeOrder(@RequestBody PostOrderRequest postOrderRequest) {
    log.info("placeOrder {}", postOrderRequest);
    if (!postOrderRequest.isValidRequest()) {
      return ResponseEntity.badRequest().build();
    }

    try {
      return ResponseEntity.ok(cartAndOrderService.postOrder(postOrderRequest.getCartId()));
    } catch (EmptyCartException e) {
      return ResponseEntity.badRequest().build();
    }
  }


}
