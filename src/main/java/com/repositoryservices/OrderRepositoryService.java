package com.repositoryservices;

import com.dto.Cart;
import com.dto.Order;

public interface OrderRepositoryService {

  Order placeOrder(Cart cart);

}
