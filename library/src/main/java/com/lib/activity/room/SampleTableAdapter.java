package com.lib.activity.room;

import java.util.List;

import com.lib.util.LogUtils;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This class implements the main functionalities of the TableAdapter in Mutuactivos.
 * 
 * 
 * @author Brais Gab韓
 */
public abstract class SampleTableAdapter extends BaseTableAdapter {
  private final Context context;
  private final LayoutInflater inflater;

  /**
   * Constructor
   * 
   * @param context The current context.
   */
  public SampleTableAdapter(Context context) {
    this.context = context;
    inflater = LayoutInflater.from(context);
  }

  /**
   * Returns the context associated with this array adapter. The context is used to create views
   * from the resource passed to the constructor.
   * 
   * @return The Context associated with this adapter.
   */
  public Context getContext() {
    return context;
  }

  /**
   * Quick access to the LayoutInflater instance that this Adapter retreived from its Context.
   * 
   * @return The shared LayoutInflater.
   */
  public LayoutInflater getInflater() {
    return inflater;
  }

  @Override
  public View getView(int row, int column, View converView, ViewGroup parent) {
    if (converView == null) {
      converView = inflater.inflate(getLayoutResource(row, column), parent, false);
    }
    setText(converView, getCellString(row, column));


    // onclick: 第一行不用
    if (row >= 0 && column >= 0) {

      String time = getCellString(-1, column );

      converView.setOnClickListener(new TableOnclickListener(row, column,time));

    }
    return converView;
  }

  class TableOnclickListener implements OnClickListener {

    private int row;
    private int col;
    private int year;
    private int mon;
    private int day;
    private String time;

    public TableOnclickListener(int row, int col, String time) {
      this.col = col;
      this.row = row;
      this.year = Data.year;
      this.mon = Data.mon;
      this.day = Data.day;
      this.time = time;
    }

    @Override
    public void onClick(View view) {

      LogUtils.log("onclick row", "" + row);
      LogUtils.log("on click col", "" + col);

      String build = Data.buildId;
      String room = Data.roomData.get(build)[row];


      LogUtils.log("build", "" + build);
      LogUtils.log("room", "" + room);
      LogUtils.log("year", "" + year);
      LogUtils.log("mon", "" + mon);
      LogUtils.log("day", "" + day);
      LogUtils.log("time", "" + time);

      Intent tostart = new Intent(context, RoomBookActivity.class);
      tostart.putExtra("year", year);
      tostart.putExtra("mon", mon);
      tostart.putExtra("day", day);
      tostart.putExtra("build", build);
      tostart.putExtra("room", room);
      tostart.putExtra("time", time);

      TextView tView = (TextView) (view.findViewById(android.R.id.text1));

      if (RoomUtils.isRoomBookingAble(tView.getText().toString())) {
        context.startActivity(tostart);
      }
    }
  }

  /**
   * Sets the text to the view.
   * 
   * @param view
   * @param text
   */
  private void setText(View view, String text) {
    ((TextView) view.findViewById(android.R.id.text1)).setText(text);
  }

  /**
   * @param row the title of the row of this header. If the column is -1 returns the title of the
   *        row header.
   * @param column the title of the column of this header. If the column is -1 returns the title of
   *        the column header.
   * @return the string for the cell [row, column]
   */
  public abstract String getCellString(int row, int column);

  public abstract int getLayoutResource(int row, int column);
}
