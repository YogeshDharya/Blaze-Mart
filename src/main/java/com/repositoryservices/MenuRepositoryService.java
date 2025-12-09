package com.repositoryservices;

import com.dto.Menu;
import org.springframework.stereotype.Service;

@Service
public interface MenuRepositoryService {

  /**
   * Return the restaurant menu.
   * @param restaurantId id of the restaurant
   * @return the restaurant's menu
   */
  Menu findMenu(String restaurantId);

}
