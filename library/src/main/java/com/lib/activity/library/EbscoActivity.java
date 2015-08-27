package com.lib.activity.library;

import com.andybrier.lib.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class EbscoActivity extends Activity {

  ProgressDialog pd = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ebsco);
    
    
    TextView tView = (TextView) findViewById(R.id.title_value);
    
    String url = "";
    
    if(getIntent().getExtras() != null && getIntent().getExtras().getString("url")!=null)
    {
      url = getIntent().getExtras().getString("url");
    }
    
    String title = "";
    if(getIntent().getExtras() != null && getIntent().getExtras().getString("title")!=null)
    {
      title = getIntent().getExtras().getString("title");
    }
    tView.setText(title);
    
    WebView webView = (WebView) findViewById(R.id.webView01);
    webView.loadUrl(url);
    pd = ProgressDialog.show(this, getString(R.string.process_title), getString(R.string.process_content));
    webView.reload();
    
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(new MyWebviewClient());;
    webView.getSettings().setUserAgentString("Android");
    
  }

  class MyWebviewClient extends WebViewClient {
   
    @Override
    public void onPageFinished(WebView view,String url)
    {
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
