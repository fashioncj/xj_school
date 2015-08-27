package com.lib.activity.library;


import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.util.StringUtils;

public class BookDetailActivity extends TabActivity {


    private LibraryApplication app;

    String macroUrl;


    private TextView title;
    private TextView author;
    private TextView press;
    private TextView isbn;
    private TextView ele;


    private TextView author_pre;
    private TextView press_pre;
    private TextView isbn_pre;
    private TextView ele_pre;

    updaterBroadcastReceiver r;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);

        app = (LibraryApplication) getApplication();

        String LOCATION = getString(R.string.tab_location);
        String PRE_RES = getString(R.string.tab_rec);

        title = (TextView) findViewById(R.id.title);
        press = (TextView) findViewById(R.id.press);
        isbn = (TextView) findViewById(R.id.isbn);
        author = (TextView) findViewById(R.id.author);
        ele = (TextView) findViewById(R.id.ele);

        press_pre = (TextView) findViewById(R.id.press_pre);
        isbn_pre = (TextView) findViewById(R.id.isbn_pre);
        author_pre = (TextView) findViewById(R.id.author_pre);
        ele_pre = (TextView) findViewById(R.id.ele_pre);

        press_pre.setText(press_pre.getText().toString() + ": ");
        isbn_pre.setText(isbn_pre.getText().toString() + ": ");
        author_pre.setText(author_pre.getText().toString() + ": ");
        ele_pre.setText(ele_pre.getText().toString() + ": ");

        // 设置标题
        macroUrl = this.getIntent().getStringExtra("url");

        Resources res = getResources(); // Resource object to get Drawables

        final TabHost tabHost = getTabHost();
        // 馆藏 Tab
        TabSpec bookLocationTabSpec =
                tabHost.newTabSpec(LOCATION).setIndicator(LOCATION,
                        res.getDrawable(R.drawable.book_detail_tab_location));
        Intent bookLocationIntent = new Intent(this, BookLocation.class);
        bookLocationIntent.putExtra("macroUrl", macroUrl);
        bookLocationTabSpec.setContent(bookLocationIntent);
        // 预约Tab
        TabSpec bookPreResTabSpec =
                tabHost.newTabSpec(PRE_RES).setIndicator(PRE_RES,
                        res.getDrawable(R.drawable.book_detail_tab_res));
        Intent bookPreResIntent = new Intent(this, BookPreReserveActivity.class);
        bookPreResIntent.putExtra("macroUrl", macroUrl);
        bookPreResTabSpec.setContent(bookPreResIntent);

        // Adding all TabSpec to TabHost
        tabHost.addTab(bookLocationTabSpec);
        tabHost.addTab(bookPreResTabSpec);

        View v;
        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            // 获取tabview项
            v = tabWidget.getChildAt(i);
            // 设置tab背景颜色
            v.setBackgroundResource(android.R.color.white);
            // 获取textview控件，（默认为白色）
            TextView textView = (TextView) v.findViewById(android.R.id.title);
            textView.setTextColor(Color.BLACK);
            // 默认选项要处理
            if (tabHost.getCurrentTab() == i)
                v.setBackgroundResource(R.drawable.renren_sdk_pay_repair_btn);
        }


        // tabchanged的监听
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            // tabId显示的是：newTabSpec里面的值
            @Override
            public void onTabChanged(String tabId) {
                // 首先把所有的view背景初始化了.
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    View v = tabHost.getTabWidget().getChildAt(i);
                    // 设置tab背景颜色
                    v.setBackgroundResource(android.R.color.white);
                    // 选中的进行处理
                    if (tabHost.getCurrentTab() == i) {
                        v.setBackgroundResource(R.drawable.renren_sdk_pay_repair_btn);
                    }

                }
            }
        });

    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter("com.lib.LibraryApplication.Refresh");
        r = new updaterBroadcastReceiver();
        registerReceiver(r, filter);

        super.onResume();
    }

    class updaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            title.setText(app.bookDetailInfo.getName());
            author.setText(app.bookDetailInfo.getAuth());
            press.setText(app.bookDetailInfo.getPress());
            isbn.setText(app.bookDetailInfo.getIsbn());

            if (!StringUtils.isEmpty(app.bookDetailInfo.getElec())) {
                ele.setVisibility(View.VISIBLE);
                ele.setText(app.bookDetailInfo.getElec());
                ele_pre.setVisibility(View.VISIBLE);
            } else {
                ele.setVisibility(View.GONE);
                ele_pre.setVisibility(View.GONE);
            }

            if (author.getText().length()<1){
                author.setVisibility(View.GONE);
            }else{
                author.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(r);
        super.onDestroy();
    }

}
