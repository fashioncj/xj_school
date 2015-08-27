package com.lib.activity.news;

/**
 * 新闻实体类
 * 
 * 
 */
public class News {

  private String title; // 标题
  private String time; // 时间
  private String url; // url
  
  private String imageUrl;
  private String summer;
  

  public String getSummer() {
    return summer;
  }

  public void setSummer(String summer) {
    this.summer = summer;
  }

  public News(String title, String time) {
    this.title = title;
    this.time = time;
  }

  public News(String title, String time, String url) {
    this.title = title;
    this.time = time;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public String toString() {
    return "News [title=" + title + ", time=" + time + ", url=" + url + ", imageUrl=" + imageUrl
        + ", summer=" + summer + "]";
  }


}
