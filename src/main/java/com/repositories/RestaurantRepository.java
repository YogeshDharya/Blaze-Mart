package com.repositories;

import com.models.RestaurantEntity;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRepository extends MongoRepository<RestaurantEntity, String> {

  @Override
  List<RestaurantEntity> findAll();
}

