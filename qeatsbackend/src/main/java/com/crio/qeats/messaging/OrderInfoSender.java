package com.crio.qeats.messaging;


import com.crio.qeats.dto.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OrderInfoSender implements PostOrderActions {

  @Override
  public void execute(Order order) {
    log.info("Sending Order Information for Order id " + order.getId());
    // Send email from here.
  }

}
