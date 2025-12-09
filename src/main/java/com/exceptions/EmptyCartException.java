package com.exceptions;

public class EmptyCartException extends BlazeMartException {

  public EmptyCartException(String message) {
    super(message);
  }

  @Override
  public int getErrorType() {
    return EMPTY_CART;
  }
}
