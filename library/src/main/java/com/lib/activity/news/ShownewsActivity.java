package com.lib.activity.news;

import com.andybrier.lib.R;
import com.lib.util.LogUtils;
import com.lib.util.StringUtils;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ShownewsActivity extends Activity {
  ProgressDialog pd = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shownews);

    pd =
        ProgressDialog.show(this, getString(R.string.process_title),
            getString(R.string.process_content));

    String title = getIntent().getExtras().getString("title");
    TextView textView = (TextView) findViewById(R.id.news_title);
    if (StringUtils.isEmpty(title)) {
      title = getString(R.string.main_tab_info);
    }
    textView.setText(title);

    String url = getIntent().getExtras().getString("url");

    final boolean notrans = getIntent().getExtras().getBoolean("notrans");

    if (false == notrans) {
      url = "http://222.92.148.165:8080/mbl/transForm?pageInfo.url=" + url;
    }
    LogUtils.log("LoadUrl", url);

    final WebView webView = (WebView) findViewById(R.id.webView01);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setUserAgentString("Android");
    webView.loadUrl(url);


    webView.setOnKeyListener(new View.OnKeyListener() {

      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
          if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // 表示按返回键时的操作
            webView.goBack(); // 后退
            return true; // 已处理
          }
        }
        return false;
      }
    });
    webView.setWebViewClient(new MyWebviewClient());;
  }

  class MyWebviewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
      pd.dismiss();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.startsWith("tel:")) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
        startActivity(intent);
      } else if (url.startsWith("http:") || url.startsWith("https:")) {
        view.loadUrl(url);
      }
      return true;
    }
  }
}
