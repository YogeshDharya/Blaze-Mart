/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.models.CartEntity;
import com.crio.qeats.repositories.CartRepository;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartRepositoryServiceImpl implements CartRepositoryService {

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Override
  public String createCart(Cart cart) {
    return null;
  }

  @Override
  public Optional<Cart> findCartByUserId(String userId) {
    ModelMapper mapper = modelMapperProvider.get();

    Optional<CartEntity> cartByUserId = cartRepository.findCartByUserId(userId);

    if (cartByUserId.isPresent()) {
      Cart cart = mapper.map(cartByUserId.get(), Cart.class);
      return Optional.of(cart);
    }

    return Optional.empty();
  }

  @Override
  public Cart findCartByCartId(String cartId) throws CartNotFoundException {
    ModelMapper mapper = modelMapperProvider.get();

    Optional<CartEntity> cartById = cartRepository.findCartById(cartId);

    if (cartById.isPresent()) {
      return mapper.map(cartById.get(), Cart.class);
    }
    throw new CartNotFoundException();
  }

  @Override
  public Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
    ModelMapper mapper = modelMapperProvider.get();
    Optional<CartEntity> cartById = cartRepository.findCartById(cartId);

    if (cartById.isPresent()) {
      cartById.get().addItem(item);
      CartEntity cartEntity = cartRepository.save(cartById.get());
      return mapper.map(cartEntity, Cart.class);
    }

    throw new CartNotFoundException();
  }

  @Override
  public Cart removeItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
    ModelMapper mapper = modelMapperProvider.get();
    Optional<CartEntity> cartById = cartRepository.findCartById(cartId);

    if (cartById.isPresent()) {
      cartById.get().removeItem(item);
      CartEntity cartEntity = cartRepository.save(cartById.get());
      return mapper.map(cartEntity, Cart.class);
    }

    throw new CartNotFoundException();
  }
}