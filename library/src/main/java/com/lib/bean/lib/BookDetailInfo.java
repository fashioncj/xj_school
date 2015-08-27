package com.lib.bean.lib;

import java.util.List;
import java.util.Map;

/**
 * 图书的详细信息
 * 
 * @author Administrator
 * 
 */
public class BookDetailInfo {

  private String name;
  
  private String auth;


  private String press;

  private String version;

  private String isbn;
  
  private String elec;
  
  



  public String getElec() {
    return elec;
  }

  public void setElec(String elec) {
    this.elec = elec;
  }

  List<Map<String, Object>> locationInfo;

 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuth() {
    return auth;
  }

  public void setAuth(String auth) {
    this.auth = auth;
  }

  public String getPress() {
    return press;
  }

  public void setPress(String press) {
    this.press = press;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public List<Map<String, Object>> getLocationInfo() {
    return locationInfo;
  }

  public void setLocationInfo(List<Map<String, Object>> locationInfo) {
    this.locationInfo = locationInfo;
  }

  @Override
  public String toString() {
    return "BookDetailInfo [name=" + name + ", auth=" + auth + ", press=" + press + ", version="
        + version + ", isbn=" + isbn + ", elec=" + elec + ", locationInfo=" + locationInfo + "]";
  }



}
