package com.lib.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andybrier.lib.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

  public final static int LISTVIEW_ACTION_INIT = 0x01;
  public final static int LISTVIEW_ACTION_REFRESH = 0x02;
  public final static int LISTVIEW_ACTION_SCROLL = 0x03;
  public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

  public final static int LISTVIEW_DATA_MORE = 0x01;
  public final static int LISTVIEW_DATA_LOADING = 0x02;
  public final static int LISTVIEW_DATA_FULL = 0x03;
  public final static int LISTVIEW_DATA_EMPTY = 0x04;

  public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
  public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
  public final static int LISTVIEW_DATATYPE_POST = 0x03;
  public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
  public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
  public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
  public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

  public final static int REQUEST_CODE_FOR_RESULT = 0x01;
  public final static int REQUEST_CODE_FOR_REPLY = 0x02;


  public static void toast(Context context, int stringId) {
    Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show();
  }


}
