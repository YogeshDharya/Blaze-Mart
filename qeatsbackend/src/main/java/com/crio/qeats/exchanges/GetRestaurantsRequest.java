/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.exchanges;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

// COMPLETED: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement GetRestaurantsRequest.
// Complete the class such that it is able to deserialize the incoming query params from
// REST API clients.
// For instance, if a REST client calls API /qeats/v1/restaurants?latitude=21.93&longitude=23.0,
// this class should be able to deserialize lat/long from that.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRestaurantsRequest {

  Double latitude;
  Double longitude;

  public boolean isValid() {
    if (this.latitude == null || this.longitude == null) {
      return false;
    }

    // Range: Latitude [0-90], Longitude [0-180]
    return this.latitude >= 0 && this.latitude <= 90 && this.longitude >= 0
        && this.longitude <= 180;
  }
}
