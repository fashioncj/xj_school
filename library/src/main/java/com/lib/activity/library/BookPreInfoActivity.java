package com.lib.activity.library;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.StatusCheckerUtil;
import com.lib.util.UrlUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 查询我的预约图书信息
 *
 * @author Administrator
 */
public class BookPreInfoActivity extends Activity {

    private final List<Map<String, String>> list = new LinkedList<Map<String, String>>();
    // Progress Dialog
    private ProgressDialog pDialog;

    private ListView lv_reserve;

    private MyHandler mHandler;

    private BaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_pre_info);

        lv_reserve = (ListView) findViewById(R.id.reservelist);

        adapter = new MyAdapter();
        lv_reserve.setAdapter(adapter);

        pDialog = new ProgressDialog(BookPreInfoActivity.this);
        pDialog.setMessage(getString(R.string.processing));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        String checkRet = StatusCheckerUtil.checkBoth(this);
        if (checkRet != null) {
            Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }


        mHandler = new MyHandler();


        new LoadThread().start();

    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0: {
                    Toast.makeText(BookPreInfoActivity.this, getString(R.string.delete_reserve_success),
                            Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 1: {
                    Toast.makeText(BookPreInfoActivity.this, getString(R.string.delete_reserve_fail),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                case 2: {
                    adapter.notifyDataSetChanged();
                    break;
                }
            }

        }
    }

    class CancelRunnable implements Runnable {
        private int num;

        public CancelRunnable(int _num) {
            num = _num;
        }

        @Override
        public void run() {

            HashMap<String, String> map = (HashMap<String, String>) lv_reserve.getItemAtPosition(num);

            String local_code = map.get("local_code_in");
            String marc_no = map.get("marc_no");
            marc_no = marc_no.substring(17);
            String cancelUri = UrlUtils.getCancelReserveUrl(local_code, marc_no, map.get("loca").trim());
            String result = new HttpUrl().get(cancelUri);

            if (result == null) {
                result = "<font color='red'>error</font>";
            }
            LogUtils.log("Cancel reserve", cancelUri);

            pDialog.dismiss();

            boolean isCancelSuccess = ContentParse.parseCancelReserve(result);

            if (isCancelSuccess) {
                list.remove(num);
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }

    }

    class LoadThread extends Thread {


        @Override
        public void run() {

            String content = new HttpUrl().get(UrlUtils.getPregUrl());
            ContentParse.parsePregInfo(content, list);

            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);

            pDialog.dismiss();
        }


    }


    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = BookPreInfoActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.preg_item, null);

            final TextView title = (TextView) view.findViewById(R.id.title);
            final TextView author = (TextView) view.findViewById(R.id.author);
            final TextView get_code = (TextView) view.findViewById(R.id.get_code);
            final TextView book_time = (TextView) view.findViewById(R.id.book_time);
            final TextView invalidate_time = (TextView) view.findViewById(R.id.invalidate_time);
            final TextView location = (TextView) view.findViewById(R.id.location);
            final TextView status = (TextView) view.findViewById(R.id.status);

            Button button = (Button) view.findViewById(R.id.btn);
            button.setTag(position);

            title.setText(list.get(position).get("reserve_name"));
            author.setText(getString(R.string.lib_author) + ":" + list.get(position).get("author"));
            get_code.setText(getString(R.string.get_book_code) + ":"
                    + list.get(position).get("local_code"));
            book_time
                    .setText(getString(R.string.book_date) + ":" + list.get(position).get("reserve_day"));
            invalidate_time.setText(getString(R.string.end_date) + ":"
                    + list.get(position).get("end_day"));
            location.setText(getString(R.string.get_book_loc) + ":"
                    + list.get(position).get("book_local"));
            status.setText(getString(R.string.res_status) + ":"
                    + list.get(position).get("reserve_status"));

            if(list.get(position).get("reserve_status").compareTo("已到书")==0||list.get(position).get("reserve_status").indexOf("to be collected")>0){
                button.setEnabled(false);
            }

            button.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    int position = Integer.parseInt(v.getTag().toString());

                    CancelRunnable cr = new CancelRunnable(position);
                    new Thread(cr).start();

                    pDialog.show();

                }
            });

            return view;
        }

    }
}
