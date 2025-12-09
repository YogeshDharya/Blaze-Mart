package com.exchanges;

import com.dto.Menu;
//import javax.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMenuResponse {
  @NotNull
  Menu menu;
}
