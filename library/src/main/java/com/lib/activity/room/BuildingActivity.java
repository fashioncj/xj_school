package com.lib.activity.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.library.MyLendActivity;
import com.lib.database.DBHelper;
import com.readystatesoftware.viewbadger.BadgeView;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BuildingActivity extends Activity {

  private ArrayList<Map<String, Object>> list;
  final String BUILD_NAME = "Build_Name";
  final String BUILD_TOTAL = "Build_Total";
  String totalRoom;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_building);

    LibraryApplication app = (LibraryApplication) getApplication();
    final DBHelper dbHelper = app.dbHelper;
    if (dbHelper.getPersonInfo() == null || dbHelper.getPersonInfo().getEmail() == null
        || dbHelper.getPersonInfo().getEmail().contains("@student")
        || dbHelper.getPersonInfo().getEmail().contains("@STUDENT")) {
      Toast.makeText(this, getString(R.string.room_book_not_auth), Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }

    
    totalRoom = getString(R.string.total_room);


    /**
     * 设置主布局。
     */
    setContentView(R.layout.activity_building);

    /**
     * ListView数据集。
     */
    list = new ArrayList<Map<String, Object>>();
    Set<String> buildingSet = Data.roomData.keySet();
    for (String buildName : buildingSet) {
      HashMap<String, Object> mMap = new HashMap<String, Object>();
      mMap.put(BUILD_NAME, buildName);
      mMap.put(BUILD_TOTAL, Data.roomData.get(buildName).length);
      list.add(mMap);
    }

    /**
     * Adaptor
     * 
     */
    ListView lv = (ListView) findViewById(R.id.list_building);
    lv.setAdapter(new BadgeAdapter(this));


    // 列表点击
    lv.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

        Intent tostart = new Intent(BuildingActivity.this, RoomActivity.class);


        tostart.putExtra("buildingName", (String) list.get(position).get(BUILD_NAME));

        startActivity(tostart);
      }
    });
  }


  private class BadgeAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;

    public BadgeAdapter(Context context) {
      mInflater = LayoutInflater.from(context);
      mContext = context;
    }

    public int getCount() {
      return Data.roomData.size();
    }

    public Object getItem(int position) {
      return position;
    }

    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;

      if (convertView == null) {
        convertView = mInflater.inflate(R.layout.building_item, null);
        holder = new ViewHolder();
        holder.text = (TextView) convertView.findViewById(R.id.buildName);
        holder.batext = (TextView) convertView.findViewById(R.id.bag);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.text.setText(String.valueOf(list.get(position).get(BUILD_NAME)));
      holder.batext.setText(totalRoom + String.valueOf(list.get(position).get(BUILD_TOTAL)));

      return convertView;
    }

    class ViewHolder {
      TextView text;
      TextView batext;
    }

  }

}
