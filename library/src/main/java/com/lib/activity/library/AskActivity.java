package com.lib.activity.library;

import com.andybrier.lib.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.Toast;

public class AskActivity extends Activity {

  private TableRow ask_phone1, ask_phone2, ask_email, renren, sina_weibo;

  String phone1;
  String phone2;
  String mail;
  String renrenUrl = "http://page.renren.com/601014993/index";
  String weiboUrl = "http://e.weibo.com/u/2430957731?ref=http%3A%2F%2Flib.xjtlu.edu.cn%2F";
  String faq = "http://222.92.148.165:8080/mbl/mobile_library_nvtitle_new.html";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.lib_ask);


    TableRow faq = (TableRow) this.findViewById(R.id.faq);
    faq.setOnClickListener(new btclick());


    ask_phone1 = (TableRow) this.findViewById(R.id.ask_phone1);
    ask_phone1.setOnClickListener(new btclick());

    ask_phone2 = (TableRow) this.findViewById(R.id.ask_phone2);
    ask_phone2.setOnClickListener(new btclick());

    ask_email = (TableRow) this.findViewById(R.id.ask_email);
    ask_email.setOnClickListener(new btclick());


    renren = (TableRow) this.findViewById(R.id.renren);
    renren.setOnClickListener(new btclick());


    sina_weibo = (TableRow) this.findViewById(R.id.sina_weibo);
    sina_weibo.setOnClickListener(new btclick());

    phone1 = (getString(R.string.ask_phone1).split(":")[1]).trim().replaceAll(" ", "");
    phone2 = (getString(R.string.ask_phone2).split(":")[1]).trim().replaceAll(" ", "");
    mail = (getString(R.string.ask_email).split(":")[1]).trim();

  }



  public class btclick implements OnClickListener {

    @Override
    public void onClick(View v) {

      Intent intent = null;

      switch (v.getId()) {
        case R.id.ask_phone1: {
          // 直接连接打电话
          intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone1));
          break;
        }
        case R.id.ask_phone2: {
          intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone2));
          break;
        }
        case R.id.ask_email: {

          intent = new Intent(Intent.ACTION_SEND);
          intent.putExtra(Intent.EXTRA_SUBJECT, "Title");
          intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"library-service@xjtlu.edu.cn"});
          intent.setType("message/rfc822");
           startActivity(Intent.createChooser(intent, "Choose Email Client"));
          break;
        }

        case R.id.renren: {
          intent = new Intent(Intent.ACTION_VIEW, Uri.parse(renrenUrl));
          break;
        }
        case R.id.sina_weibo: {
          intent = new Intent(Intent.ACTION_VIEW, Uri.parse(weiboUrl));
          break;
        }
        case R.id.faq: {
          intent = new Intent(Intent.ACTION_VIEW, Uri.parse(faq));
          break;
        }
      }

      if (intent != null) {

        if (intent.resolveActivity(getPackageManager()) != null) {
          startActivity(intent);
        } else {
          Toast.makeText(AskActivity.this, getString(R.string.no_app_install), Toast.LENGTH_SHORT)
              .show();
        }

      }
    }

  }

}
