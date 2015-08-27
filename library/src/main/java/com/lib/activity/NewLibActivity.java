package com.lib.activity;

import com.andybrier.lib.R;
import com.andybrier.lib.R.layout;
import com.andybrier.lib.R.menu;
import com.lib.activity.library.AskActivity;
import com.lib.activity.library.BookPreInfoActivity;
import com.lib.activity.library.EbscoActivity;
import com.lib.activity.library.LibSearchActivity;
import com.lib.activity.library.LibinfoActivity;
import com.lib.activity.library.LibraryActivity;
import com.lib.activity.library.LostCardActivity;
import com.lib.activity.library.MyLendActivity;
import com.lib.activity.library.RecommondActivity;
import com.lib.activity.news.NewsActivity;
import com.lib.util.UrlUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class NewLibActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tab_lib);
  }
  
  
  public void btn_news(View target)
  {
    Intent  toStartIntent = new Intent(this, NewsActivity.class);
    toStartIntent.putExtra("type", 3);
    startActivity(toStartIntent);
  }

  public void btn_ebsco(View target)
  {
    Intent toStartIntent = new Intent( this, EbscoActivity.class);
    String url = UrlUtils.getEbscoUrl();
    toStartIntent.putExtra("url", url);
    toStartIntent.putExtra("title", getString(R.string.ebsco));
    startActivity(toStartIntent);
  }
  public void btn_opac(View target)
  {
    Intent toStartIntent = new Intent( this, LibSearchActivity.class);
    startActivity(toStartIntent);
  }

  public void btn_borrowing(View target)
  {
    Intent toStartIntent = new Intent( this, MyLendActivity.class);
    startActivity(toStartIntent);
  }
  public void btn_reserve(View target)
  {
    Intent toStartIntent = new Intent( this, BookPreInfoActivity.class);
    startActivity(toStartIntent);
  }
  public void btn_recommand(View target)
  {
    Intent toStartIntent = new Intent( this, RecommondActivity.class);
    startActivity(toStartIntent);
  }
  
  public void btn_train_plan(View target)
  {
    Intent toStartIntent = new Intent( this, EbscoActivity.class);
    String url = UrlUtils.getTrainPlanUrl();
    toStartIntent.putExtra("url", url);
    toStartIntent.putExtra("title", getString(R.string.train_plan));

    startActivity(toStartIntent);
  }
  
  public void btn_lost(View target)
  {
    Intent toStartIntent = new Intent( this, LostCardActivity.class);
    startActivity(toStartIntent);
  }
  public void btn_info(View target)
  {
    Intent toStartIntent = new Intent( this, LibinfoActivity.class);
    startActivity(toStartIntent);
  }
  public void btn_ask(View target)
  {
    Intent toStartIntent = new Intent( this, AskActivity.class);
    startActivity(toStartIntent);
  }
  
  public void btn_libguild(View target)
  {
    Intent toStartIntent = new Intent( this, EbscoActivity.class);
    String url = UrlUtils.getLibguildUrl();
    toStartIntent.putExtra("url", url);
    toStartIntent.putExtra("title", getString(R.string.lib_guides));

    startActivity(toStartIntent);
  }
  public void btn_train_res(View target)
  {
    Intent toStartIntent = new Intent( this, EbscoActivity.class);
    String url = UrlUtils.getTrainResUrl();
    toStartIntent.putExtra("url", url);
    toStartIntent.putExtra("title", getString(R.string.train_res));

    startActivity(toStartIntent);
  }

  
}
