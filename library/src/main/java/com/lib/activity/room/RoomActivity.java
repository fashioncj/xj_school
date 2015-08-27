package com.lib.activity.room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.andybrier.lib.R;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.TimeUtil;
import com.lib.util.UrlUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;



/**
 * @author hellogv
 */
public class RoomActivity extends TabActivity {

  private EditText datePickerEdit;


  private int m_year, m_month, m_day;
  private Calendar cal;



  private MyHandler mHandler;
  private ProgressDialog progressDialog;

  String buildingId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    buildingId = getIntent().getExtras().getString("buildingName");


    TextView textView = (TextView) findViewById(R.id.room_name);
    textView.setText(buildingId);

    // 时间选择
    datePickerEdit = (EditText) findViewById(R.id.today);
    datePickerEdit.setText(TimeUtil.getNowDate());
    datePickerEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        showDialog(0);
      }
    });


    cal = Calendar.getInstance();// 获取日历的实例
    m_year = cal.get(Calendar.YEAR);// 年
    m_month = cal.get(Calendar.MONTH) + 1;// 月
    m_day = cal.get(Calendar.DAY_OF_MONTH);// 日

    // 设置静态
    Data.year = m_year;
    Data.mon = m_month;
    Data.day = m_day;
    Data.buildId = buildingId;


    final TabHost tabHost = getTabHost();
    // 上午 Tab
    TabSpec morTabSpec = tabHost.newTabSpec(getString(R.string.morning));
    Intent mor = new Intent(this, MorningActivity.class);
    mor.putExtra("buildingName", buildingId);
    morTabSpec.setContent(mor);
    morTabSpec.setIndicator(getString(R.string.morning));
    // 下午Tab
    TabSpec aftTabSpec = tabHost.newTabSpec(getString(R.string.afternoon));
    Intent aft = new Intent(this, AfternoonActivity.class);
    aft.putExtra("buildingName", buildingId);
    aftTabSpec.setContent(aft);
    aftTabSpec.setIndicator(getString(R.string.afternoon));
    // 晚上Tab
    TabSpec nigTabSpec = tabHost.newTabSpec(getString(R.string.midnight));
    Intent night = new Intent(this, NightActivity.class);
    night.putExtra("buildingName", buildingId);
    nigTabSpec.setContent(night);
    nigTabSpec.setIndicator(getString(R.string.midnight));

    // Adding all TabSpec to TabHost
    tabHost.addTab(morTabSpec);
    tabHost.addTab(aftTabSpec);
    tabHost.addTab(nigTabSpec);

    tabHost.setOnTabChangedListener(new OnTabChangeListener() {
      @Override
      public void onTabChanged(String arg0) {
        LogUtils.log("***Selected Tab",
            "Im currently in tab with index::" + tabHost.getCurrentTab());

        Data.currentSelect = tabHost.getCurrentTab();

      }
    });


    // 设置tab的活跃
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);// 得到小时
    if (hour >= 0 && hour <= 12) {
      tabHost.setCurrentTab(0);
    } else if (hour <= 18) {
      tabHost.setCurrentTab(1);
    } else {
      tabHost.setCurrentTab(2);
    }


    // 加载数据
    // processor bar
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage(getString(R.string.processing));
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();


    new HtmlThread().start();
    mHandler = new MyHandler();

  }


  protected Dialog onCreateDialog(int id) {
    if (id == 0) {
      return new DatePickerDialog(this, datePickerButtonListener, m_year, m_month-1, m_day);
    }
    return null;
  }

  private OnDateSetListener datePickerButtonListener = new OnDateSetListener() {
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      // 将当前的设置的时间赋值到文本框中
      cal.set(Calendar.YEAR, year);
      cal.set(Calendar.MONTH, monthOfYear);
      cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

      m_year = cal.get(Calendar.YEAR);// 年
      m_month = cal.get(Calendar.MONTH) + 1;// 月
      m_day = cal.get(Calendar.DAY_OF_MONTH);// 日

      // 设置静态
      Data.year = m_year;
      Data.mon = m_month;
      Data.day = m_day;
      Data.buildId = buildingId;

      LogUtils.log("date set mon", "" + Data.mon);

      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String datString = df.format(cal.getTime());
      Date selDate = null;
      try {
        selDate = df.parse(datString);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      String week = TimeUtil.getWeekOfDate(selDate);
      datePickerEdit.setText(datString + " " + week);

      // 重新加载数据
      progressDialog.show();
      new HtmlThread().start();

    }
  };

  class MyHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      switch (msg.what) {
        case 0: {
          progressDialog.dismiss();


          Intent i = new Intent("com.lib.LibraryApplication.Refresh.Room");
          sendBroadcast(i);

          break;
        }
      }
    }
  }

  class HtmlThread extends Thread {



    @Override
    public void run() {



      JSONObject rootObject = new JSONObject();
      JSONObject jsonObject = new JSONObject();
      try {
        rootObject.put("areaRoomInfo", jsonObject);
        jsonObject.put("areaID", buildingId);
        jsonObject.put("year", m_year);
        jsonObject.put("month", m_month);
        jsonObject.put("day", m_day);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      final String con = rootObject.toString();
      LogUtils.log("RequestRoomStatus", con);

      String httpContent = null;
      final String url = UrlUtils.getRoomStatusUrl();
      if (url != null) {
        httpContent = new HttpUrl().post(url, con);
      }

      ContentParse.parseRoomStatus(httpContent);

      Message msg = new Message();
      msg.what = 0;
      mHandler.sendMessage(msg);
    }

  }

}
