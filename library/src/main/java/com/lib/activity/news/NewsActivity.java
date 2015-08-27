package com.lib.activity.news;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.andybrier.lib.R;
import com.lib.adaptor.ListViewNewsAdapter;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.UIHelper;
import com.lib.util.UrlUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iiseeuu.asyncimage.widget.AsyncImageView;


public class NewsActivity   extends ListActivity  {


  private final  List<News> list = new LinkedList<News>();

  private ListViewNewsAdapter adapter;

  private PullToRefreshListView listview;

  private int startPage = 0;
  private int type = 0;
  private String titleString;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.school_news_index);

    //设置标题
    setTyleAndTitle();


    //设置listview
    setupPullToRefreshList();
   

    //加载数据
    new LoadDataTask(true).execute();


 
  }

private void setupPullToRefreshList()
{
  listview = (PullToRefreshListView) findViewById(R.id.list);

   adapter = new ListViewNewsAdapter(NewsActivity.this, list, R.layout.news_listitem);
   listview.setAdapter(adapter);
  
  //设置refresh
  listview.setOnRefreshListener(new OnRefreshListener<ListView>() {
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
      new LoadDataTask(false).execute();
    }
    });
    
    //设置声音
    /**
     * Add Sound Event Listener
     */
//    SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
//    soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
//    soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
//    soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
//    listview.setOnPullEventListener(soundListener);
    
    
    //只能从下面拉
    listview.setMode(Mode.PULL_FROM_END);
    
    // 点击list，打开新闻
    listview.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg3) {
        
        int _index = index-1;

        Intent showNewsIntent = new Intent(NewsActivity.this, ShownewsActivity.class);
        showNewsIntent.putExtra("url", (String) (list.get(_index).getUrl()));
        showNewsIntent.putExtra("title", titleString);
        startActivity(showNewsIntent);
      }
    });
    
    listview.setOnScrollListener(new OnScrollListener() {
      
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (getListView() == view) {
          searchAsyncImageViews(view,
                  scrollState == OnScrollListener.SCROLL_STATE_FLING);
      }
        
      }
      
      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        // TODO Auto-generated method stub
        
      }
    });
  }
/**
 * 控制imageView状态
 * 
 * @param viewGroup
 * @param pause
 */
private void searchAsyncImageViews(ViewGroup viewGroup, boolean pause) {
    final int childCount = viewGroup.getChildCount();
    for (int i = 0; i < childCount; i++) {
        AsyncImageView image = (AsyncImageView) viewGroup.getChildAt(i)
                .findViewById(R.id.async_image);
        if (image != null) {
            image.setPaused(pause);
        }
    }
}

  private void setTyleAndTitle() {
    // 设置title
    if (getIntent().getExtras() != null) {
      type = getIntent().getExtras().getInt("type");
    }
    TextView titleTextView = ((TextView) findViewById(R.id.news_title));
    titleString = null;
    switch (type) {
      case 0: {
        titleString = getString(R.string.school_news);
        break;
      }
      case 1: {
        titleString = getString(R.string.media_news);
        break;
      }
      case 2: {
        titleString = getString(R.string.action_news);
        break;
      }
      case 3: {
        titleString = getString(R.string.lib_news);
        break;
      }
      default:
        break;
    }

    titleTextView.setText(titleString);
  }

  class LoadDataTask extends AsyncTask<Void, Void, Integer> {
    
    
    private ProgressDialog progressDialog;
    
    private boolean isfirst;

    public LoadDataTask(boolean first) {
      isfirst = first;
      // processor bar
      progressDialog = new ProgressDialog(NewsActivity.this);
      progressDialog.setMessage(getString(R.string.processing));
      progressDialog.setCanceledOnTouchOutside(false);
      
      if(isfirst  )
      {
         progressDialog.show();
      }
    }

    @Override
    protected Integer doInBackground(Void... params) {

      if (isCancelled()) {
        return  null;
      }

      HttpUrl httpUrl = new HttpUrl();
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("language", Locale.getDefault().getLanguage());
        jsonObject.put("startPage", startPage);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      final String url = UrlUtils.getLoadNewsUrl(type);
      LogUtils.log("load news url", url);
      LogUtils.log("load news content", jsonObject.toString());
      String content = httpUrl.post(url, jsonObject.toString());

      int ret = -1;
      if( content != null)
      {
        ret = 0;
      
        List<News> news =  ContentParse.parserNews(content);
        if(null!= news && news.size()>0)
        {
          startPage++;
        }
        list.addAll(news);
      }

      return ret;
    }


    @Override
    protected void onPostExecute(Integer result) {
      
      if(isfirst )
      {
        progressDialog.dismiss();
      }
      if( -1 == result )
      {
         UIHelper.toast(NewsActivity.this,R.string.check_net);
      }
      else {
      adapter.notifyDataSetChanged();
      }
      listview.onRefreshComplete();
      
     
      super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
    }
  }
 
}
