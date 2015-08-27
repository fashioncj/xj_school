package com.lib.activity.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.StatusCheckerUtil;
import com.lib.util.UrlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的当前借阅
 *
 * @author Administrator
 */
public class MyLendActivity extends Activity {

    private final static String TAG = "LendFragment";

    private final static int ADD_LEND_INFO = 1;
    private final static int RENEW = 3;


    private ListView lv_lend;

    private MyHandler mHandler;
    private ProgressDialog progressDialog;


    private List<Map<String, String>> list;
    public BaseAdapter adapter;

    public String result;

    Bitmap code_bitmap = null;
    //String check_code="";
    String verify_code="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lend);


        String checkRet = StatusCheckerUtil.checkBoth(this);
        if (checkRet != null) {
            Toast.makeText(this, checkRet, Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }


        lv_lend = (ListView) findViewById(R.id.lv_lendfragment);

        // processor bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.processing));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        new HtmlThread().start();
        mHandler = new MyHandler();

        new ImageThread().start();


    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case ADD_LEND_INFO: {
                    lv_lend.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                    break;
                }
                case RENEW: {
                    Toast.makeText(MyLendActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
            }
        }
    }

    class HtmlThread extends Thread {

        @Override
        public void run() {

            final String myLendings = new com.lib.util.HttpUrl().get(UrlUtils.getMyLendUrl());

            list = ContentParse.parseMyLending(myLendings);


            adapter = new MyAdapter();


            // 提前传递List加快载入速度
            Message msg = new Message();
            msg.what = ADD_LEND_INFO;
            mHandler.sendMessage(msg);
        }

    }


    class RenewRunnable implements Runnable {

        private int num;

        public RenewRunnable(int _num) {
            this.num = _num;
        }

        @Override
        public void run() {
            HashMap<String, String> map = (HashMap<String, String>) lv_lend.getItemAtPosition(num);
            String code = map.get("lend_code");
            String checkcode=map.get("check_code");

            final String result = new com.lib.util.HttpUrl().get(UrlUtils.getRenewUrl(code,verify_code,checkcode));

            String renewResult = ContentParse.parseRenewResult(result);

            Log.i(TAG, "返回结果=" + renewResult);

            progressDialog.dismiss();

            Message msg = new Message();
            msg.what = RENEW;
            msg.obj = renewResult;
            mHandler.sendMessage(msg);
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
            LayoutInflater inflater = MyLendActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.book_lend_item, null);

            final TextView isbn = (TextView) view.findViewById(R.id.isbn);
            final TextView title_author = (TextView) view.findViewById(R.id.title_author);
            final TextView lend_time = (TextView) view.findViewById(R.id.lend_time);
            final TextView return_time = (TextView) view.findViewById(R.id.return_time);
            final TextView renew_times = (TextView) view.findViewById(R.id.renew_times);
            final TextView location = (TextView) view.findViewById(R.id.location);

            Button button = (Button) view.findViewById(R.id.btn);
            button.setTag(position);

            title_author.setText(list.get(position).get("lend_name"));
            isbn.setText(getString(R.string.get_book_code) + ": " + list.get(position).get("lend_code"));
            lend_time.setText(getString(R.string.lend_date) + ": " + list.get(position).get("lend_day"));
            return_time.setText(getString(R.string.return_date) + ": "
                    + list.get(position).get("back_day"));
            renew_times.setText(getString(R.string.renew_times) + ": "
                    + list.get(position).get("lend_times"));
            location
                    .setText(getString(R.string.get_book_loc) + ": " + list.get(position).get("location"));


            button.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    final int position = Integer.parseInt(v.getTag().toString());
                    //popup dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyLendActivity.this);
                    LayoutInflater factory = LayoutInflater.from(MyLendActivity.this);
                    final View textEntryView = factory.inflate(R.layout.dialog_verifycode, null);
                    ImageView imageView = (ImageView) textEntryView.findViewById(R.id.imageView_code);
                    final EditText editText = (EditText) textEntryView.findViewById(R.id.editText_code);
                    if (code_bitmap != null) {
                        imageView.setImageBitmap(code_bitmap);
                    } else {
                        new ImageThread().start();
                    }

                    builder.setTitle(R.string.verifycode_diaolog_title);
                    builder.setView(textEntryView);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editText.getText().length() == 4) {
                                verify_code=editText.getText().toString();

                                RenewRunnable cr = new RenewRunnable(position);
                                new Thread(cr).start();
                                progressDialog.show();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(MyLendActivity.this, R.string.code_error,Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.cancel, null);
                    builder.show();
                }
            });

            return view;
        }

    }
    class ImageThread extends Thread {

        @Override
        public void run() {

            try {
                HttpUrl imageurl = new HttpUrl();
                Bitmap result = imageurl.getBitmapFromServer("http://opac.lib.xjtlu.edu.cn/cn/reader/captcha.php");

                if (result != null) {
                    code_bitmap = result;
//                    Message msg = new Message();
//                    msg.what = 3;
//                    mHandler.sendMessage(msg);
                } else {
//                    Message msg = new Message();
//                    msg.what = 4;
//                    mHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }



}
