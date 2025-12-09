package com.messaging;

import com.dto.Order;

public interface PostOrderActions {
  void execute(Order order);
}
