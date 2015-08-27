package com.lib.activity.news;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.setting.SettingIndex;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class NewsIndexActivity extends TabActivity  implements OnCheckedChangeListener {

  private TabHost mTabHost;
  private Intent schoolIntent;
  private Intent activeIntent;
  private Intent mediaIntent;
  private Intent libIntent;

  View.OnTouchListener gestureListener;
  int currentView = 0;

  RadioButton b0;
  RadioButton b1;
  RadioButton b2;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.news_maintabs);

    this.mTabHost = getTabHost();

    LibraryApplication mApp = ((LibraryApplication) getApplicationContext());
    boolean netStatus = checkNetworkInfo();
    mApp.setNetStatus(netStatus);


    this.schoolIntent = new Intent(this, NewsActivity.class);
    schoolIntent.putExtra("type", 0);
    
    this.activeIntent = new Intent(this, NewsActivity.class);
    activeIntent.putExtra("type", 2);

    
    this.mediaIntent = new Intent(this, NewsActivity.class);
    mediaIntent.putExtra("type", 1);
    
     
    
    b0 = ((RadioButton) findViewById(R.id.radio_button0));
    b0.setOnCheckedChangeListener(this);
    b1 = ((RadioButton) findViewById(R.id.radio_button2));
    b1.setOnCheckedChangeListener(this);
    b2 = ((RadioButton) findViewById(R.id.radio_button3));
    b2.setOnCheckedChangeListener(this);
    

    setupIntent();



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
          this.mTabHost.setCurrentTabByTag("B_TAB");
          currentView = 1;
          break;
        }
        case R.id.radio_button3: {
          this.mTabHost.setCurrentTabByTag("C_TAB");
          currentView = 2;
          break;
        }
        
      }
    }

  }

  private void setupIntent() {
    TabHost localTabHost = this.mTabHost;

    localTabHost.addTab(buildTabSpec("A_TAB", R.string.school_news, R.drawable.icon_1_n,
        this.schoolIntent));

    localTabHost.addTab(buildTabSpec("B_TAB", R.string.action_news, R.drawable.icon_3_n,
        this.activeIntent));

    localTabHost.addTab(buildTabSpec("C_TAB", R.string.media_news, R.drawable.icon_4_n,
        this.mediaIntent));
     

  }

  private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {
    TabSpec ret =  this.mTabHost.newTabSpec(tag)
        .setIndicator(getString(resLabel), getResources().getDrawable(resIcon)).setContent(content);
    return ret;
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
    return super.dispatchTouchEvent(event);
  }

   
}
