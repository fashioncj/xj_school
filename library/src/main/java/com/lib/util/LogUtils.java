package com.lib.util;

import android.util.Log;

public final class LogUtils {
  private static boolean isDebugEnable = true;

  public static void log(String tag, String msg) {
    if (isDebugEnable && msg != null) {
      Log.e(tag, msg);
    }
  }

  public static void log(String tag, String msg, Throwable ex) {
    if (isDebugEnable) Log.e(tag, msg, ex);
  }
}
