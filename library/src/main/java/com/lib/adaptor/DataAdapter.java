package com.lib.adaptor;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.andybrier.lib.R;
import com.lib.activity.library.BookPreReserveActivity;
import com.lib.bean.lib.BookReserveInfo;
import com.lib.bean.lib.GetBookLoction;
import com.lib.util.HttpUrl;
import com.lib.util.LogUtils;
import com.lib.util.UrlUtils;

import java.net.URLEncoder;
import java.util.List;

public class DataAdapter extends ArrayAdapter<BookReserveInfo> {

    private final BookPreReserveActivity myContext;

    private Handler reserveHandler;

    public DataAdapter(Handler reserveHandler, Activity context, int rowViewId,
                       List<BookReserveInfo> infos) {
        super(context, rowViewId, infos);
        myContext = (BookPreReserveActivity) context;

        this.reserveHandler = reserveHandler;
    }

    static class ViewHolder {
        protected TextView getCode;
        protected TextView location;
        protected TextView canReserve;

        protected Spinner spin;
        protected Button button;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        // Check to see if this row has already been painted once.
        if (convertView == null) {
            // If it hasn't, set up everything:
            LayoutInflater inflator = myContext.getLayoutInflater();
            view = inflator.inflate(R.layout.book_pre_reserve_item, null);

            // Make a new ViewHolder for this row, and modify its data and spinner:
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.getCode = (TextView) view.findViewById(R.id.res_get_code);
            viewHolder.location = (TextView) view.findViewById(R.id.res_location);
            viewHolder.canReserve = (TextView) view.findViewById(R.id.res_can_reserve);

            viewHolder.spin = (Spinner) view.findViewById(R.id.m_sp_time_type);
            viewHolder.button = (Button) view.findViewById(R.id.btnPreg);
            viewHolder.button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View buttonView) {

                    myContext.getReservdialog().show();

                    GetBookLoction loction = (GetBookLoction) viewHolder.spin.getSelectedItem();

                    String macroUrl = myContext.getIntent().getExtras().getString("macroUrl");

                    new ReserveThread(loction, macroUrl).start();

                }
            });
            // Update the TextView to reflect what's in the Spinner
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        // This is what gets called every time the ListView refreshes
        ViewHolder holder = (ViewHolder) view.getTag();
        BookReserveInfo info = getItem(position);
        holder.getCode.setText(myContext.getString(R.string.get_book_code) + ": " + info.getCallNo());
        holder.location.setText(myContext.getString(R.string.location) + ": " + info.getLocation());

        String canRes = info.getCanReserve();
        String status = myContext.getString(R.string.res_status) + ": ";
        if (canRes != null) {

            holder.canReserve.setText(status + canRes);
            holder.spin.setEnabled(false);
            holder.button.setEnabled(false);
            holder.canReserve.setTextColor(myContext.getResources().getColor(R.color.red));
        } else {
            canRes = myContext.getString(R.string.res_status_ok);
            holder.canReserve.setText(status + canRes);
            holder.canReserve.setTextColor(myContext.getResources().getColor(R.color.gree));
        }

        ArrayAdapter<GetBookLoction> spinAdapter =
                new ArrayAdapter<GetBookLoction>(myContext.getBaseContext(),
                        android.R.layout.simple_spinner_item, info.getGetBookLocation());
        holder.spin.setAdapter(spinAdapter);

        return view;
    }


    class ReserveThread extends Thread {

        GetBookLoction loction;
        String macroUrl;

        public ReserveThread(GetBookLoction loction, String macroUrl) {
            this.loction = loction;
            this.macroUrl = macroUrl;
        }

        @Override
        public void run() {

            final String callno_no = loction.getBookReserveInfo().getHiddenCallNoName();
            final String callno_value =
                    URLEncoder.encode(loction.getBookReserveInfo().getHiddenCallNoValue());

            final String loca_no = loction.getBookReserveInfo().getHiddenLocationName();
            final String loca_value =
                    URLEncoder.encode(loction.getBookReserveInfo().getHidenLocationValue());

            final String take_localNo = loction.getLocationCode();
            final String url =
                    UrlUtils.getReservUrl(macroUrl, take_localNo, callno_no, callno_value, loca_no,
                            loca_value);
            LogUtils.log("reserve url", url);
            String retString = new HttpUrl().get(url);

            LogUtils.log("reserve url", retString);
            //boolean ret = ContentParse.parseReserveResult(retString);
            //if (ret) {
            if(retString.indexOf("预约申请成功")>0){
                Message msg = new Message();
                msg.what = 0;
                reserveHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                reserveHandler.sendMessage(msg);
            }
        }
    }
}
