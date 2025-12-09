package com.exchanges;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@NoArgsConstructor
@Slf4j
public class GetRestaurantsRequest {

  Double latitude;
  Double longitude;
  String searchFor;

  public GetRestaurantsRequest(Double latitude, Double longitude, String searchFor) {
    log.info("GetRestaurantsRequest AllArgs {} ", searchFor);
    this.latitude = latitude;
    this.longitude = longitude;
    this.searchFor = searchFor;
  }

  public GetRestaurantsRequest(Double latitude, Double longitude) {
    log.info("GetRestaurantsRequest Lat,Long");
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public boolean isValid() {
    if (this.latitude == null || this.longitude == null) {
      return false;
    }

    return this.latitude >= 0 && this.latitude <= 90 && this.longitude >= 0
        && this.longitude <= 180;
  }

  public boolean hasSearchQuery() {
    if (searchFor == null) {
      return false;
    }
    return !searchFor.equals("");
  }
}
