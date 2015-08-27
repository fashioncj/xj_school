package com.lib.activity.room;

import com.andybrier.lib.R;
import com.inqbarna.tablefixheaders.TableFixHeaders;

import android.os.Bundle;
import android.content.Context;
import android.content.res.Resources;

public class NightActivity extends BaseRoomActivity {
  /** Called when the activity is first created. */

  String buildingId;

  int roomNo;

  SampleTableAdapter baseTableAdapter;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_night);

    buildingId = getIntent().getExtras().getString("buildingName");

    roomNo = Data.roomData.get(buildingId).length;

    TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table_night);
    baseTableAdapter = new NightAdapter(this);
    tableFixHeaders.setAdapter(baseTableAdapter);

  }

  @Override
  SampleTableAdapter getAdapter() {
    return baseTableAdapter;
  }



  public class NightAdapter extends SampleTableAdapter {

    private final int width;
    private final int height;


    public NightAdapter(Context context) {
      super(context);

      Resources resources = context.getResources();

      width = resources.getDimensionPixelSize(R.dimen.table_width);
      height = resources.getDimensionPixelSize(R.dimen.table_height);
    }

    @Override
    public int getRowCount() {
      return roomNo;
    }

    @Override
    public int getColumnCount() {
      return 8;
    }

    @Override
    public int getWidth(int column) {
      if (column == -1) {
        return 130;
      }
      return width;
    }

    @Override
    public int getHeight(int row) {
      return height;
    }

    @Override
    public String getCellString(int row, int column) {

      // 返回表头
      if (row == -1) {
        switch (column) {
          case -1: {
            return getString(R.string.scoolroom);
          }
          case 0: {
            return "18:00";
          }
          case 1: {
            return "18:30";
          }
          case 2: {
            return "19:00";
          }
          case 3: {
            return "19:30";
          }

          case 4: {
            return "20:00";
          }
          
          case 5: {
            return "20:30";
          }

          case 6: {
            return "21:00";
          }
          case 7: {
            return "21:30";
          }

          default:
            break;
        }
      }

      // 第一列
      else if (column == -1) {
        return Data.roomData.get(buildingId)[row];
      }

      // 具体的数据
      else {
        String roomName = Data.roomData.get(buildingId)[row];
        return RoomUtils.getRoomSatus(roomName, column, 2);
      }

      return "-1";
    }


    @Override
    public int getLayoutResource(int row, int column) {
      final int layoutResource;
      switch (getItemViewType(row, column)) {
        case 0:
          layoutResource = R.layout.item_table1_header;
          break;
        case 1:
          layoutResource = R.layout.item_table1;
          break;
        default:
          throw new RuntimeException("wtf?");
      }
      return layoutResource;
    }

    @Override
    public int getItemViewType(int row, int column) {
      if (row < 0) {
        return 0;
      } else {
        return 1;
      }
    }

    @Override
    public int getViewTypeCount() {
      return 3;
    }
  }



}
