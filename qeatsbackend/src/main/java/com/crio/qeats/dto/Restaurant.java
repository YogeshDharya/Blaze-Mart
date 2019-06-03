/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

// COMPLETED: CRIO_TASK_MODULE_SERIALIZATION - Implement Restaurant class.
// Complete the class such that it produces the following JSON during serialization.
// {
//  "restaurantId": "10",
//  "name": "A2B",
//  "city": "Hsr Layout",
//  "imageUrl": "www.google.com",
//  "latitude": 20.027,
//  "longitude": 30.0,
//  "opensAt": "18:00",
//  "closesAt": "23:00",
//  "attributes": [
//    "Tamil",
//    "South Indian"
//  ]
// }
public class Restaurant {

  @JsonProperty(value = "restaurantId")
  String restaurantId;

  @JsonProperty(value = "name")
  String name;

  @JsonProperty(value = "city")
  String city;

  @JsonProperty(value = "imageUrl")
  String imageUrl;

  @JsonProperty(value = "latitude")
  double latitude;

  @JsonProperty(value = "longitude")
  double longitude;

  @JsonProperty(value = "opensAt")
  String opensAt;

  @JsonProperty(value = "closesAt")
  String closesAt;

  @JsonProperty(value = "attributes")
  ArrayList<String> attributes;
}

