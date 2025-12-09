package com.exceptions;

public class CartNotFoundException extends BlazeMartException {

  @Override
  public int getErrorType() {
    return CART_NOT_FOUND;
  }
}
