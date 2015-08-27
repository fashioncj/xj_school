package com.lib.activity;

import com.andybrier.lib.R;
import com.lib.activity.map.MyLocationActivity;
import com.lib.activity.news.NewsIndexActivity;
import com.lib.activity.news.ShownewsActivity;
import com.lib.activity.room.BuildingActivity;
import com.lib.activity.room.RoomSearchActivity;
import com.lib.util.UrlUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class NewLifeActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tab_news);
  }


  public void btn_overview(View target) {
    Intent toStartIntent = new Intent(this, ShownewsActivity.class);
    toStartIntent.putExtra("url", UrlUtils.getSchoolInfoUrl());
    toStartIntent.putExtra("title", getString(R.string.school_info));
    toStartIntent.putExtra("notrans", true);
    startActivity(toStartIntent);
  }

  // 校园新闻
  public void btn_news(View target) {
    Intent toStartIntent = new Intent(this, NewsIndexActivity.class);
    toStartIntent.putExtra("type", 0);
    startActivity(toStartIntent);
  }

  // 会议预定
  public void btn_building(View target) {
    Intent toStartIntent = new Intent(this, BuildingActivity.class);
    startActivity(toStartIntent);
  }
  

  // 我的教室
  public void btn_myRoom(View target) {
    Intent toStartIntent = new Intent(this, RoomSearchActivity.class);
    startActivity(toStartIntent);
  }

  // 校园地图
  public void btn_map(View target) {
    Intent toStartIntent = new Intent(this, ShowMapActivity.class);
  
    startActivity(toStartIntent);
  }

  // 校园电话
  public void btn_phone(View target) {
    Intent toStartIntent = new Intent(this, ShownewsActivity.class);
    toStartIntent.putExtra("url", UrlUtils.getContactUrl());
    toStartIntent.putExtra("title", getString(R.string.school_tel));
    toStartIntent.putExtra("notrans", true);
    startActivity(toStartIntent);
  }

  // 生活周边
  public void btn_location(View target) {
    Intent toStartIntent = new Intent(this, MyLocationActivity.class);

    startActivity(toStartIntent);
  }

  // 生活周边
  public void btn_email(View target) {
    Intent toStartIntent = getPackageManager().getLaunchIntentForPackage("com.android.email");

    if (toStartIntent == null) {
      toStartIntent = getPackageManager().getLaunchIntentForPackage("com.htc.android.mail");
    }
    if (toStartIntent == null) {
      toStartIntent = getPackageManager().getLaunchIntentForPackage("com.sec.android.email");
    }

    if (toStartIntent == null) {
      Toast.makeText(this, getString(R.string.no_app_install), Toast.LENGTH_SHORT).show();
    }

    else {
      startActivity(toStartIntent);
    }
  }
}
