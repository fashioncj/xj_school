package com.lib.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {


  public static String getNowDate() {
    Date today = new Date();

    SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");

    String result = sFormat.format(today) + "  " + getWeekOfDate(today);

    return result;
  }

  public static String getWeekOfDate(Date dt) {

    String[] zh_weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    String[] en_weekDays =
        {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);
    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0) w = 0;

    if (LanguageUtil.isChinese()) {
      return zh_weekDays[w];
    } else {
      return en_weekDays[w];
    }
  }

}
