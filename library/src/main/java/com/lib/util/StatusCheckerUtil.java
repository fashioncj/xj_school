package com.lib.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;

public class StatusCheckerUtil {

  private static boolean isLogin(Activity activity) {
    // 先登录
    LibraryApplication mApp = (LibraryApplication) activity.getApplication();
    boolean loginStatus = mApp.getLoginStatus();
    if (loginStatus != true) {
      return false;
    }
    return true;

  }


  public static boolean isNetworkOK(Activity activity) {
    boolean isNetOK = false;
    final ConnectivityManager conMgr =
        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
    if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
      isNetOK = true;
    }
    return isNetOK;
  }

  public static String checkBoth(Activity activity) {
    if (!isNetworkOK(activity)) {
      return activity.getApplicationContext().getString(R.string.check_net);
    } else if (!isLogin(activity)) {
      return activity.getApplicationContext().getString(R.string.please_login);
    }
    else {
      return null;
    }
  }
}
