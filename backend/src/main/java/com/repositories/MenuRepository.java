package com.repositories;

import com.models.MenuEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuRepository extends MongoRepository<MenuEntity, String> {

  Optional<MenuEntity> findMenuByRestaurantId(String restaurantId);

  Optional<List<MenuEntity>> findMenusByItemsItemIdIn(List<String> itemIdList);

}
