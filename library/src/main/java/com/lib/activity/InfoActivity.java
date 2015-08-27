package com.lib.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.andybrier.lib.R;

import com.lib.activity.map.MyLocationActivity;
import com.lib.activity.news.NewsIndexActivity;
import com.lib.activity.news.ShownewsActivity;
import com.lib.activity.room.BuildingActivity;
import com.lib.activity.room.RoomSearchActivity;
import com.lib.util.UrlUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class InfoActivity extends Activity {

  // 定义整形数组 （图片资源）
  private int[] images = {R.drawable.icon_01,  R.drawable.icon_02, 
                          R.drawable.icon_05, R.drawable.icon_06,
                          R.drawable.icon_08, R.drawable.icon_07, 
                          R.drawable.icon_16, R.drawable.icon_mail};
 
  private GridView gridView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);

    setContentView(R.layout.gridview_info);


    String[] texts =
        {getString(R.string.school_info), getString(R.string.news_all),
            getString(R.string.room_book), getString(R.string.my_room_book),
            getString(R.string.school_map), getString(R.string.school_tel), 
            getString(R.string.main_tab_life), getString(R.string.email),};


    gridView = (GridView) findViewById(R.id.gridview_info);
    // 生成动态数组，并且转入数据
    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

    for (int i = 0; i < texts.length; i++) {
      HashMap<String, Object> map = new HashMap<String, Object>();

      map.put("ItemImage", images[i]);// 添加图像资源的ID
      map.put("itemtext", texts[i]);
      list.add(map);
    }
    // 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
    SimpleAdapter adapter = new SimpleAdapter(this, // 数据来源
        list, R.layout.gridview_item,// XML实现
        new String[] {"ItemImage", "itemtext"}, // 动态数组与ImageItem对应的子项
        new int[] {R.id.gridview_imageview, R.id.gridview_textview}// //ImageItem的XML文件里面的一个ImageView,两个TextView
        // ID
        );
    // 添加并且显示
    gridView.setAdapter(adapter);
    // 添加消息处理
    gridView.setOnItemClickListener(clickListener);
  }

  // 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
  private OnItemClickListener clickListener = new OnItemClickListener() {

    @Override
    public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
        // click happened
        View arg1,// The view within the AdapterView that was clicked
        int arg2,// The position of the view in the adapter
        long arg3// The row id of the item that was clicked
    ) {

      HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);

      final String text = (String) item.get("itemtext");
      Intent toStartIntent = null;
      // 西普概况
       if (text.equals(getString(R.string.school_info))) {
        toStartIntent = new Intent(InfoActivity.this, ShownewsActivity.class);
        toStartIntent.putExtra("url", UrlUtils.getSchoolInfoUrl());
        toStartIntent.putExtra("title", getString(R.string.school_info));
        toStartIntent.putExtra("notrans", true);
      }
      // 校园新闻
       else if (text.equals(getString(R.string.news_all))) {

        toStartIntent = new Intent(InfoActivity.this, NewsIndexActivity.class);
        toStartIntent.putExtra("type", 0);
      }
 
      // 预订会议
      else if (text.equals(getString(R.string.room_book))) {
        toStartIntent = new Intent(InfoActivity.this, BuildingActivity.class);
      }
      // 我的教室
      else if (text.equals(getString(R.string.my_room_book))) {
        toStartIntent = new Intent(InfoActivity.this, RoomSearchActivity.class);
      }
       
       // 校园地图
      else if (text.equals(getString(R.string.school_map))) {
//        toStartIntent = new Intent(InfoActivity.this, ShownewsActivity.class);
//        toStartIntent.putExtra("url", UrlUtils.getSchoolMapUrl());
//        toStartIntent.putExtra("title", getString(R.string.school_map));
//        toStartIntent.putExtra("notrans", true);
        
        toStartIntent = new Intent(InfoActivity.this, ShowMapActivity.class);
       }

       
      // 校园电话
      else if (text.equals(getString(R.string.school_tel))) {
        toStartIntent = new Intent(InfoActivity.this, ShownewsActivity.class);
        toStartIntent.putExtra("url", UrlUtils.getContactUrl());
        toStartIntent.putExtra("title", getString(R.string.school_tel));
        toStartIntent.putExtra("notrans", true);
      }
   
    //生活周邊
      else if (text.equals(getString(R.string.main_tab_life)))
      {
        toStartIntent = new Intent(InfoActivity.this, MyLocationActivity.class);
      }
      // email
      else if (text.equals("Email")) {
        toStartIntent = getPackageManager().getLaunchIntentForPackage("com.android.email");

        if (toStartIntent == null) {
          toStartIntent = getPackageManager().getLaunchIntentForPackage("com.htc.android.mail");
        }
        if (toStartIntent == null) {
          toStartIntent = getPackageManager().getLaunchIntentForPackage("com.sec.android.email");
        }

        if (toStartIntent == null) {
          Toast.makeText(InfoActivity.this, getString(R.string.no_app_install), Toast.LENGTH_SHORT)
              .show();
        }

      }
      if (toStartIntent != null) {
        startActivity(toStartIntent);
      }
    }
  };
}
