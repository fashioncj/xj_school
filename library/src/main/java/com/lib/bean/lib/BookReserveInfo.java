package com.lib.bean.lib;

import java.util.List;

/**
 * 图书预约信息
 * 
 * @author Administrator
 * 
 */
public class BookReserveInfo {

  // I775.45/H13
  private String callNo;

  // callno1
  private String hiddenCallNoName;

  // I775.45/H13
  private String hiddenCallNoValue;

  // 中文系
  private String location;

  // location1
  private String hiddenLocationName;

  // 101
  private String hidenLocationValue;

  // 取书地点
  private List<GetBookLoction> getBookLocation;

  // 是否可以预约
  private String canReserve;


  public String getCanReserve() {
    return canReserve;
  }

  public void setCanReserve(String canReserve) {
    this.canReserve = canReserve;
  }

  public String getTxt() {
    return this.callNo + " " + this.getLocation();
  }

  public String getCallNo() {
    return callNo;
  }

  public void setCallNo(String callNo) {
    this.callNo = callNo;
  }

  public String getHiddenCallNoName() {
    return hiddenCallNoName;
  }

  public void setHiddenCallNoName(String hiddenCallNoName) {
    this.hiddenCallNoName = hiddenCallNoName;
  }

  public String getHiddenCallNoValue() {
    return hiddenCallNoValue;
  }

  public void setHiddenCallNoValue(String hiddenCallNoValue) {
    this.hiddenCallNoValue = hiddenCallNoValue;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getHiddenLocationName() {
    return hiddenLocationName;
  }

  public void setHiddenLocationName(String hiddenLocationName) {
    this.hiddenLocationName = hiddenLocationName;
  }

  public String getHidenLocationValue() {
    return hidenLocationValue;
  }

  public void setHidenLocationValue(String hidenLocationValue) {
    this.hidenLocationValue = hidenLocationValue;
  }

  public List<GetBookLoction> getGetBookLocation() {
    return getBookLocation;
  }

  public void setGetBookLocation(List<GetBookLoction> getBookLocation) {
    this.getBookLocation = getBookLocation;
  }


}
