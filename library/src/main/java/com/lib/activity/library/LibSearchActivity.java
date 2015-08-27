package com.lib.activity.library;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.google.zxing.CaptureActivity;
import com.lib.LibraryApplication;
import com.lib.bean.lib.Book;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.UrlUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibSearchActivity extends Activity {
    private final static int ADD_BOOK_LIST = 1;
    private final static int GET_URL_RIGHT = 2;
    private final static int ADD_MORE_BOOK = 3;
    private final static int NO_MORE_BOOK = 4;
    private final static int ONLY_ONE_PAGE = 5;
    private final static int NO_ANY_BOOK = 6;
    private final static int ISBN_SEARCH_OK = 7;
    private final static int ISBN_SEARCH_NO = 8;
    private final static int ISBN_SEARCH_DOUBAN_OK = 9;
    private final static int ISBN_SEARCH_DOUBAN_NO = 10;

    private boolean NEXT_BOOKACTIVITY = false;

    private final static String TAG = "SearchActivity";

    private MyHandler mHandler;
    private MakeUrl makeUrl;

    private EditText editText;
    private Button getButton;
    private Button scanButton;
    private ListView lv;
    private ProgressBar progressbar;
    private Button loadButton;
    private View loadView;
    public ProgressDialog dialog;
    public InputMethodManager imm;
    private RadioGroup radioGroup;

    public boolean STATUS;
    public int maxBookNum;
    public int maxPageNum;
    public int nowPageNum;
    private List<Map<String, Object>> list;
    public SimpleAdapter adapter;

    public String result;
    public String getInput;
    public String beforeName;

    public LibraryApplication mApp;

    private Dialog isbnNotFound = null;

    private String searchingIsbn;


    private ProgressDialog httpProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // processor bar
        httpProgressBar = new ProgressDialog(this);
        httpProgressBar.setMessage(getString(R.string.processing));
        httpProgressBar.setCanceledOnTouchOutside(false);

        mApp = ((LibraryApplication) getApplicationContext());

        AlertDialog.Builder builderOK = new AlertDialog.Builder(LibSearchActivity.this);
        builderOK.setMessage(getString(R.string.book_no_find_recommon)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int arg1) {
                        _dialog.dismiss();
                        // 去豆瓣中查找
                        new IsbnDoubanSearchThread(searchingIsbn).start();
                        httpProgressBar.show();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int arg1) {
                _dialog.dismiss();
            }
        });
        isbnNotFound = builderOK.create();


        // 检测网络状态
        STATUS = mApp.getNetStatus();

        beforeName = "";
        // 进度dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.processing));
        dialog.setCanceledOnTouchOutside(false);

        // 软键盘管理
        imm = (InputMethodManager) getSystemService(LibSearchActivity.this.INPUT_METHOD_SERVICE);

        getButton = (Button) findViewById(R.id.button_get);
        scanButton = (Button) findViewById(R.id.button_scan);

        lv = (ListView) findViewById(R.id.lv);
        editText = (EditText) findViewById(R.id.name_book);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        loadView = getLayoutInflater().inflate(R.layout.footer_morebook, null);
        loadButton = (Button) loadView.findViewById(R.id.button_loading);
        progressbar = (ProgressBar) loadView.findViewById(R.id.progressbar);


        /**
         * 扫描二维码
         */
        scanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivityForResult(new Intent(LibSearchActivity.this, CaptureActivity.class), 0);
            }
        });

        /**
         * 搜索
         */
        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });


        // 检测enter键
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    getButton.performClick();
                    return true;
                }
                return false;
            }
        });

        // 点击ListView中的item 获得其中书本的url传递给另一个activity
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(arg2);
                String title = map.get("book_title");
                String author = map.get("book_author");
                String press = map.get("book_press");
                String url = map.get("book_netaddress");

                Intent i = new Intent(LibSearchActivity.this, BookDetailActivity.class);
                i.putExtra("title", title);
                i.putExtra("author", author);
                i.putExtra("press", press);
                i.putExtra("url", url);
                startActivity(i);
                // 标记动画
                NEXT_BOOKACTIVITY = true;
            }

        });
        loadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                loadButton.setVisibility(View.GONE);

                new HtmlMoreThread().start();
            }
        });

        mHandler = new MyHandler();
    }


    class IsbnSearchThread extends Thread {
        private String isbn;

        public IsbnSearchThread(String isbn) {
            this.isbn = isbn;
        }


        @Override
        public void run() {

            final String url = UrlUtils.getSearchURL(isbn, "isbn");

            LogUtils.log("SearchISBN", url);

            String result = new HttpUrl().get(url);
            LogUtils.log("SearchISBN result", result);

            String macroUrl = ContentParse.parseIsbnSearch(result);

            Message m = mHandler.obtainMessage();
            if (macroUrl != null) {
                m.what = ISBN_SEARCH_OK;
                m.obj = macroUrl;
            } else {
                m.what = ISBN_SEARCH_NO;
            }
            mHandler.sendMessage(m);
        }

    }

    /**
     * 豆瓣中查找图书
     *
     * @author Administrator
     */
    class IsbnDoubanSearchThread extends Thread {
        private String isbn;

        public IsbnDoubanSearchThread(String isbn) {
            this.isbn = isbn;
        }

        @Override
        public void run() {

            final String url = UrlUtils.getDoubanSearchURL(isbn);

            String result = null;
            try {
                result = new HttpUrl().getDouban(url);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpProgressBar.dismiss();
            }

            Book book = ContentParse.parseDoubanIsbnSearch(result);

            Message m = mHandler.obtainMessage();
            if (book != null) {
                m.what = ISBN_SEARCH_DOUBAN_OK;
                m.obj = book;
            } else {
                m.what = ISBN_SEARCH_DOUBAN_NO;
            }
            mHandler.sendMessage(m);
        }
    }

    private void doSearch() {
        if (STATUS == false) {
            Toast.makeText(LibSearchActivity.this, "请连接有效网络", Toast.LENGTH_SHORT).show();
        } else {
            progressbar.setVisibility(View.GONE);
            loadButton.setVisibility(View.VISIBLE);

            getInput = editText.getText().toString();
            Log.i(TAG, "Input=" + getInput + "!!!");
            if (getInput.equals("")) {
                Toast.makeText(LibSearchActivity.this, "请输入书名", Toast.LENGTH_SHORT).show();
            } else {
                getInput = getInput.replace(' ', '+');
                getInput = getInput.replace("\\", "");

                // 隐藏软键盘 显示进度框
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                dialog.show();

                // 需要在这里初始化部分变量 page
                makeUrl = new MakeUrl();
                new HtmlThread().start();
            }
        }
    }

    /**
     * 处理扫描结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("RESULT");

            searchingIsbn = scanResult;
            new IsbnSearchThread(scanResult).start();
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


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
                    Toast.makeText(LibSearchActivity.this, getString(R.string.load_full), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LibSearchActivity.this, getString(R.string.load_full), Toast.LENGTH_SHORT).show();
                    break;
                case NO_ANY_BOOK:
                    Toast.makeText(LibSearchActivity.this, getString(R.string.load_empty), Toast.LENGTH_SHORT).show();
                    makeUrl.resetName();
                    break;

                /**
                 * ISBN查询到图书 直接到详细
                 */
                case ISBN_SEARCH_OK: {
                    Intent i = new Intent(LibSearchActivity.this, BookDetailActivity.class);
                    i.putExtra("url", (String) msg.obj);
                    startActivity(i);
                    break;
                }
                /**
                 * ISBN查询不到图书
                 */
                case ISBN_SEARCH_NO: {
                    isbnNotFound.show();
                    break;
                }

                /**
                 * 豆瓣中查找图书
                 */
                case ISBN_SEARCH_DOUBAN_OK: {
                    Intent i = new Intent(LibSearchActivity.this, RecommondActivity.class);
                    Book book = (Book) msg.obj;
                    i.putExtra("title", book.getTitle());
                    i.putExtra("author", book.getAuthor());
                    i.putExtra("pub_year", book.getPub_year());
                    i.putExtra("pub", book.getPublisher());
                    i.putExtra("isbn", searchingIsbn);

                    startActivity(i);
                    break;
                }

                /**
                 * 豆瓣中查找不到图书
                 */
                case ISBN_SEARCH_DOUBAN_NO: {
                    Intent i = new Intent(LibSearchActivity.this, RecommondActivity.class);
                    i.putExtra("isbn", searchingIsbn);
                    startActivity(i);
                    break;
                }


            }

        }
    }

    // 9.15 1:06 需要查找strong 标签 第二个显示的即为查找结果总数 其实修改displaypg
    // 键值可以修改每页显示的个数，但是局限于100个

    //fashioncj：fix page number select
    class HtmlThread extends Thread {

        @Override
        public void run() {

            Message msg = new Message();

            result = new HttpUrl().get(makeUrl.getSearchUrl());
            LogUtils.log(TAG,result);
            // ！！！！！建议加入一个判断result是否为空的代码
            Document doc = Jsoup.parse(result);
            // 原分析
            Elements trs = doc.select("tr");
            int totalTrs = trs.size();

            // trs 数值大于 0时 表示存在列表
            if (totalTrs > 0) {

                LibSearchActivity.this.list = new ArrayList<Map<String, Object>>();
                // 获取结果册数 与 结果页数 与 当前page键值
                Elements strongs = doc.select("strong");
                // Strong不存在时无法取值
                beforeName = makeUrl.searchName;
//                Log.i(TAG, "beforeName = " + beforeName);
//                String temMax = strongs.get(1).html().toString();

                Elements page=doc.getElementsByClass("box_bgcolor");
                Elements fonts=page.get(0).getElementsByTag("font");
                String temMax=fonts.get(2).html().toString();
                LogUtils.log(TAG,temMax);



                LibSearchActivity.this.maxBookNum = Integer.parseInt(temMax);
                Log.i(TAG, "maxBookNum = " + maxBookNum);

                if (maxBookNum % 20 == 0) {
                    maxPageNum = maxBookNum / 20;
                } else {
                    maxPageNum = maxBookNum / 20;
                    maxPageNum++;
                }
                Log.i(TAG, "maxPageNum = " + maxPageNum);

                nowPageNum = 2;

                for (int i = 0; i < totalTrs - 1; i++) {

                    Elements tds = trs.get(i + 1).select("td");
                    int totalTds = tds.size();

                    Map<String, Object> map = new HashMap<String, Object>();

                    for (int j = 0; j < totalTds; j++) {

                        switch (j) {

                            case 1:
                                map.put("book_title", tds.get(j).select("a").get(0).html().toString());
                                map.put("book_netaddress", tds.get(j).select("a").attr("href").toString());
                                break;
                            case 2:
                                map.put("book_author", tds.get(j).html().toString());
                                break;
                            case 3:
                                map.put("book_press", tds.get(j).html().toString());
                                break;
                            case 4:
                                map.put("book_address", tds.get(j).html().toString());
                                break;
                            case 5:
                                map.put("cate", tds.get(j).html().toString());
                                break;
                        }
                    }

                    LibSearchActivity.this.list.add(map);
                    adapter =
                            new SimpleAdapter(LibSearchActivity.this, list, R.layout.book_list_item,
                                    new String[]{"book_title", "book_author", "book_press", "book_address", "cate"},
                                    new int[]{R.id.title, R.id.author, R.id.press, R.id.address, R.id.cate});
                }
                // great 给handler传递一切的时候到了！！！！

                if (maxBookNum <= 20) {
                    msg.what = ONLY_ONE_PAGE;
                } else {
                    msg.what = ADD_BOOK_LIST;
                }
            } else {
                msg.what = NO_ANY_BOOK;
            }
            mHandler.sendMessage(msg);
        }
    }

    // 为了解析并且加载后几页的内容做的新类 线程类？
    class HtmlMoreThread extends Thread {
        @Override
        public void run() {

            Log.i(TAG, "nowPageNum = " + nowPageNum);
            // 原分析
            // MainActivity.this.list = new ArrayList<Map<String, Object>>();
            //result = new HttpUrl().get(makeUrl.getPageUrl(nowPageNum++));
            result = new HttpUrl().get("http://opac.lib.xjtlu.edu.cn/cn/opac/search_adv_result.php?sType0=02&q0=php&page="+(nowPageNum++));
            Document doc = Jsoup.parse(result);

            Elements trs = doc.select("tr");
            int totalTrs = trs.size();

            if (totalTrs > 0) {
                for (int i = 0; i < totalTrs - 1; i++) {

                    Elements tds = trs.get(i + 1).select("td");
                    int totalTds = tds.size();

                    Map<String, Object> map = new HashMap<String, Object>();

                    for (int j = 0; j < totalTds; j++) {

                        switch (j) {

                            case 1:
                                map.put("book_title", tds.get(j).select("a").get(0).html().toString());
                                map.put("book_netaddress", tds.get(j).select("a").attr("href").toString());
                                break;
                            case 2:
                                map.put("book_author", tds.get(j).html().toString());
                                break;
                            case 3:
                                map.put("book_press", tds.get(j).html().toString());
                                break;
                            case 4:
                                map.put("book_address", tds.get(j).html().toString());
                                break;
                            case 5:
                                map.put("cate", tds.get(j).html().toString());
                                break;
                        }
                    }

                    LibSearchActivity.this.list.add(map);
                }

            }

            // 模拟点击下一页,到达maxPageNum send 结束Message 取出footerview
            if (nowPageNum <= maxPageNum) {
                Message moreMsg = new Message();
                moreMsg.what = ADD_MORE_BOOK;
                mHandler.sendMessage(moreMsg);
            } else {
                Message endMsg = new Message();
                endMsg.what = NO_MORE_BOOK;
                mHandler.sendMessage(endMsg);
            }

        }
    }

    //
    public class MakeUrl {

        String name;
        private String searchName;
        private String type;

        public MakeUrl() {
            name = LibSearchActivity.this.getInput;
            switch (LibSearchActivity.this.radioGroup.getCheckedRadioButtonId()) {
                case R.id.bookName: {
                    type = "title";
                    type = "02";
                    break;
                }
                case R.id.bookAuthor: {
                    type = "author";
                    type = "03";
                    break;
                }
                case R.id.isbn: {
                    type = "isbn";
                    type = "any";

                    break;
                }
            }

            Log.i(TAG, name);
            try {
                searchName = new String(name.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public String getSearchUrl() {
            String url;
            url = UrlUtils.getSearchURL(searchName, type);
            Log.i("MainActiv.getSearchUrl", url);
            return url;
        }

        public String getPageUrl(int page) {
            String url;
            url = UrlUtils.getPageURL(searchName, type, maxBookNum, page);
            Log.i("MainActivity.getSearchUrl", url);
            return url;
        }

        public void resetName() {
            Log.i("beforeName", beforeName);
            if (LibSearchActivity.this.beforeName.equals("") == false) {
                try {
                    searchName = new String(beforeName.getBytes(), "UTF-8");
                    Log.i(TAG, "resetName");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i(TAG, "onPause");
        if (NEXT_BOOKACTIVITY == true) {
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            NEXT_BOOKACTIVITY = false;
        } else {
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

}
