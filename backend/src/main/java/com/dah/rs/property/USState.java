package com.dah.rs.property;

import com.fasterxml.jackson.annotation.JsonValue;

public enum USState {

  ALABAMA("Alabama"),
  ALASKA("Alaska"),
  ARIZONA("Arizona"),
  ARKANSAS("Arkansas"),
  CALIFORNIA("California"),
  COLORADO("Colorado"),
  CONNECTICUT("Connecticut"),
  DELAWARE("Delaware"),
  FLORIDA("Florida"),
  GEORGIA("Georgia"),
  HAWAII("Hawaii"),
  IDAHO("Idaho"),
  ILLINOIS("Illinois"),
  INDIANA("Indiana"),
  IOWA("Iowa"),
  KANSAS("Kansas"),
  KENTUCKY("Kentucky"),
  LOUISIANA("Louisiana"),
  MAINE("Maine"),
  MARYLAND("Maryland"),
  MASSACHUSETTS("Massachusetts"),
  MICHIGAN("Michigan"),
  MINNESOTA("Minnesota"),
  MISSISSIPPI("Mississippi"),
  MISSOURI("Missouri"),
  MONTANA("Montana"),
  NEBRASKA("Nebraska"),
  NEVADA("Nevada"),
  NEW_HAMPSHIRE("New Hampshire"),
  NEW_JERSEY("New Jersey"),
  NEW_MEXICO("New Mexico"),
  NEW_YORK("New York"),
  NORTH_CAROLINA("North Carolina"),
  NORTH_DAKOTA("North Dakota"),
  OHIO("Ohio"),
  OKLAHOMA("Oklahoma"),
  OREGON("Oregon"),
  PENNSYLVANIA("Pennsylvania"),
  RHODE_ISLAND("Rhode Island"),
  SOUTH_CAROLINA("South Carolina"),
  SOUTH_DAKOTA("South Dakota"),
  TENNESSEE("Tennessee"),
  TEXAS("Texas"),
  UTAH("Utah"),
  VERMONT("Vermont"),
  VIRGINIA("Virginia"),
  WASHINGTON("Washington"),
  WEST_VIRGINIA("West Virginia"),
  WISCONSIN("Wisconsin"),
  WYOMING("Wyoming");

  private final String value;

  USState(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

}
