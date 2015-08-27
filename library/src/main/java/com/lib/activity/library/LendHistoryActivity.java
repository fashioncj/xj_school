package com.lib.activity.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.StatusCheckerUtil;
import com.lib.util.UrlUtils;

public class LendHistoryActivity extends Activity {


  // 获取借阅历史
  private final static String LEND_HISTORY_URL = UrlUtils.getMyLendHistory();

  private LendHistory lendHistory;

  private final static int ADD_BOOK_LIST = 1;
  private final static int ADD_MORE_BOOK = 3;
  private final static int NO_MORE_BOOK = 4;
  private final static int ONLY_ONE_PAGE = 5;
  private final static int NO_ANY_BOOK = 6;

  private ProgressBar progressbar;

  private ArrayList<Map<String, Object>> list;
  private SimpleAdapter adapter;
  private Handler handler;
  private ProgressDialog dialog;
  private ListView lv;
  private View loadView;
  private Button loadButton;

  // 当前页
  private int nowPageNum;
  private int maxPageNum;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lend_history);

    String checkRet = StatusCheckerUtil.checkBoth(this);
    if (checkRet != null) {
      Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
      this.finish();
      return;
    }


    lendHistory = new LendHistory();
    lendHistory.setAuthor(getString(R.string.lib_author));
    lendHistory.setBookName(getString(R.string.lib_bookname));
    lendHistory.setLendDate(getString(R.string.lend_date));
    lendHistory.setReturnDate(getString(R.string.return_date));
    lendHistory.setLocation(getString(R.string.location));

    list = new ArrayList<Map<String, Object>>();
    // 进度dialog
    dialog = new ProgressDialog(this);
    dialog.setMessage(getString(R.string.processing));
    dialog.setCanceledOnTouchOutside(false);
    dialog.show();

    lv = (ListView) findViewById(R.id.lv);

    loadView = getLayoutInflater().inflate(R.layout.footer_morebook, null);
    loadButton = (Button) loadView.findViewById(R.id.button_loading);
    progressbar = (ProgressBar) loadView.findViewById(R.id.progressbar);

    /**
     * 加载更多
     */
    loadButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        progressbar.setVisibility(View.VISIBLE);
        loadButton.setVisibility(View.GONE);

        new FetchMoreLendHistotyThread().start();
      }
    });


    // 点击ListView中的item 获得其中书本的url传递给另一个activity
    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Map<String, Object> map = (HashMap<String, Object>) lv.getItemAtPosition(arg2);
        String title = (String) map.get("name");

        String authStr = (String) map.get("auther");
        String authArray[] = authStr.split(lendHistory.getAuthor() + ContentParse.MAO);
        String author = "";
        if (authArray.length >= 1) {
          author = authArray[1];
        }

        String press = "";
        String url = (String) map.get("maco");
        int start = url.indexOf("item.php");
        url = url.substring(start);

        Intent i = new Intent(LendHistoryActivity.this, BookDetailActivity.class);
        i.putExtra("title", title);
        i.putExtra("author", author);
        i.putExtra("press", press);
        i.putExtra("url", url);
        startActivity(i);
      }

    });


    handler = new MyHandler();

    new FetchLendHistotyThread().start();

  }

  @Override
  protected void onStart() {
    super.onStart();
  };


  class MyHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      dialog.cancel();

      switch (msg.what) {

        case ADD_BOOK_LIST:
          // 添加底部View but如果上一次没有remove就再次点击get会添加重复的loadview
          // 添加底部View的条件应该为判断listview中现实的个数
          if (lv.getFooterViewsCount() == 0) {
            // 使其不可进行 listview 之中的itemclick loadbutton仍可点击
            lv.addFooterView(loadView, null, false);
          }
          lv.setAdapter(adapter);
          Log.i("TAG", "FootViewNum=" + lv.getFooterViewsCount());
          break;
        case ONLY_ONE_PAGE:
          if (lv.getFooterViewsCount() != 0) {
            lv.removeFooterView(loadView);
          }
          lv.setAdapter(adapter);
          Toast.makeText(LendHistoryActivity.this, "全部加载完成，没有更多图书！", Toast.LENGTH_SHORT).show();
          Log.i("TAG", "ONLY ONE PAGE");
          break;
        case ADD_MORE_BOOK:
          adapter.notifyDataSetChanged();
          progressbar.setVisibility(View.GONE);
          loadButton.setVisibility(View.VISIBLE);
          break;
        case NO_MORE_BOOK:
          adapter.notifyDataSetChanged();
          lv.removeFooterView(loadView);
          Toast.makeText(LendHistoryActivity.this, "全部加载完成，没有更多图书！", Toast.LENGTH_SHORT).show();
          break;
        case NO_ANY_BOOK:
          Toast.makeText(LendHistoryActivity.this, "本馆没有您检索的馆藏书目", Toast.LENGTH_SHORT).show();
          break;
      }

    }
  }

  class FetchLendHistotyThread extends Thread {

    @Override
    public void run() {


      final String result = new HttpUrl().get(LEND_HISTORY_URL);

      if (result == null) {
        return;
      }


      maxPageNum = ContentParse.parseLendHistory(result, list, lendHistory);

      adapter =
          new SimpleAdapter(LendHistoryActivity.this, list, R.layout.lend_book_list_item,
              new String[] {"name", "auther", "lend_date", "return_date", "location"}, new int[] {
                  R.id.title, R.id.author, R.id.lend_date, R.id.return_date, R.id.location});

      LogUtils.log("List size", "" + list.size());

      nowPageNum = 2;

      Message msg = new Message();
      if (maxPageNum == 0) {
        msg.what = NO_ANY_BOOK;
      } else if (maxPageNum == 1) {
        msg.what = ONLY_ONE_PAGE;
      } else {
        msg.what = ADD_BOOK_LIST;
      }
      handler.sendMessage(msg);
    }
  }


  class FetchMoreLendHistotyThread extends Thread {
    @Override
    public void run() {

      // 原分析
      // MainActivity.this.list = new ArrayList<Map<String, Object>>();
      final String result = new HttpUrl().get(UrlUtils.getMyLendHistoryPaging(nowPageNum));
      nowPageNum++;

      ContentParse.parseLendHistory(result, list, lendHistory);

      // 模拟点击下一页,到达maxPageNum send 结束Message 取出footerview
      if (nowPageNum <= maxPageNum) {
        Message moreMsg = new Message();
        moreMsg.what = ADD_MORE_BOOK;
        handler.sendMessage(moreMsg);
      } else {
        Message endMsg = new Message();
        endMsg.what = NO_MORE_BOOK;
        handler.sendMessage(endMsg);
      }

    }
  }

}
