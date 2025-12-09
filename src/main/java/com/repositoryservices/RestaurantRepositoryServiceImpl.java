package com.repositoryservices;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

import ch.hsr.geohash.GeoHash;
import com.dto.Restaurant;
import com.globals.GlobalConstants;
import com.models.RestaurantEntity;
import com.repositories.RestaurantRepository;
import com.utils.GeoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@Slf4j
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @PostConstruct
  private void init() {
    GlobalConstants.initCache();
  }

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
                                                    Double longitude, LocalTime currentTime,
                                                    Double servingRadiusInKms) {

    if (GlobalConstants.isCacheAvailable()) {
      return findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime,
          servingRadiusInKms);
    } else {
      return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
    }
  }

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude, Double longitude,
                                                         LocalTime currentTime,
                                                         Double servingRadiusInKms) {
    List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
    List<Restaurant> restaurants = new ArrayList<>();
    log.info("Restaurants received: {}", restaurantEntities.size());
    for (RestaurantEntity restaurantEntity : restaurantEntities) {
      if (isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude,
          servingRadiusInKms)) {
        restaurants.add(modelMapperProvider.get().map(restaurantEntity, Restaurant.class));
      }
    }
    return restaurants;
  }

  private List<Restaurant> findAllRestaurantsCloseByFromCache(Double latitude, Double longitude,
                                                              LocalTime currentTime,
                                                              Double servingRadiusInKms) {
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

  @Override
  public List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
                                                String searchString, LocalTime currentTime,
                                                Double servingRadiusInKms) {
    final String regexExact = String.format("^%s$", searchString);
    final String regexAll = String.format(".*%s.*", searchString);

    LinkedHashSet<RestaurantEntity> restaurantEntityLinkedHashSet = new LinkedHashSet<>();
    Query queryExact = new Query(Criteria.where("name").regex(regexExact, "i"));
    Query queryRestMatches = new Query(Criteria.where("name").regex(regexAll, "i"));


    restaurantEntityLinkedHashSet.addAll(mongoTemplate.find(queryExact, RestaurantEntity.class));
    restaurantEntityLinkedHashSet.addAll(mongoTemplate.find(queryRestMatches,
        RestaurantEntity.class));

    List<Restaurant> restaurants = new ArrayList<>();
    restaurantEntityLinkedHashSet.forEach(restaurantEntity -> {
      if (isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude,
          servingRadiusInKms)) {
        restaurants.add(modelMapperProvider.get().map(restaurantEntity, Restaurant.class));
      }
    });
    return restaurants;
  }

  @Override
  public List<Restaurant> findRestaurantsByAttributes(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
    final String regexAll = String.format(".*%s.*", searchString);
    Query queryRestMatches = new Query();
    queryRestMatches.addCriteria(Criteria.where("attributes").in(regexAll, "i"));
    List<RestaurantEntity> restaurantEntities =
        new ArrayList<>(mongoTemplate.find(queryRestMatches, RestaurantEntity.class));

    return filterRestaurantEntities(restaurantEntities, currentTime, latitude, longitude,
        servingRadiusInKms);
  }

  @Override
  public List<Restaurant> findRestaurantsByItemName(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {


    return new ArrayList<>();
  }

  @Override
  public List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
                                                          String searchString,
                                                          LocalTime currentTime,
                                                          Double servingRadiusInKms) {

    return new ArrayList<>();
  }

  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   *
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
                                             LocalTime currentTime, Double latitude,
                                             Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return GeoUtils.findDistanceInKm(latitude, longitude,
          restaurantEntity.getLatitude(), restaurantEntity.getLongitude())
          < servingRadiusInKms;
    }

    return false;
  }


  private List<Restaurant> filterRestaurantEntities(List<RestaurantEntity> restaurantEntities,
                                                    LocalTime currentTime, Double latitude,
                                                    Double longitude, Double servingRadiusInKms) {
    List<Restaurant> restaurants = new ArrayList<>();
    restaurantEntities.forEach(restaurantEntity -> {
      if (isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude,
          servingRadiusInKms)) {
        restaurants.add(modelMapperProvider.get().map(restaurantEntity, Restaurant.class));
      }
    });
    return restaurants;
  }
}
