package com.lib.activity.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.bean.lib.Person;
import com.lib.database.DBHelper;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.StatusCheckerUtil;
import com.lib.util.UrlUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 借阅证挂失
 * 
 * @author Administrator
 * 
 */
public class LostCardActivity extends Activity {

  private Button button;
  private EditText lostcardEditText;

  private Handler handler;

  private String cardNo;

  private ProgressDialog progressBar;

  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lost_card);

    LibraryApplication application = (LibraryApplication) getApplication();
    dbHelper = application.dbHelper;

    String checkRet = StatusCheckerUtil.checkBoth(this);
    if (checkRet != null) {
      Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }

    // 先看一看是不能能够挂失
    final String golostUrl = UrlUtils.getGoLostCardUrl();
    HttpUrl post = new HttpUrl();
    String _result = post.post(golostUrl, new ArrayList<NameValuePair>());
    String canGoLost = ContentParse.parseGoLostResult(_result);
    if (canGoLost != null) {
      Toast.makeText(LostCardActivity.this, canGoLost, Toast.LENGTH_SHORT).show();

      this.finish();
      return;
    }


    progressBar = new ProgressDialog(this);
    progressBar.setMessage(getString(R.string.processing));
    progressBar.setCanceledOnTouchOutside(false);

    button = (Button) findViewById(R.id.button_lost);
    lostcardEditText = (EditText) findViewById(R.id.lostcardId);


    // dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.card_no_can_not_be_null)).setCancelable(false)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface _dialog, int arg1) {
            _dialog.dismiss();
          }
        });
    final Dialog nullAlert = builder.create();

    button.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {

        cardNo = lostcardEditText.getText().toString();
        LogUtils.log("Lost Card", cardNo);

        if (cardNo == null || cardNo.trim().length() == 0) {
          nullAlert.show();
        } else {
          handler = new LostHandler();

          progressBar.show();

          new LostThread().start();
        }
      }
    });


  }


  class LostHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      progressBar.dismiss();

      int parsedResult = msg.what;

      if (parsedResult == 0) {
        Toast.makeText(LostCardActivity.this,
            LostCardActivity.this.getString(R.string.lost_success), Toast.LENGTH_SHORT).show();
         LostCardActivity.this.finish();
      } else {
        Toast.makeText(LostCardActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();

        // 这里自动去重新登录
        new Thread(new Runnable() {

          @Override
          public void run() {
            // TODO Auto-generated method stub

            Person p = dbHelper.getPersonInfo();
            if (p != null && p.getName() != null && p.getPass() != null) {
              List<NameValuePair> params = new ArrayList<NameValuePair>();
              params.add(new BasicNameValuePair("number", p.getName()));
              params.add(new BasicNameValuePair("passwd", p.getPass()));
              params.add(new BasicNameValuePair("returnUrl", ""));
              params.add(new BasicNameValuePair("select", "ldap"));
              try {
                HttpUrl postUrl = new HttpUrl();
                postUrl.post(UrlUtils.getLoginUrl(), params);
              } catch (Exception e) {}
            }
          }
        }).start();

      }
    }
  }

  class LostThread extends Thread {

    @Override
    public void run() {

      List<NameValuePair> params = new ArrayList<NameValuePair>(1);

      // 先看一看是不能能够挂失
      final String golostUrl = UrlUtils.getGoLostCardUrl();
      HttpUrl post = new HttpUrl();
      String _result = post.post(golostUrl, params);
      String canGoLost = ContentParse.parseGoLostResult(_result);
      if (canGoLost != null) {
        Toast.makeText(LostCardActivity.this, canGoLost, Toast.LENGTH_SHORT).show();
      }



      final String lostUrl = UrlUtils.getLostCardUrl();

      post = new HttpUrl();
      params.add(new BasicNameValuePair("loss_pwd", cardNo));
      String result = post.post(lostUrl, params);

      LogUtils.log("Post lost card return", result);

      String parsedResult = ContentParse.parseLostResult(result);
      if (parsedResult == null) {
        LogUtils.log("Lost", "request result is ok");
      }

      int what = parsedResult == null ? 0 : 1;
      Message message = new Message();
      message.what = what;
      message.obj = parsedResult;

      handler.sendMessage(message);
    }

  }


}
