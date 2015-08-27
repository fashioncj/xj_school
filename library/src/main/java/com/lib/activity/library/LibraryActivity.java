package com.lib.activity.library;

import java.util.ArrayList;
import java.util.HashMap;

import com.andybrier.lib.R;
import com.lib.activity.InfoActivity;
import com.lib.activity.news.NewsActivity;
import com.lib.activity.setting.LoginActivity;
import com.lib.util.UrlUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LibraryActivity extends Activity {

  // 定义 log资源
  private int[] images = {R.drawable.news,
                          R.drawable.icon_ebsco,    R.drawable.icon_09, 
                          R.drawable.icon_10,       R.drawable.icon_13, 
                          R.drawable.icon_15,       R.drawable.icon_netopen, 
                          R.drawable.icon_11,       R.drawable.icon_12, 
                          R.drawable.ask};


  private GridView gridView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    // 定义文字 {"查询预约","当前借阅","借阅证挂失","讲座","读者荐购","预约管理","个人中心" ,"图书馆信息"};
    final String[] texts =
      
        {   getString(R.string.lib_news),
           getString(R.string.ebsco),      getString(R.string.lib_search),
          getString(R.string.lib_lend),   getString(R.string.lib_myreseave),  
          getString(R.string.rec_title),  getString(R.string.train_plan),
          getString(R.string.lib_card_lost), getString(R.string.lib_info),
          getString(R.string.lib_ask)};

    setContentView(R.layout.gridview_lib);

    gridView = (GridView) findViewById(R.id.gridview_lib);

    // 生成九宫格数组
    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    for (int i = 0; i < images.length; i++) {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("ItemImage", images[i]);
      map.put("itemtext", texts[i]);
      list.add(map);
    }
    // 生成适配器
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {

      HashMap<String, Object> item =
          (HashMap<String, Object>) adapterView.getItemAtPosition(position);

      final String text = (String) item.get("itemtext");
      Intent toStartIntent = null;
      if (text.equals(getString(R.string.lib_search))) {

        toStartIntent = new Intent(LibraryActivity.this, LibSearchActivity.class);
      } else if (text.equals(getString(R.string.lib_mylib))) {
        toStartIntent = new Intent(LibraryActivity.this, LoginActivity.class);
      } else if (text.equals(getString(R.string.lib_myreseave))) {
        toStartIntent = new Intent(LibraryActivity.this, BookPreInfoActivity.class);
      } else if (text.equals(getString(R.string.lib_lend))) {
        toStartIntent = new Intent(LibraryActivity.this, MyLendActivity.class);
      } else if (text.equals(getString(R.string.lib_card_lost))) {
        toStartIntent = new Intent(LibraryActivity.this, LostCardActivity.class);
      } else if (text.equals(getString(R.string.rec_title))) {
        toStartIntent = new Intent(LibraryActivity.this, RecommondActivity.class);
      } else if (text.equals(getString(R.string.len_history))) {
        toStartIntent = new Intent(LibraryActivity.this, LendHistoryActivity.class);
      } else if (text.equals(getString(R.string.lib_info))) {
        toStartIntent = new Intent(LibraryActivity.this, LibinfoActivity.class);
      } 
      else if (text.equals(getString(R.string.lib_ask))) {
        toStartIntent = new Intent(LibraryActivity.this, AskActivity.class);
      }
      
      // 培训安排
      else if (text.equals(getString(R.string.train_plan))) {
        toStartIntent = new Intent(LibraryActivity.this, EbscoActivity.class);
        String url = UrlUtils.getTrainPlanUrl();
        toStartIntent.putExtra("url", url);
        toStartIntent.putExtra("title", getString(R.string.train_plan));

      }
      // ebsco
      else if (text.equals(getString(R.string.ebsco))) {
        toStartIntent = new Intent(LibraryActivity.this, EbscoActivity.class);
        String url = UrlUtils.getEbscoUrl();
        toStartIntent.putExtra("url", url);
        toStartIntent.putExtra("title", getString(R.string.ebsco));

      }
      
      //图书馆新闻
      else if (text.equals(getString(R.string.lib_news))) {
        toStartIntent = new Intent(LibraryActivity.this, NewsActivity.class);
        toStartIntent.putExtra("type", 3);
      } 

      if (toStartIntent != null) {
        startActivity(toStartIntent);
      }
    }
  };

}
