package com.lib.activity.room.adaptor;

import java.util.List;

import com.andybrier.lib.R;
import com.lib.activity.room.RoomSearchActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RoomBookAdaptor extends ArrayAdapter<RoomBookingInfo> {


  private final List<RoomBookingInfo> list;

  private final RoomSearchActivity context;

  public RoomBookAdaptor(RoomSearchActivity context, int itemLayout, List<RoomBookingInfo> list) {
    super(context, itemLayout, list);
    this.context = context;
    this.list = list;
  }



  @Override
  public View getView(final int position, View v, ViewGroup parent) {
    // Keeps reference to avoid future findViewById()
    ViewHolder viewHolder;

    if (v == null) {
      LayoutInflater li =
          (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = li.inflate(R.layout.room_booking_item, parent, false);

      viewHolder = new ViewHolder();
      viewHolder.txName = (TextView) v.findViewById(R.id.name);
      viewHolder.txRoom = (TextView) v.findViewById(R.id.room);
      viewHolder.txTime = (TextView) v.findViewById(R.id.room_time);
      viewHolder.txDesc = (TextView) v.findViewById(R.id.room_desc);
      viewHolder.btCancle = (Button) v.findViewById(R.id.btn);

      v.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) v.getTag();
    }

    final RoomBookingInfo info = list.get(position);
    if (info != null) {
      viewHolder.txName.setText(  info.getName());
      viewHolder.txRoom.setText(context.getString(R.string.scoolroom) + ": " + info.getRoom());
      viewHolder.txTime.setText(context.getString(R.string.m_time) + ": " + info.getTime());
      viewHolder.txDesc.setText(context.getString(R.string.m_desc_short) + ": " + info.getDesc());

      viewHolder.btCancle.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {

          context.getProgressDialog().show();
          
          CancleTask cr = new CancleTask(info, context.getP(), context.getCancleHandle(),position);
          cr.start();
        }
      });
    }
    return v;
  }

  static class ViewHolder {
    Button btCancle;
    TextView txName;
    TextView txRoom;
    TextView txTime;
    TextView txDesc;
  }

}
