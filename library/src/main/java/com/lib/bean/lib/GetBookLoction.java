package com.lib.bean.lib;

public class GetBookLoction {

  private String location;
  private String locationCode;
  
  private BookReserveInfo bookReserveInfo;
  
  

  public BookReserveInfo getBookReserveInfo() {
    return bookReserveInfo;
  }

  public void setBookReserveInfo(BookReserveInfo bookReserveInfo) {
    this.bookReserveInfo = bookReserveInfo;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocationCode() {
    return locationCode;
  }

  public void setLocationCode(String locationCode) {
    this.locationCode = locationCode;
  }

  @Override
  public String toString() {
    return this.location;
  }



}
