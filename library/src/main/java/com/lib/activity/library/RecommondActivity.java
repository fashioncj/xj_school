package com.lib.activity.library;


import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.bean.lib.Person;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.StatusCheckerUtil;
import com.lib.util.StringUtils;
import com.lib.util.UrlUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 读者推荐
 * 
 * @author Administrator
 * 
 */
public class RecommondActivity extends Activity {

  private ProgressDialog progressBar;

  // 控件
  private Button okButton;
  private EditText nameEditText;
  private EditText authorEditText;
  private EditText publisherEditText;
  private EditText publisherTimeEditText;
  private EditText isbnEditText;
  private EditText reasonEditText;

  private String name;
  private String author;
  private String publisher;
  private String publishTime;
  private String isbn;
  private String reason;


  private Handler handler;


  private String rec_result_error;

  // 对话框
  private AlertDialog nameNullAlert;
  private AlertDialog authorNullAlert;
  private AlertDialog toomany;

  private AlertDialog resultDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recommond);


    String checkRet = StatusCheckerUtil.checkBoth(this);
    if (checkRet != null) {
      Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }


    // dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.rec_title_null)).setCancelable(false)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface _dialog, int arg1) {
            _dialog.dismiss();
          }
        });
    nameNullAlert = builder.create();
    
    builder.setMessage(getString(R.string.rec_author_null)).setCancelable(false)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface _dialog, int arg1) {
            _dialog.dismiss();
          }
        });
    authorNullAlert = builder.create();
    
    builder.setMessage(getString(R.string.rec_reason_toomany)).setCancelable(false)
    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface _dialog, int arg1) {
        _dialog.dismiss();
      }
    });
    toomany = builder.create();

    
    
    String sucRec = getString(R.string.rec_result_ok);
    LibraryApplication application = (LibraryApplication) getApplication();
    Person p = application.dbHelper.getPersonInfo();
    if( p != null && p.getEmail() != null)
    {
      sucRec = sucRec + "\nFeedback:" + p.getEmail();
    }
    
    AlertDialog.Builder builderOK = new AlertDialog.Builder(this);
    builderOK.setMessage( sucRec).setCancelable(false)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface _dialog, int arg1) {
            _dialog.dismiss();
            RecommondActivity.this.finish();
          }
        });
    resultDialog = builderOK.create();


    // processor bar
    progressBar = new ProgressDialog(this);
    progressBar.setMessage(getString(R.string.processing));
    progressBar.setCanceledOnTouchOutside(false);


    rec_result_error = getString(R.string.rec_result_error);

    // 控件
    okButton = (Button) findViewById(R.id.rec_ok);
    nameEditText = (EditText) findViewById(R.id.rec_bookName);
    authorEditText = (EditText) findViewById(R.id.rec_author);
    publisherEditText = (EditText) findViewById(R.id.rec_publisher);
    publisherTimeEditText = (EditText) findViewById(R.id.rec_publish_time);
    isbnEditText = (EditText) findViewById(R.id.rec_isbn);
    reasonEditText = (EditText) findViewById(R.id.rec_rea);

    /**
     * 预置
     */
    if (getIntent().getExtras() != null) {
      String in_author = getIntent().getExtras().getString("author");
      if (!StringUtils.isEmpty(in_author)) {
        authorEditText.setText(in_author);
      }
      String in_title = getIntent().getExtras().getString("title");
      if (!StringUtils.isEmpty(in_title)) {
        nameEditText.setText(in_title);
      }
      String in_pub = getIntent().getExtras().getString("pub");
      if (!StringUtils.isEmpty(in_pub)) {
        publisherEditText.setText(in_pub);
      }
      String in_pubYear = getIntent().getExtras().getString("pub_year");
      if (!StringUtils.isEmpty(in_pubYear)) {
        publisherTimeEditText.setText(in_pubYear);
      }
      String in_isbn = getIntent().getExtras().getString("isbn");
      if (!StringUtils.isEmpty(in_isbn)) {
        isbnEditText.setText(in_isbn);
      }
    }

    handler = new RecommondHandler();

    okButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {

        name = nameEditText.getText().toString();
        author = authorEditText.getText().toString();
        publisher = publisherEditText.getText().toString();
        publishTime = publisherTimeEditText.getText().toString();
        isbn = isbnEditText.getText().toString();
        reason = reasonEditText.getText().toString();

        if (name == null || name.trim().length() == 0  ) {
          nameNullAlert.show();
        } 
        else if(author == null
            || author.trim().length() == 0)
        {
           authorNullAlert.show();
        }
        else if(reason != null && reason.getBytes().length>400)
        {
          toomany.show();
        }
        else {
          progressBar.show();
          new RecommondThread().start();
        }

      }
    });
  }


  class RecommondHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      progressBar.dismiss();

      String parsedResult = (String) msg.obj;

      if (parsedResult.equals("OK")) {
        resultDialog.show();
      } else {
        Toast.makeText(RecommondActivity.this, rec_result_error, Toast.LENGTH_SHORT).show();
      }
    }
  }

  class RecommondThread extends Thread {

    @Override
    public void run() {

      final String url =
          UrlUtils.getRecommonUrl(name, author, publisher, publishTime, isbn, reason);
      LogUtils.log("Recommon URL", url);

      final HttpUrl get = new HttpUrl();
      String result = get.get(url);

      LogUtils.log("Recommon Result", result);

      String parsedResult = "OK";
      LogUtils.log("Parsed Result", parsedResult);
      Message message = new Message();
      message.obj = parsedResult;

      handler.sendMessage(message);
    }

  }



}
