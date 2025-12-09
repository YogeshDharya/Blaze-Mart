package com.exceptions;

import com.exceptions.BlazeMartException;

public class UserNotFoundException extends BlazeMartException {
  public UserNotFoundException(String message) {
    super(message);
  }

  @Override
  public int getErrorType() {
    return USER_NOT_FOUND;
  }
}
