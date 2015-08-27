package com.lib.activity.library;

import java.util.List;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.exception.BookReservationException;
import com.lib.adaptor.DataAdapter;
import com.lib.bean.lib.BookReserveInfo;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.UrlUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.ProgressDialog;



/**
 * 查看圖書詳細：預約Tab
 * 
 * @author Administrator
 * 
 */
public class BookPreReserveActivity extends ListActivity {

  // Progress Dialog
  private ProgressDialog pDialog;

  List<BookReserveInfo> reservList;

  private String macroUrl;

  private ProgressDialog reservdialog;


  private ReserveHandler reserveHandler;
  
  private String errorMsg;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.book_pre_reserve);


    reservdialog = new ProgressDialog(this);
    reserveHandler = new ReserveHandler();
    reservdialog.setMessage(getString(R.string.processing));
    reservdialog.setCanceledOnTouchOutside(false);

    LibraryApplication mApp = (LibraryApplication) getApplication();
    boolean loginStatus = mApp.getLoginStatus();
    if (loginStatus == true) {
      // Loading INBOX in Background Thread
      macroUrl = getIntent().getExtras().getString("macroUrl");
      new LoadTask().execute();
    } else {
      Toast.makeText(BookPreReserveActivity.this, getString(R.string.please_login),
          Toast.LENGTH_SHORT).show();
    }



  }

  class ReserveHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      reservdialog.cancel();

      switch (msg.what) {

        case 0:
          Toast.makeText(BookPreReserveActivity.this, getString(R.string.book_ok),
              Toast.LENGTH_SHORT).show();
          break;
        case 1:
          Toast.makeText(BookPreReserveActivity.this, getString(R.string.book_err),
              Toast.LENGTH_SHORT).show();
          break;
      }

    }
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
      pDialog = new ProgressDialog(BookPreReserveActivity.this);
      pDialog.setMessage(getString(R.string.processing));
      pDialog.setIndeterminate(false);
      pDialog.setCancelable(false);
      pDialog.show();
    }

    /**
     * getting Inbox JSON
     * */
    protected String doInBackground(String... args) {

      String content = new HttpUrl().get(UrlUtils.getBookLocationUrl(macroUrl));

      try {
        BookPreReserveActivity.this.reservList = ContentParse.parseBookReserve(content);
      } catch (BookReservationException e) {
        BookPreReserveActivity.this.reservList = null;
        errorMsg = e.getMsg();
      }



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
          // updating listview
          if (reservList == null) {
            TextView txt = (TextView) findViewById(android.R.id.empty);
            txt.setText(errorMsg);
          } else {
            setListAdapter(new DataAdapter(reserveHandler, BookPreReserveActivity.this,
                R.layout.book_pre_reserve_item, reservList));
          }
        }
      });
    }
  }

  public ProgressDialog getReservdialog() {
    return reservdialog;
  }


}
