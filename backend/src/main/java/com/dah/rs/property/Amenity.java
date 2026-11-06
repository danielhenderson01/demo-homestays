package com.dah.rs.property;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Amenity {
  WIFI("WiFi"),
  KITCHEN("Kitchen"),
  LAUNDRY("Laundry"),
  FREE_PARKING("Free Parking"),
  POOL("Pool"),
  HOT_TUB("Hot Tub"),
  ELEVATOR("Elevator"),
  DISHWASHER("Dishwasher"),
  SMART_TV("Smart TV"),
  OTHER("Other");

  private final String value;

  Amenity(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
