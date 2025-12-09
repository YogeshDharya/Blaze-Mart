package com.exchanges;

import com.dto.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartModifiedResponse {
  private Cart cart;
  private int cartResponseType;
}
