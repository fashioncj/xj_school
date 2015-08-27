package com.lib.activity;

import com.andybrier.lib.R;
import com.andybrier.lib.R.layout;
import com.andybrier.lib.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView.ScaleType;

public class ShowMapActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_map);
    
    TouchImageView img = (TouchImageView) findViewById(R.id.imageView1);  
    img.setImageResource(R.drawable.map);  
    //img.setScaleType(ScaleType.FIT_XY);
    img.setMaxZoom(14f);  
  }



}
