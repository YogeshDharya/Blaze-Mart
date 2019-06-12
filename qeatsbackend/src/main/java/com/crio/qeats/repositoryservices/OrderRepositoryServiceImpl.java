/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderRepositoryServiceImpl implements OrderRepositoryService {
  @Override
  public Order placeOrder(Cart cart) {
    return null;
  }
}