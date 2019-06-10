/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import ch.hsr.geohash.GeoHash;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;
import com.crio.qeats.utils.GeoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@Slf4j
@Primary
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  // COMPLETED: CRIO_TASK_MODULE_REDIS - Add cache support.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
               Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    if (GlobalConstants.isCacheAvailable()) {
      return findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime,
          servingRadiusInKms);
    } else {
      return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
    }
  }

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude, Double longitude,
               LocalTime currentTime, Double servingRadiusInKms) {
    List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
    List<Restaurant> restaurants = new ArrayList<>();
    log.info("Restaurants received: {}", restaurantEntities.size());
    for (RestaurantEntity restaurantEntity : restaurantEntities) {
      if (GeoUtils.findDistanceInKm(latitude, longitude, restaurantEntity.getLatitude(),
          restaurantEntity.getLongitude()) <= servingRadiusInKms
          && isOpenNow(currentTime, restaurantEntity)) {
        restaurants.add(modelMapperProvider.get().map(restaurantEntity, Restaurant.class));
      }
    }
    return restaurants;
  }


  // COMPLETED: CRIO_TASK_MODULE_REDIS - Implement caching.

  /**
   * Implement caching for restaurants closeby.
   * Whenever the entry is not there in the cache, you will have to populate it from DB.
   * If the entry is already available in the cache, then return it from cache to save DB lookup.
   * The cache entries should expire in GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS.
   * Make sure you use something like a GeoHash with a slightly lower precision,
   * so that for lat/long that are slightly close, the function returns the same set of restaurants.
   */
  private List<Restaurant> findAllRestaurantsCloseByFromCache(
      Double latitude, Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 7);
    String key = geoHash.toBase32();
    Jedis jedis = GlobalConstants.getJedisPool().getResource();

    List<Restaurant> restaurants = new ArrayList<>();
    if (jedis.get(key) == null) {
      restaurants = findAllRestaurantsCloseFromDb(latitude, longitude,
          currentTime, servingRadiusInKms);
      try {
        jedis.set(key, objectMapper.writeValueAsString(restaurants));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }

    } else {
      try {
        restaurants = objectMapper.readValue(jedis.get(key), new TypeReference<List<Restaurant>>() {
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return restaurants;
  }
}
