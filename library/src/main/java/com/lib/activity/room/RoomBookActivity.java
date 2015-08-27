package com.lib.activity.room;

import org.json.JSONException;
import org.json.JSONObject;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.library.LibSearchActivity;
import com.lib.bean.lib.Person;
import com.lib.database.DBHelper;
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
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RoomBookActivity extends Activity {
  private String param;
  private Handler hander;

  private ProgressDialog progressBar;
  // 对话框
  private AlertDialog sucDialog;
  private AlertDialog failDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room_book);

    // 检查网络和登录
    String checkRet = StatusCheckerUtil.checkBoth(this);
    if (checkRet != null) {
      Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }


    LibraryApplication app = (LibraryApplication) getApplication();
    final DBHelper dbHelper = app.dbHelper;
    if (dbHelper.getPersonInfo() == null || dbHelper.getPersonInfo().getEmail() == null
        || dbHelper.getPersonInfo().getEmail().contains("@student")
        || dbHelper.getPersonInfo().getEmail().contains("@STUDENT")) {
      Toast.makeText(this, getString(R.string.room_book_not_auth), Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }



    // dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.room_book_success)).setCancelable(false)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface _dialog, int arg1) {
            _dialog.dismiss();
            RoomBookActivity.this.finish();
          }
        });
    sucDialog = builder.create();

    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    builder2.setMessage(getString(R.string.room_book_fail)).setCancelable(false)
        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface _dialog, int arg1) {
            _dialog.dismiss();
            RoomBookActivity.this.finish();
          }
        });
    failDialog = builder2.create();

    // processor bar
    progressBar = new ProgressDialog(this);
    progressBar.setMessage(getString(R.string.processing));
    progressBar.setCanceledOnTouchOutside(false);

    hander = new OrderHandler();

    Intent intent = getIntent();
    final int orderYear = intent.getExtras().getInt("year");
    final int orderMonth = (intent.getExtras().getInt("mon"));
    final int orderDay = intent.getExtras().getInt("day");
    final String orderArea = intent.getExtras().getString("build");
    final String orderRoom = intent.getExtras().getString("room");
    String time = intent.getExtras().getString("time");

    final String orderHour = time.split(":")[0];
    final String orderMinute = time.split(":")[1];

    TextView room_book_title = (TextView) findViewById(R.id.room_book_title);
    room_book_title.setText(orderArea + "-" + orderRoom);

    final EditText m_name = (EditText) findViewById(R.id.m_name);
    final EditText m_desc = (EditText) findViewById(R.id.m_desc);
    final EditText m_time = (EditText) findViewById(R.id.m_longday);
    final Spinner m_sp_time_type = (Spinner) findViewById(R.id.m_sp_time_type);
    m_sp_time_type.setSelection(0);
    final Spinner m_sp_type = (Spinner) findViewById(R.id.m_sp_type);
    final EditText m_book_time = (EditText) findViewById(R.id.m_book_time);

    m_book_time.setText(orderYear + "-" + orderMonth + "-" + orderDay + " " + orderHour + ":"
        + orderMinute);

    Button button = (Button) findViewById(R.id.button1);
    button.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {

        String meetingName = m_name.getText().toString();
        String desc = m_desc.getText().toString();
        String orderDuration = m_time.getText().toString();

        if (StringUtils.isEmpty(meetingName)) {
          Toast.makeText(RoomBookActivity.this, getString(R.string.meeting_name_not_null),
              Toast.LENGTH_SHORT).show();
          return;
        }
        if (StringUtils.isEmpty(desc)) {
          Toast.makeText(RoomBookActivity.this, getString(R.string.meeting_desc_not_null),
              Toast.LENGTH_SHORT).show();
          return;
        }
        if (StringUtils.isEmpty(orderDuration)) {
          Toast.makeText(RoomBookActivity.this, getString(R.string.meeting_dur_not_null),
              Toast.LENGTH_SHORT).show();
          return;
        }
        progressBar.show();

        //
        String orderUnit = "days";
        String orderUnitSe = (String) m_sp_time_type.getSelectedItem();
        if (getString(R.string.m_t_min).equals(orderUnitSe)) {
          orderUnit = "minutes";
        } else if (getString(R.string.m_t_hour).equals(orderUnitSe)) {
          orderUnit = "hours";
        } else if (getString(R.string.m_t_day).equals(orderUnitSe)) {
          orderUnit = "days";
        } else if (getString(R.string.m_t_week).equals(orderUnitSe)) {
          orderUnit = "weeks";
        }

        String orderType = "I";
        String orderTypeSe = (String) m_sp_type.getSelectedItem();
        if (getString(R.string.m_type_inner).equals(orderTypeSe)) {
          orderType = "I";
        } else if (getString(R.string.m_type_outter).equals(orderTypeSe)) {
          orderType = "E";
        } else if (getString(R.string.m_type_manager).equals(orderTypeSe)) {
          orderType = "D";
        }

        /***
         * {"orderInfo":{"meetingDesc":"testdesc","meetingName":"testmeet","orderArea":"24",
         * "orderDay"
         * :"16","orderDuration":"1","orderHour":"08","orderMinute":"30","orderMonth":"04",
         * "orderRoom"
         * :"166","orderType":"I","orderUnit":"hours","orderUser":"xiaopu.jin","orderYear":"2013"}}
         * 
         * 
         */
        JSONObject rootObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
          rootObject.put("orderInfo", jsonObject);
          jsonObject.put("meetingName", meetingName);
          jsonObject.put("meetingDesc", desc);

          jsonObject.put("orderDay", orderDay);
          jsonObject.put("orderHour", orderHour);
          jsonObject.put("orderMinute", orderMinute);
          jsonObject.put("orderMonth", orderMonth);
          jsonObject.put("orderYear", orderYear);

          jsonObject.put("orderRoom", orderRoom);
          jsonObject.put("orderArea", orderArea);

          jsonObject.put("orderType", orderType);

          jsonObject.put("orderUnit", orderUnit);
          jsonObject.put("orderDuration", orderDuration);

          final DBHelper dbHelper = ((LibraryApplication) getApplicationContext()).dbHelper;
          Person p = dbHelper.getPersonInfo();
          jsonObject.put("orderUser", p.getName());
          jsonObject.put("passWord", p.getPass());


        } catch (JSONException e) {
          e.printStackTrace();
        }

        param = rootObject.toString();
        LogUtils.log("Order meeting", param);

        Thread t = new OrderThread();
        t.start();
      }
    });


  }



  private final class OrderHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      progressBar.dismiss();

      String parsedResult = (String) msg.obj;

      if (parsedResult.equals("success")) {
        sucDialog.show();
      } else {
        failDialog.show();
      }
    }
  }

  class OrderThread extends Thread {

    @Override
    public void run() {

      final String url = UrlUtils.getOrderRoomUrl();
      LogUtils.log("Order URL", url);
      LogUtils.log("Order Content", param);

      final HttpUrl get = new HttpUrl();
      String result = get.post(url, param);

      LogUtils.log("Order Result", result);

      String parsedResult = ContentParse.parseOrderResult(result);
      LogUtils.log("Parsed Result", parsedResult);
      Message message = new Message();
      message.obj = parsedResult;

      hander.sendMessage(message);
    }

  }

}
