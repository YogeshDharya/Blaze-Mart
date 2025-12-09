package com.messaging;

import com.dto.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class DeliveryBoyAssigner implements PostOrderActions {
  static int called = 0;


  @Override
  public void execute(Order order) {
    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.info("Assigning Delivery boy for order id " + order.getId());
    assignDeliveryBoy();
  }

  private void assign(String deliveryBoyName) {
    log.info("Delivery " + deliveryBoyName + " will deliver your order in 31 minutes");
  }

  private boolean possibleToCombineDeliveries() {

    if (called % 60 == 59) {
      log.info("Combining Deliveries");
      return true;
    }
    ++called;

    return false;
  }

  private void assignDeliveryBoy() {
    try {
      boolean assigned = false;
      int secondsToWaitToCombineDeliveries = 5;
      do {
        Thread.sleep(1000);
        assigned = possibleToCombineDeliveries();
      } while (!assigned && (secondsToWaitToCombineDeliveries-- > 0));

      if (!assigned) {
        assign("Raju");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
