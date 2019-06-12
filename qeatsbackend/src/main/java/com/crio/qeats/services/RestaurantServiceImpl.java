/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;


  // COMPLETED: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
    double currentLat = getRestaurantsRequest.getLatitude();
    double currentLong = getRestaurantsRequest.getLongitude();

    Double servingRange = normalHoursServingRadiusInKms;
    if (isPeekHour(currentTime)) {
      servingRange = peakHoursServingRadiusInKms;
    }

    List<Restaurant> restaurants = restaurantRepositoryService
        .findAllRestaurantsCloseBy(currentLat, currentLong, currentTime, servingRange);
    return new GetRestaurantsResponse(restaurants);
  }

  // COMPLETED: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Implement findRestaurantsBySearchQuery. The request object has the search string.
  // We have to combine results from multiple sources:
  // 1. Restaurants by name (exact and inexact)
  // 2. Restaurants by cuisines (also called attributes)
  // 3. Restaurants by food items it serves
  // 4. Restaurants by food item attributes (spicy, sweet, etc)
  // Remember, a restaurant must be present only once in the resulting list.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findRestaurantsBySearchQuery(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
    if (!getRestaurantsRequest.hasSearchQuery()) {
      return findAllRestaurantsCloseBy(getRestaurantsRequest, currentTime);
    }

    List<Restaurant> restaurants = new ArrayList<>();
    final Double latitude = getRestaurantsRequest.getLatitude();
    final Double longitude = getRestaurantsRequest.getLongitude();
    final String searchQuery = getRestaurantsRequest.getSearchFor();
    Double servingRadiusInKms = normalHoursServingRadiusInKms;
    if (isPeekHour(currentTime)) {
      servingRadiusInKms = peakHoursServingRadiusInKms;
    }

    // Restaurants by Name
    restaurants.addAll(restaurantRepositoryService.findRestaurantsByName(latitude, longitude,
        searchQuery, currentTime, servingRadiusInKms));

    // Restaurants by Cuisines (Attributes)
    restaurants.addAll(restaurantRepositoryService.findRestaurantsByAttributes(latitude,
        longitude, searchQuery, currentTime, servingRadiusInKms));

    // Restaurants by Food Item
    restaurants.addAll(restaurantRepositoryService.findRestaurantsByItemName(latitude,
        longitude, searchQuery, currentTime, servingRadiusInKms));

    // Restaurants by Food Item Attributes
    restaurants.addAll(restaurantRepositoryService.findRestaurantsByItemAttributes(latitude,
        longitude, searchQuery, currentTime, servingRadiusInKms));
    return new GetRestaurantsResponse(restaurants);
  }

  private boolean isPeekHour(LocalTime currentTime) {
    LocalTime s1 = LocalTime.of(8, 0);
    LocalTime e1 = LocalTime.of(10, 0);

    LocalTime s2 = LocalTime.of(13, 0);
    LocalTime e2 = LocalTime.of(14, 0);

    LocalTime s3 = LocalTime.of(19, 0);
    LocalTime e3 = LocalTime.of(21, 0);

    return (currentTime.isAfter(s1) && currentTime.isBefore(e1))
        || (currentTime.isAfter(s2) && currentTime.isBefore(e2))
        || (currentTime.isAfter(s3) && currentTime.isBefore(e3))
        || currentTime.equals(s1) || currentTime.equals(e1)
        || currentTime.equals(s2) || currentTime.equals(e2)
        || currentTime.equals(s3) || currentTime.equals(e3);
  }
}
