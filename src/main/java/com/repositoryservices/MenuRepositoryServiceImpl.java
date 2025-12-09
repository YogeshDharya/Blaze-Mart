package com.repositoryservices;

import com.dto.Menu;
import com.models.MenuEntity;
import com.repositories.MenuRepository;

import java.util.Optional;

import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuRepositoryServiceImpl implements MenuRepositoryService {

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  public Menu findMenu(String restaurantId) {
    ModelMapper modelMapper = modelMapperProvider.get();

    Optional<MenuEntity> menuById = menuRepository.findMenuByRestaurantId(restaurantId);

    Menu menu = null;

    if (menuById.isPresent()) {
      menu = modelMapper.map(menuById.get(), Menu.class);
    }

    return menu;
  }
}
