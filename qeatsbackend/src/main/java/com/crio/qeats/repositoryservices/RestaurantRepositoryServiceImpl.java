/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;
import com.crio.qeats.utils.GeoUtils;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  // COMPLETED: CRIO_TASK_MODULE_NOSQL - Implement findAllRestaurantsCloseby.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
  }

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude, Double longitude,
      LocalTime currentTime,
      Double servingRadiusInKms) {
    List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
    List<Restaurant> restaurants = new ArrayList<>();
    log.info("Restaurants received: {}", restaurantEntities.size());
    for (RestaurantEntity restaurantEntity : restaurantEntities) {
      if (GeoUtils.findDistanceInKm(latitude, longitude, restaurantEntity.getLatitude(),
          restaurantEntity.getLongitude()) <= servingRadiusInKms) {
        restaurants.add(new Restaurant(
            restaurantEntity.getRestaurantId(),
            restaurantEntity.getName(),
            restaurantEntity.getCity(),
            restaurantEntity.getImageUrl(),
            restaurantEntity.getLatitude(),
            restaurantEntity.getLongitude(),
            restaurantEntity.getOpensAt(),
            restaurantEntity.getClosesAt(),
            restaurantEntity.getAttributes()
        ));
      }
    }

    return restaurants;
  }

}