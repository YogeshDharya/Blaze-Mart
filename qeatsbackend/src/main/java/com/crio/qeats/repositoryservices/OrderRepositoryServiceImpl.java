/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.models.CartEntity;
import com.crio.qeats.models.OrderEntity;
import com.crio.qeats.repositories.CartRepository;
import com.crio.qeats.repositories.OrderRepository;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class OrderRepositoryServiceImpl implements OrderRepositoryService {

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private CartRepository cartRepository;

  @Override
  public Order placeOrder(Cart cart) {
    ModelMapper mapper = modelMapperProvider.get();
    OrderEntity orderEntity = mapper.map(cart, OrderEntity.class);
    orderEntity.setPlacedTime(LocalTime.now().toString());
    Order order = mapper.map(orderRepository.insert(orderEntity), Order.class);
    CartEntity cartEntity = mapper.map(cart, CartEntity.class);
    cartEntity.clearCart();
    cartRepository.save(cartEntity);
    return order;
  }
}