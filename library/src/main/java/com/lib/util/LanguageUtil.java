package com.lib.util;

import java.util.Locale;

public class LanguageUtil {

  public static boolean isChinese() {
    String lan = Locale.getDefault().getLanguage();

    if (lan.equals("zh")) {
      return true;
    } else {
      return false;
    }
  }
}
