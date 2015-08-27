package com.lib.activity;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.library.LibraryActivity;
import com.lib.activity.map.LifeIndexActivity;
import com.lib.activity.setting.SettingIndex;

import android.R.bool;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;
import android.view.MotionEvent;

public class IndexActivity extends TabActivity implements OnCheckedChangeListener {

  private AnimationTabHost mTabHost;
  private Intent infoIntent;
  private Intent libIntent;
  private Intent setIntent;

  private GestureDetector gestureDetector;
  View.OnTouchListener gestureListener;
  int currentView = 0;

  RadioButton b0;
  RadioButton b1;
  RadioButton b3;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.maintabs);

    gestureDetector = new GestureDetector(new TabHostTouch());


    LibraryApplication mApp = ((LibraryApplication) getApplicationContext());
    boolean netStatus = checkNetworkInfo();
    mApp.setNetStatus(netStatus);


    this.infoIntent = new Intent(this, NewLifeActivity.class);
    this.libIntent = new Intent(this, NewLibActivity.class);
    this.setIntent = new Intent(this, SettingIndex.class);

    b0 = ((RadioButton) findViewById(R.id.radio_button0));
    b0.setOnCheckedChangeListener(this);
    b1 = ((RadioButton) findViewById(R.id.radio_button2));
    b1.setOnCheckedChangeListener(this);
    b3 = ((RadioButton) findViewById(R.id.radio_button4));
    b3.setOnCheckedChangeListener(this);
    

    setupIntent();

    mTabHost.setOpenAnimation(true);


  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      switch (buttonView.getId()) {
        case R.id.radio_button0: {
          this.mTabHost.setCurrentTabByTag("A_TAB");
          currentView = 0;
          break;
        }
        case R.id.radio_button2: {
          this.mTabHost.setCurrentTabByTag("LIB_TAB");
          currentView = 1;
          break;
        }
        case R.id.radio_button4: {
          this.mTabHost.setCurrentTabByTag("MORE_TAB");
          currentView = 2;
          break;
        }
      }
    }

  }

  private void setupIntent() {
    this.mTabHost = (AnimationTabHost) getTabHost();
    TabHost localTabHost = this.mTabHost;

    localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_tab_info, R.drawable.icon_1_n,
        this.infoIntent));

    localTabHost.addTab(buildTabSpec("LIB_TAB", R.string.main_tab_lib, R.drawable.icon_3_n,
        this.libIntent));

    localTabHost.addTab(buildTabSpec("MORE_TAB", R.string.main_tab_set, R.drawable.icon_5_n,
        this.setIntent));

  }

  private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {
    return this.mTabHost.newTabSpec(tag)
        .setIndicator(getString(resLabel), getResources().getDrawable(resIcon)).setContent(content);
  }

  private boolean checkNetworkInfo() {
    boolean netStatus = false;

    ConnectivityManager connMgr =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    boolean isWifiConn = networkInfo.isConnected();
    networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    boolean isMobileConn = false;
    if (networkInfo != null) isMobileConn = networkInfo.isConnected();
    Log.i("WIFI", "Wifi connected: " + isWifiConn);
    Log.i("Mobile", "Mobile connected: " + isMobileConn);

    if (isWifiConn || isMobileConn) netStatus = true;
    return netStatus;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if (gestureDetector.onTouchEvent(event)) {
      event.setAction(MotionEvent.ACTION_CANCEL);
    }
    return super.dispatchTouchEvent(event);
  }

  private class TabHostTouch extends SimpleOnGestureListener {
    private static final int ON_TOUCH_DISTANCE = 80;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      if (e2.getX() - e1.getX() >= ON_TOUCH_DISTANCE) {
        currentView = mTabHost.getCurrentTab() - 1;
        if (currentView < 0) {
          currentView = mTabHost.getTabCount() - 1;
        }
      } else if (e1.getX() - e2.getX() >= ON_TOUCH_DISTANCE) {
        currentView = mTabHost.getCurrentTab() + 1;
        if (currentView >= mTabHost.getTabCount()) {
          currentView = 0;
        }
      }
      mTabHost.setCurrentTab(currentView);
      
      
      switch (currentView) {
        case 0: {
          b0.setChecked(true);
          break;
        }

        case 1: {
          b1.setChecked(true);
          break;
        }
        
        case 2: {
          b3.setChecked(true);
          break;
        }

        default:
          break;
      }
      return false;
    }
  }



}
