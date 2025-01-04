package com.exceptions;

import com.exceptions.BlazeMart;

public class UserNotFoundException extends BlazeMart {
  public UserNotFoundException(String message) {
    super(message);
  }

  @Override
  public int getErrorType() {
    return USER_NOT_FOUND;
  }
}
