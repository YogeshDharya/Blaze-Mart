package com.crio.qeats.messaging;

import com.crio.qeats.dto.Order;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.OrderMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OrderInfoSender implements PostOrderActions {

  private final RabbitTemplate rabbitTemplate;

  public OrderInfoSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void execute(Order order) {
    log.info("Sending Order Information for Order id " + order.getId());
    OrderMessage message = new OrderMessage("Sending Order Information for Order id "
        + order.getId());
    rabbitTemplate.convertAndSend(GlobalConstants.EXCHANGE_NAME,
        GlobalConstants.ROUTING_KEY, message);
  }

}
