package com.lib.activity.room;

import com.lib.util.LogUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public abstract class BaseRoomActivity extends Activity {
  updaterBroadcastReceiver r;

  boolean isReg = false;

  @Override
  public void onResume() {
    IntentFilter filter = new IntentFilter("com.lib.LibraryApplication.Refresh.Room");
    r = new updaterBroadcastReceiver();
    registerReceiver(r, filter);

    isReg = true;
    super.onResume();
  }

  class updaterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      getAdapter().notifyDataSetChanged();
    }
  }

  @Override
  protected void onDestroy() {

    if (isReg) {
      unregisterReceiver(r);
      LogUtils.log("Unregreceiver", r.toString());
    }
    super.onDestroy();
  }

  abstract SampleTableAdapter getAdapter();
}
