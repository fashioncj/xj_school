package com.lib.activity.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.andybrier.lib.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我的生活主页面
 * @author Administrator
 *
 */
public class LifeIndexActivity extends Activity {

  // 定义整形数组 （图片资源）
  private int[] images = {R.drawable.icon_16, R.drawable.icon_17, R.drawable.icon_18 };
  private String[] texts = null;
  private GridView gridView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gridview_life);

    texts =
        new String[] {getString(R.string.bus_line), getString(R.string.local_food),
            getString(R.string.local_buy)};


    gridView = (GridView) findViewById(R.id.gridview_life);
    // 生成动态数组，并且转入数据
    ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

    for (int i = 0; i <texts.length; i++) {
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
        int position,// The position of the view in the adapter
        long arg3// The row id of the item that was clicked
    ) {

      HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(position);

      final String text = (String) item.get("itemtext");
      Intent toStartIntent = null;

      // 公交查询
      if (text.equals(getString(R.string.bus_line))) {
        toStartIntent = new Intent(LifeIndexActivity.this, MyLocationActivity.class);
      }
      // 周边美食
      else if (text.equals(getString(R.string.local_food))) {
        toStartIntent = new Intent(LifeIndexActivity.this, MyLocationActivity.class);
      }
      // 周边购物
      else if (text.equals(getString(R.string.local_buy))) {
        toStartIntent = new Intent(LifeIndexActivity.this, MyLocationActivity.class);
      }


      // 启动Activity
      if (toStartIntent != null) {
        startActivity(toStartIntent);
      }
    }
  };

}
