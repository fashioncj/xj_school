package com.lib.activity.setting;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.IndexActivity;
import com.lib.bean.lib.Person;
import com.lib.database.DBHelper;
import com.lib.util.ContentParse;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.UrlUtils;

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 保存个人信息
 *
 * @author Administrator
 */
public class LoginActivity extends Activity {

    private final static String TAG = "LoginActivity";

    private LibraryApplication app;

    private Handler mHandler;


    private Button loginButton;
    private EditText editNumber;
    private EditText editPasswd;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editCode;
    private ImageView iv_code;

    private String loginName;
    private String loginPass;
    private String loginCode;
    private String email = "";

    AlertDialog okAlertDialog;
    AlertDialog failDialog;

    private ProgressDialog progressBar;

    private int isfromSplash = 0;

    Bitmap code_bitmap = null;

    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isfromSplash = this.getIntent().getIntExtra("isFromSplash", 0);

        app = (LibraryApplication) getApplication();

        // processor bar
        progressBar = new ProgressDialog(this);
        progressBar.setMessage(getString(R.string.processing));
        progressBar.setCanceledOnTouchOutside(false);

        // 不能为空
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.login_not_null)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int arg1) {
                        _dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();

        AlertDialog.Builder builderOK = new AlertDialog.Builder(this);
        builderOK.setMessage(getString(R.string.save_info_ok)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int arg1) {
                        _dialog.dismiss();

                        startActivity(new Intent(getApplication(), IndexActivity.class));
                        LoginActivity.this.finish();
                    }
                });
        okAlertDialog = builderOK.create();


        AlertDialog.Builder builderFail = new AlertDialog.Builder(this);
        builderFail.setMessage(getString(R.string.login_failed)).setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int arg1) {
                        _dialog.dismiss();
                    }
                });
        failDialog = builderFail.create();


        editNumber = (EditText) findViewById(R.id.student_number);
        editPasswd = (EditText) findViewById(R.id.student_passwd);
        editPhone = (EditText) findViewById(R.id.student_phone);
        editEmail = (EditText) findViewById(R.id.student_email);
        editCode = (EditText) findViewById(R.id.et_login_code);
        iv_code = (ImageView) findViewById(R.id.iv_login_code);

        new ImageThread().start();


        dbHelper = ((LibraryApplication) getApplicationContext()).dbHelper;
        Person p = dbHelper.getPersonInfo();

        if (p != null) {
            editNumber.setText(p.getName());
            editPasswd.setText(p.getPass());
            editPhone.setText(p.getPhone());
            editEmail.setText(p.getEmail());

        }
        loginButton = (Button) findViewById(R.id.loginOK);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                String name = editNumber.getText().toString();
                String password = editPasswd.getText().toString();
                String code = editCode.getText().toString();

                if (StringUtil.isBlank(name) || StringUtil.isBlank(password)||StringUtil.isBlank(code)) {
                    dialog.show();
                } else {

                    loginName = name;
                    loginPass = password;
                    loginCode = code;

                    new LoginThread().start();
                    progressBar.show();
                }
            }
        });


        // 取消
        Button cancelButton = (Button) findViewById(R.id.loginCancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //打开index
                if (isfromSplash == 1) {
                    startActivity(new Intent(getApplication(), IndexActivity.class));
                }
                //关闭
                else {
                    LoginActivity.this.finish();
                }

            }
        });

        iv_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageThread().start();
            }
        });

        mHandler = new MyHandler();

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            progressBar.dismiss();

            switch (msg.what) {
                case 0: {
                    okAlertDialog.show();

                    dbHelper.delete();
                    dbHelper.insert(loginName, loginPass, "", email);

                    ListIterator<Cookie> iterator=HttpUrl.getCookieStore().getCookies().listIterator();
                    while (iterator.hasNext()){
                        Cookie cookie=iterator.next();
                        LogUtils.log(TAG,cookie.toString());
                        if(cookie.getName().compareTo("PHPSESSID")==0){
                            SharedPreferences.Editor editor=getSharedPreferences("Personal",MODE_PRIVATE).edit();
                            editor.putString("cookies_name",cookie.getName());
                            editor.putString("cookies_value",cookie.getValue());
                            editor.putString("cookies_domain",cookie.getDomain());
                            editor.commit();
                        }
                    }


                    break;
                }
                case 1: {
                    failDialog.show();
                    new ImageThread().start();
                    break;
                }
                case 3: {
                    iv_code.setImageBitmap(code_bitmap);
                    break;
                }
                case 4:
                    Toast.makeText(LoginActivity.this, R.string.login_code_fail_hint, Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    class LoginThread extends Thread {

        @Override
        public void run() {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("number", loginName));
            params.add(new BasicNameValuePair("passwd", loginPass));
            params.add(new BasicNameValuePair("returnUrl", ""));
            params.add(new BasicNameValuePair("select", "ldap"));
            params.add(new BasicNameValuePair("captcha", loginCode));
            try {
                HttpUrl postUrl = new HttpUrl();
                String result = postUrl.post(UrlUtils.getLoginUrl(), params);

                Log.i(TAG, "null"+result);

//                boolean loginRet = ContentParse.parseLogin(result);
//                if (loginRet) {
                if (result.indexOf("span class=\"profile-name\"")>0) {
                    Log.i(TAG, "Login success");
                    //获取个人信息
                    HttpUrl getUserInfo = new HttpUrl();
                    String info_result = getUserInfo.get(UrlUtils.getUserInfoUrl());
                    email = ContentParse.parserUserInfo(info_result).getEmail();


                    app.setLibcookie(true);

                    Message msg = new Message();
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                } else {

                    Log.i(TAG, "Login failed");

                    app.setLibcookie(false);

                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    class ImageThread extends Thread {

        @Override
        public void run() {

            try {
                HttpUrl imageurl = new HttpUrl();
                Bitmap result = imageurl.getBitmapFromServer(UrlUtils.getverifyCode());

                if (result != null) {
                    code_bitmap = result;
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 4;
                    mHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
