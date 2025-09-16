package com.exceptions;

public class ItemNotFromSameRestaurantException extends blazemartException {

  public ItemNotFromSameRestaurantException() {
  }

  public ItemNotFromSameRestaurantException(String message) {
    super(message);
  }

  @Override
  public int getErrorType() {
    return ITEM_NOT_FROM_SAME_RESTAURANT;
  }
}
