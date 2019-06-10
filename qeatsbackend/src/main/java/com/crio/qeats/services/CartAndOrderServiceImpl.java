package com.crio.qeats.services;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.messaging.DeliveryBoyAssigner;
import com.crio.qeats.messaging.OrderInfoSender;
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


  @Autowired
  private OrderInfoSender orderInfoSender;

  @Autowired
  private DeliveryBoyAssigner deliveryBoyAssigner;


  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    Cart cart = cartRepositoryService.findCartByCartId(cartId);
    Order placedOrder = orderRepositoryService.placeOrder(cart);

    // TODO: CRIO_TASK_MODULE_RABBITMQ - Implement postorder actions asynchronously.
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

}
