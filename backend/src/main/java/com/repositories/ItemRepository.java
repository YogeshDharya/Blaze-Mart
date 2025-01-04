package com.repositories;

import com.models.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<ItemEntity, String> {

}
