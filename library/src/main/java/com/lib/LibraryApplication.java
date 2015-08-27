package com.lib;

import java.util.Locale;

import android.app.Application;
import android.content.res.Configuration;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.iiseeuu.asyncimage.GDApplication;
import com.lib.bean.lib.BookDetailInfo;
import com.lib.database.DBHelper;

public class LibraryApplication extends GDApplication {

  //baidu api 的key
  String mStrKey = "DA32395086962E7973C29537B2AC4479F730A3FF";
  BMapManager mBMapMan = null;

  public BMapManager getMapManager() {
    return mBMapMan;
  }

  private boolean loginStatus = false;
  public boolean m_bKeyRight = true;
  public DBHelper dbHelper = null;


  public static String languageToLoad = "en";
  
  public boolean changed = false;

  public BookDetailInfo bookDetailInfo;

  @Override
  public void onCreate() {
    super.onCreate();

    // 強制刷新語言為英文
    if (!changed) {
      Locale locale = new Locale("en");
      Locale.setDefault(locale);
      Configuration config = new Configuration();
      config.locale = locale;
      getBaseContext().getResources().updateConfiguration(config, null);
    }



    dbHelper = new DBHelper(this);

    // 创建manager
    mBMapMan = new BMapManager(this);
    mBMapMan.init(this.mStrKey, new MyGeneralListener());
  }

  @Override
  // 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
  public void onTerminate() {
    if (mBMapMan != null) {
      mBMapMan.destroy();
      mBMapMan = null;
    }
    super.onTerminate();

  }



  public boolean getLoginStatus() {
    return loginStatus;
  }

  public void setLibcookie(boolean status) {
    loginStatus = status;
  }

  private boolean netStatus = false;

  public boolean getNetStatus() {
    return netStatus;
  }

  public void setNetStatus(boolean status) {
    netStatus = status;
  }

  // 常用事件监听，用来处理通常的网络错误，授权验证错误等
  class MyGeneralListener implements MKGeneralListener {

    @Override
    public void onGetNetworkState(int iError) {
      if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
        Toast.makeText(LibraryApplication.this.getApplicationContext(), "您的网络出错啦！",
            Toast.LENGTH_LONG).show();
      } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
        Toast.makeText(LibraryApplication.this.getApplicationContext(), "输入正确的检索条件！",
            Toast.LENGTH_LONG).show();
      }
    }

    @Override
    public void onGetPermissionState(int iError) {
      if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
        // 授权Key错误：
        Toast.makeText(LibraryApplication.this.getApplicationContext(), "请输入正确的授权Key！",
            Toast.LENGTH_LONG).show();
        LibraryApplication.this.m_bKeyRight = false;
      }
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    Locale locale = new Locale(languageToLoad);
    Locale.setDefault(locale);
    Configuration config = new Configuration();
    config.locale = locale;
    getBaseContext().getResources().updateConfiguration(config, null);
    super.onConfigurationChanged(newConfig);
  }
  

  @Override
  public void onLowMemory() {
      super.onLowMemory();
  }
}
