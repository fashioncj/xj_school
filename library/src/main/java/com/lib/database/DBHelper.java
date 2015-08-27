package com.lib.database;

import com.lib.bean.lib.Person;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
  private final static String DATABASE_NAME = "libDB";
  private final static int DATABASE_VERSION = 1;
  private static final String tableName = "localContact";
  private static final String nameRow = "myName";
  private static final String passRow = "myPassword";
  private static final String phone = "phone";
  private static final String email = "email";
  
  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " ("/* +ID+" INTEGER PRIMARY KEY," */
        + nameRow + " VARCHAR," 
        + passRow + " VARCHAR," 
        + phone   + " VARCHAR," 
        + email	  + " VARCHAR)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // 删除以前的旧表，创建一张新的空表
    db.execSQL("DROP TABLE IF EXISTS " + tableName);
    onCreate(db);
  }


  /**
   * 添加数据
   */
  public long insert(String name, String password,String phone, String email) {
    SQLiteDatabase db = getWritableDatabase();// 获取可写SQLiteDatabase对象
    // ContentValues类似map，存入的是键值对
    
    String inPhone = phone == null?  "" : phone;
    String inEmail = email == null?  "" : email;

    ContentValues contentValues = new ContentValues();
    contentValues.put(DBHelper.nameRow, name);
    contentValues.put(DBHelper.passRow, password);
    contentValues.put(DBHelper.phone, inPhone);
    contentValues.put(DBHelper.email, inEmail);
    return db.insert(tableName, null, contentValues);
  }

  public Person getPersonInfo() {
    Person p = null;
    Cursor cursor = getReadableDatabase().query(tableName, null, null, null, null, null, null);
    if (cursor.moveToFirst()) {
      p= new Person();
      p.setName( cursor.getString(0));
      p.setPass(cursor.getString(1));
      p.setPhone(cursor.getString(2));
      p.setEmail(cursor.getString(3));
    }
    return p;
  }

  /**
   * 删除数据
   */
  public void delete() {
    SQLiteDatabase db = getWritableDatabase();
    db.delete(tableName, null, null);
  }

}
