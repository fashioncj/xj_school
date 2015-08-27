package com.lib.activity.room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.room.adaptor.RoomBookAdaptor;
import com.lib.activity.room.adaptor.RoomBookingInfo;
import com.lib.bean.lib.Person;
import com.lib.database.DBHelper;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.StatusCheckerUtil;
import com.lib.util.TimeUtil;
import com.lib.util.UrlUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class RoomSearchActivity extends Activity {

  private EditText textView;
  private int m_year, m_month, m_day;
  private Person p;
  private final List<RoomBookingInfo> list = new LinkedList<RoomBookingInfo>();
  private ArrayAdapter<RoomBookingInfo> adapter;
  private Handler cancleHandle;

  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room_search);

    // 检查网络和登录
    String checkRet = StatusCheckerUtil.checkBoth(this);
    if (checkRet != null) {
      Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }

    ListView listView = (ListView) findViewById(R.id.list_booking);
    adapter = new RoomBookAdaptor(this, R.layout.room_booking_item, list);
    listView.setAdapter(adapter);
    
    //设置Handler
    cancleHandle = new CancleHanlder();

    // 获取p
    final DBHelper dbHelper = ((LibraryApplication) getApplicationContext()).dbHelper;
    this.p = dbHelper.getPersonInfo();

    if (dbHelper.getPersonInfo() == null || dbHelper.getPersonInfo().getEmail() == null
        || dbHelper.getPersonInfo().getEmail().contains("@student")
        || dbHelper.getPersonInfo().getEmail().contains("@STUDENT")) {
      Toast.makeText(this, getString(R.string.room_book_not_auth), Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }


    // 设置processbar
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage(getString(R.string.processing));
    progressDialog.setCanceledOnTouchOutside(false);

    // 时间选择
    textView = (EditText) findViewById(R.id.date_now);
    textView.setText(TimeUtil.getNowDate());
    Calendar cal = Calendar.getInstance();
    m_year = cal.get(Calendar.YEAR);// 年
    m_month = cal.get(Calendar.MONTH);// 月
    m_day = cal.get(Calendar.DAY_OF_MONTH);// 日

    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        showDialog(0);
      }
    });
    Button bt = (Button) findViewById(R.id.bt_room_search);
    bt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // do search
        new LoadDataTask().execute();
      }
    });

  }


  protected Dialog onCreateDialog(int id) {
    if (id == 0) {
      return new DatePickerDialog(this, datePickerButtonListener, m_year, m_month, m_day);
    }
    return null;
  }

  private OnDateSetListener datePickerButtonListener = new OnDateSetListener() {
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      // 将当前的设置的时间赋值到文本框中
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, year);
      cal.set(Calendar.MONTH, monthOfYear);
      cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

      m_year = year;
      m_month = monthOfYear;
      m_day = dayOfMonth;

      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String datString = df.format(cal.getTime());
      Date selDate = null;
      try {
        selDate = df.parse(datString);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      String week = TimeUtil.getWeekOfDate(selDate);
      textView.setText(datString + " " + week);
    }
  };

 /**
  * 处理cancle的结果
  * @author Administrator
  *
  */
 class CancleHanlder extends Handler
  {
   @Override
   public void handleMessage(Message msg) {
     super.handleMessage(msg);
     
     progressDialog.dismiss();
     
     Object o = msg.obj;
     String ret = "fail";
     if( o != null)
     {
       ret=(String)o;
     }
     
     if (ret.contains("success" )){
       Toast.makeText(RoomSearchActivity.this,
           getString(R.string.del_book_success), Toast.LENGTH_SHORT).show();
       list.remove(msg.arg1);
       adapter.notifyDataSetChanged();
     } else {
       Toast.makeText(RoomSearchActivity.this, getString(R.string.del_book_fail),
           Toast.LENGTH_SHORT).show();
     }
     
   }
  }

  class LoadDataTask extends AsyncTask<Void, Void, Void> {
    
    private ProgressDialog innerProgressDialog;
    public LoadDataTask()
    {
      innerProgressDialog = new ProgressDialog( RoomSearchActivity.this);
      innerProgressDialog.setMessage(getString(R.string.processing));
      innerProgressDialog.setCanceledOnTouchOutside(false);
      innerProgressDialog.show();
    }
    
    @Override
    protected Void doInBackground(Void... params) {

      if (isCancelled()) {
        return null;
      }

      final String url = UrlUtils.getRoomBookInfo();
      LogUtils.log("Get Room info url", url);

      // "{\"filterInfo\":{\"fromDay\":\"16\",\"fromMonth\":\"04\",\"fromYear\":\"2013\",\"creatorMatch\":\"weiwei.jiang\"}}";
      JSONObject root = new JSONObject();
      JSONObject filter = new JSONObject();
      try {
        root.put("filterInfo", filter);
        filter.put("fromDay", m_day);
        filter.put("fromMonth", (m_month + 1) % 12);
        filter.put("fromYear", m_year);
        filter.put("creatorMatch", p.getName());
      } catch (JSONException e) {
        e.printStackTrace();
      }

      String con = root.toString();
      LogUtils.log("Get Room info content", con);

      HttpUrl httpUrl = new HttpUrl();
      String content = httpUrl.post(url, con);

      ContentParse.parserRoomBook(content, list);

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {

      adapter.notifyDataSetChanged();

      innerProgressDialog.dismiss();
    }


  }

  public Person getP() {
    return p;
  }


  public Handler getCancleHandle() {
    return cancleHandle;
  }


  public ProgressDialog getProgressDialog() {
    return progressDialog;
  }


}
