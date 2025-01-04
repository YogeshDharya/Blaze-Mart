package com.repositoryservices;

import com.dto.Cart;
import com.dto.Item;
import com.exceptions.CartNotFoundException;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface CartRepositoryService {

  @NonNull String createCart(Cart cart);

  Optional<Cart> findCartByUserId(String userId);

  Cart findCartByCartId(String cartId) throws CartNotFoundException;

  Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException;

  Cart removeItem(Item item, String cartId, String restaurantId) throws CartNotFoundException;

}
