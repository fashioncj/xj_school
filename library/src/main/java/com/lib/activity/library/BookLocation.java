package com.lib.activity.library;

import java.util.List;
import java.util.Map;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.UrlUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;


/**
 * 查询图书的详细情况：馆藏信息
 * @author Administrator
 *
 */
public class BookLocation extends ListActivity {


  // Progress Dialog
  private ProgressDialog pDialog;

  List<Map<String, Object>> bookLocationList;

  private String url;

  private LibraryApplication app;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.book_location);


    app = (LibraryApplication) getApplication();

    url = getIntent().getExtras().getString("macroUrl");
    LogUtils.log("book item url", url);


    // Loading INBOX in Background Thread
    new LoadTask().execute();
  }


  /**
   * Background Async Task to Load all INBOX messages by making HTTP Request
   * */
  class LoadTask extends AsyncTask<String, String, String> {

    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pDialog = new ProgressDialog(BookLocation.this);
      pDialog.setMessage(getString(R.string.processing));
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.show();
    }

    /**
     * getting Inbox JSON
     * */
    protected String doInBackground(String... args) {
      String _url = UrlUtils.getBookDetailUtr(url);
      LogUtils.log("Getdetial", _url);
      String content = new HttpUrl().get(_url);
      app.bookDetailInfo = ContentParse.parseBookDetail(content);
      LogUtils.log("bookDetail", app.bookDetailInfo.toString());
      BookLocation.this.bookLocationList = app.bookDetailInfo.getLocationInfo();

      return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
      // dismiss the dialog after getting all products
      pDialog.dismiss();
      // updating UI from Background Thread
      runOnUiThread(new Runnable() {
        public void run() {
          /**
           * Updating parsed JSON data into ListView
           * */
          ListAdapter adapter =
              new SimpleAdapter(BookLocation.this, BookLocation.this.bookLocationList,
                  R.layout.book_location_item, new String[] {"address", "info"}, new int[] {
                      R.id.location_info, R.id.book_info});
          // updating listview
          setListAdapter(adapter);


          Intent i = new Intent("com.lib.LibraryApplication.Refresh");
          sendBroadcast(i);
        }
      });
    }

  }

}
