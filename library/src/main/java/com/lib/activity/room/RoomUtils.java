package com.lib.activity.room;


public class RoomUtils {



  public static String getRoomSatus(String roomName, int column, int morAfterNight) {

    String status = Data.buildAllRoomStatus.get(roomName);

    int index = column;

    if (morAfterNight == 1) {
      index = index + 9;
    } else if (morAfterNight == 2) {
      index = index + 20;
    }

    if (status == null) {
      return "+";
    }
    char value = status.charAt(index);
    if (value == '0') {
      return "+";
    } else {
      return "-";
    }
  }

  public static boolean isRoomBookingAble(String txtview) {

    if (txtview.equals("+")) {
      return true;
    }
    return false;
  }
}
