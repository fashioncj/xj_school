package com.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.setting.LoginActivity;
import com.lib.bean.lib.Person;
import com.lib.database.DBHelper;
import com.lib.util.HttpUrl;
import com.lib.util.StatusCheckerUtil;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    private LibraryApplication mApp;

    private String username;
    private String password;

    Handler handler;
    Person p;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mApp = ((LibraryApplication) getApplicationContext());
        handler = new MyHandler();

        DBHelper helper = mApp.dbHelper;
        p = helper.getPersonInfo();
        if (p != null) {
            username = p.getName();
            password = p.getPass();
        }


        // 检查网路情况: 网络不好，直接splash到主菜单
        boolean checkRet = StatusCheckerUtil.isNetworkOK(this);
        if (!checkRet) {
            Toast.makeText(this, getString(R.string.check_net), Toast.LENGTH_SHORT).show();
            handler.postDelayed(new splashhandler(), 1000);
            return;
        }

        // 检查是否已经注册了用户密码
        if (p == null) {
            Intent loginIntent = new Intent(getApplication(), LoginActivity.class);
            loginIntent.putExtra("isFromSplash", 1);
            startActivity(loginIntent);
            SplashActivity.this.finish();
        } else {
            //屏蔽自动登录
            LoginThread thread = new LoginThread();
            thread.start();
//            Intent loginIntent = new Intent(getApplication(), LoginActivity.class);
//            loginIntent.putExtra("isFromSplash", 1);
//            startActivity(loginIntent);
//            SplashActivity.this.finish();
        }


    }


    class splashhandler implements Runnable {

        public void run() {
            startActivity(new Intent(getApplication(), IndexActivity.class));
            SplashActivity.this.finish();
        }

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


      switch (msg.what) {
        case 0:
            startActivity(new Intent(getApplication(), IndexActivity.class));
            SplashActivity.this.finish();
          Toast
              .makeText(SplashActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT)
              .show();
          break;
        case 1:
          Toast.makeText(SplashActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT)
              .show();
            Intent loginIntent = new Intent(getApplication(), LoginActivity.class);
            loginIntent.putExtra("isFromSplash", 1);
            startActivity(loginIntent);
            SplashActivity.this.finish();
          break;
      }

//            startActivity(new Intent(getApplication(), IndexActivity.class));
//            SplashActivity.this.finish();
        }
    }

    // 登录
    class LoginThread extends Thread {

        @Override
        public void run() {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("number", username));
            params.add(new BasicNameValuePair("passwd", password));
            params.add(new BasicNameValuePair("returnUrl", ""));
            params.add(new BasicNameValuePair("select", "ldap"));
            SharedPreferences sharedPreferences=getSharedPreferences("Personal",MODE_PRIVATE);

            BasicClientCookie cookie = new BasicClientCookie(sharedPreferences.getString("cookies_name",""),sharedPreferences.getString("cookies_value",""));
            cookie.setDomain("opac.lib.xjtlu.edu.cn");
            cookie.setPath("/");
            AbstractHttpClient client=new DefaultHttpClient();
            HttpUrl.setCookieStore(client.getCookieStore());
            HttpUrl.getCookieStore().addCookie(cookie);
            Log.i("cookie",cookie.toString());
            try {
                HttpUrl postUrl = new HttpUrl();
                String result = postUrl.get("http://opac.lib.xjtlu.edu.cn/cn/reader/redr_info.php");

                Log.i("loginret", result);
//
                boolean loginRet = result.indexOf("profile_left")>0;
                //if (loginRet) {
                if (loginRet) {
                    mApp.setLibcookie(true);
                    Log.i("login", "setLibCookie is ok");
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                } else {
                    mApp.setLibcookie(false);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Log.e("exception", e.toString());
            }
        }
    }


}
