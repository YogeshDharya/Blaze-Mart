package com.services;

import com.dto.Item;
import com.exceptions.ItemNotFoundInRestaurantMenuException;
import com.exchanges.GetMenuResponse;

public interface MenuService {

  /**
   * Return the restaurant menu.
   * @param restaurantId id of the restaurant
   * @return the restaurant's menu
   */
  GetMenuResponse findMenu(String restaurantId);

  /**
   * Find the item in the restaurant using restaurantId/itemId and return the item if found.
   * @param itemId id of the item
   * @param restaurantId id of the restaurant
   * @return item if found
   * @exception ItemNotFoundInRestaurantMenuException if the item is not found
   */
  Item findItem(String itemId, String restaurantId) throws ItemNotFoundInRestaurantMenuException;
}
