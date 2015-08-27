package com.lib.util;

import com.andybrier.lib.R;

import android.app.AlertDialog;
import android.content.Context;

public class AlertUtils {


  public static void alert(Context context, String title, String content) {
    AlertDialog localAlertDialog =
        new AlertDialog.Builder(context).setTitle(title).setMessage(content)
            .setPositiveButton(context.getString(R.string.ok), new NullClickListener()).create();
    try {
      localAlertDialog.show();
    } catch (Exception localException) {
      localException.printStackTrace();
    }
  }


}
