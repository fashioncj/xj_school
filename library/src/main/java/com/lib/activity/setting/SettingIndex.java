package com.lib.activity.setting;


import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.lib.LibraryApplication;
import com.lib.activity.library.AskActivity;
import com.lib.activity.library.AskActivity.btclick;
import com.lib.util.UrlUtils;

public class SettingIndex extends  Activity {

  private TableRow login, logout, choose_lang, clear_cache;
  private LibraryApplication app;
  private String[] areas;

 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);

    app = (LibraryApplication) getApplication();
    areas = new String[]{getString(R.string.chinese), getString(R.string.english)};

    
    login = (TableRow) this.findViewById(R.id.login);
    login.setOnClickListener(new btclick());

    logout = (TableRow) this.findViewById(R.id.logout);
    logout.setOnClickListener(new btclick());

    choose_lang = (TableRow) this.findViewById(R.id.ch_lang);
    choose_lang.setOnClickListener(new btclick());
    

    clear_cache = (TableRow) this.findViewById(R.id.clear_cache);
    clear_cache.setOnClickListener(new btclick());
    
 

  }



  public class btclick implements OnClickListener {

    @Override
    public void onClick(View v) {

      switch (v.getId()) {
        case R.id.login: {
          Intent personInfoIntent = new Intent(SettingIndex.this, LoginActivity.class);
          startActivity(personInfoIntent);
          break;
        }
        case R.id.logout: {
          SettingIndex.this.finish();
          break;
        }
        case R.id.ch_lang: {
          new AlertDialog.Builder(SettingIndex.this).setTitle(getString(R.string.menu_about)).setItems(areas,new DialogInterface.OnClickListener(){  
            public void onClick(DialogInterface dialog, int which){  
              
              app.changed = true;
              
              String tips = null;
              
              if( app.languageToLoad.equals("en"))
              {
                tips="Change language successfully,please restart this application by click Log out!";
              }
              else {
                tips = "修改语言成功，请点击\"退出\"重启应用！";

              }
              
              if(which == 0)
              {
                app.languageToLoad = "zh";
                
                UrlUtils.HOST = UrlUtils.HOST_ZH;
                UrlUtils.HOST_OPAC = UrlUtils.HOST_OPAC_ZH;
                
               
              }
              else
              {
                
                app.languageToLoad = "en";
                
                UrlUtils.HOST = UrlUtils.HOST_EN;
                UrlUtils.HOST_OPAC = UrlUtils.HOST_OPAC_EN;
                

              }
              
              
             //強制刷新語言
             String languageToLoad  = app.languageToLoad;
             Locale locale = new Locale(languageToLoad);
             Locale.setDefault(locale);
             Configuration config = new Configuration();
             config.locale = locale;
             getBaseContext().getResources().updateConfiguration(config, null);
             
             Toast.makeText(getBaseContext(),tips, Toast.LENGTH_SHORT).show();

             dialog.dismiss();  
            }  
         }).show();  
          break;
        }
        
        case R.id.clear_cache:
        {
          app.dbHelper.delete();

          AlertDialog.Builder builderOK = new AlertDialog.Builder(SettingIndex.this);
          builderOK.setMessage(getString(R.string.clear_cache_success)).setCancelable(false)
              .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface _dialog, int arg1) {
                  _dialog.dismiss();
                }
              });
          Dialog resultDialog = builderOK.create();
          resultDialog.show();
          
          break;
        }
        
      }

      
    }

  }

}
