package com.lib.activity.room.adaptor;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.lib.bean.lib.Person;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.UrlUtils;

public class CancleTask extends Thread {

  private final RoomBookingInfo info;

  private final Person p;

  private final Handler handler;
  
  private final int listIndex;

  public CancleTask(RoomBookingInfo info, Person p, Handler handler,int listIndex) {

    this.info = info;
    this.p = p;
    this.handler = handler;
    this.listIndex = listIndex;
  }

  @Override
  public void run() {


    final String url = UrlUtils.getDelRoomBookInfo();
    LogUtils.log("Del Room info url", url);

    // String tString =
    // "{\"undoInfo\":{\"entryID\":\"74197\",\"userName\":\"xiaopu.jin\",\"passWord\":22410831}}";
    JSONObject root = new JSONObject();
    JSONObject filter = new JSONObject();
    try {
      root.put("undoInfo", filter);
      filter.put("entryID", info.getId());
      filter.put("userName", p.getName());
      filter.put("passWord", p.getPass());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    String con = root.toString();
    LogUtils.log("Del Room Request", con);

    HttpUrl httpUrl = new HttpUrl();
    String content = httpUrl.post(url, con);

    LogUtils.log("Del Room Result", content);

    Message message = handler.obtainMessage();
    message.obj = content;
    message.arg1 = listIndex;
    handler.sendMessage(message);
    
  }

}
