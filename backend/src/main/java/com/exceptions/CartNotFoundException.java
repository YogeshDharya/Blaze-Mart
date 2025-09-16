package com.exceptions;

public class CartNotFoundException extends blazemartException {

  @Override
  public int getErrorType() {
    return CART_NOT_FOUND;
  }
}
