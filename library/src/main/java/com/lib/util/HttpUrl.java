package com.lib.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ListIterator;

public class HttpUrl {

    private final String TAG = "HttpUrl";

    public static CookieStore getCookieStore() {
        return cookieStore;
    }

    public static void setCookieStore(CookieStore cookieStore) {
        HttpUrl.cookieStore = cookieStore;
    }

    private static CookieStore cookieStore=null;

    public String get(String uri) {

        BufferedReader in = null;
        String result = null;
        try {

            DefaultHttpClient client = new DefaultHttpClient();
            // cookie 绑定
            if (cookieStore != null) {
                client.setCookieStore(cookieStore);
            }
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            result = new String(sb.toString().getBytes(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public Bitmap getBitmapFromServer(String imagePath) {


        DefaultHttpClient client = new DefaultHttpClient();
        if (cookieStore != null) {
            client.setCookieStore(cookieStore);
        }
        HttpGet get = new HttpGet(imagePath);
        Bitmap pic = null;
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            pic = BitmapFactory.decodeStream(is);   // 关键是这句代码
            // cookie 保存
            if (cookieStore == null) {
                cookieStore = ((AbstractHttpClient) client).getCookieStore();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pic;
    }


    public String getDouban(String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        return sb.toString();
    }

    public String post(String url, List<NameValuePair> params) {

        String result = null;

        try {

            HttpPost httpPost = new HttpPost(url);

            if (params != null && params.size() > 0) {

                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                httpPost.setEntity(entity);
            }

            DefaultHttpClient httpClient = new DefaultHttpClient();

            // cookie 绑定
            if (cookieStore != null) {
                httpClient.setCookieStore(cookieStore);
                ListIterator<Cookie> iterator=cookieStore.getCookies().listIterator();
                while (iterator.hasNext()){
                    Cookie cookie=iterator.next();
                    LogUtils.log(TAG+"post_cookie",cookie.toString());
                }
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);

            StringBuilder builder = new StringBuilder();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            result = builder.toString();
            Log.d(TAG, "result is ( " + result + " )");

            // cookie 保存
            cookieStore = ((AbstractHttpClient) httpClient).getCookieStore();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        Log.d(TAG, "over");
        return result;
    }


    public String post(String url, String content) {

        String result = null;

        try {

            HttpPost httpPost = new HttpPost(url);

            if (content != null && content.length() > 0) {

                httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
                StringEntity se = new StringEntity(content, HTTP.UTF_8);
                httpPost.setEntity(se);
            }

            DefaultHttpClient httpClient = new DefaultHttpClient();

            // cookie 绑定
            if (cookieStore != null) {
                httpClient.setCookieStore(cookieStore);
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);

            StringBuilder builder = new StringBuilder();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }
            result = builder.toString();
            Log.d(TAG, "result is ( " + result + " )");

            // cookie 保存
            cookieStore = ((AbstractHttpClient) httpClient).getCookieStore();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        Log.d(TAG, "over");
        return result;
    }

    public void setCookie(CookieStore cookie) {
        cookieStore = cookie;
    }
}
