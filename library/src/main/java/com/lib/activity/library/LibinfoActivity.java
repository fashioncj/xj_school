package com.lib.activity.library;

import com.andybrier.lib.R;
import com.andybrier.lib.R.layout;
import com.andybrier.lib.R.menu;
import com.lib.util.LanguageUtil;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class LibinfoActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_libinfo);

    String url = null;


    if (!LanguageUtil.isChinese()) {
      url = "http://222.92.148.165:8080/mbl/mobile_library_nvtitle.html";
    } else {
      url = "http://222.92.148.165:8080/mbl/mobile_library_nvtitle_cn.html";

    }



    WebView webView = (WebView) findViewById(R.id.webView01);
    webView.loadUrl(url);
    webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


  }



}
