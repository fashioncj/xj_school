package com.lib.activity.exception;

public class BookReservationException  extends Exception{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final String msg;

  public BookReservationException(String msg) {
    super();
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
  
}
