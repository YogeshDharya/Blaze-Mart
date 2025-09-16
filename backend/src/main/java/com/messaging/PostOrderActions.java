package com.messaging;

import com.crio.blazemart.dto.Order;

public interface PostOrderActions {
  void execute(Order order);
}
