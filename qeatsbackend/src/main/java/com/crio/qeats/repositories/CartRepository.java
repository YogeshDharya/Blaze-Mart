/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositories;

import com.crio.qeats.models.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<CartEntity, String> {

  Optional<CartEntity> findCartByUserId(String userId);

  Optional<CartEntity> findCartById(String cartId);

}
