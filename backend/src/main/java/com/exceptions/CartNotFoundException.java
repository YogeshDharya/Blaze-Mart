package com.exceptions;

public class CartNotFoundException extends QEatsException {

  @Override
  public int getErrorType() {
    return CART_NOT_FOUND;
  }
}
