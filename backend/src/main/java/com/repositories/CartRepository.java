package com.repositories;

import com.models.CartEntity;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<CartEntity, String> {

  Optional<CartEntity> findCartByUserId(String userId);

  Optional<CartEntity> findCartById(String cartId);

}
