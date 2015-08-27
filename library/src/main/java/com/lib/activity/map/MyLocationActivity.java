package com.lib.activity.map;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andybrier.lib.R;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;
import com.lib.LibraryApplication;

public class MyLocationActivity extends MapActivity implements
		LocationListener, OnClickListener {

	MapController mMapController;// 寰楀埌mMapView鐨勬帶鍒舵潈,鍙互鐢ㄥ畠鎺у埗鍜岄┍鍔ㄥ钩绉诲拰

	GeoPoint geoPoint;

	MKSearch mKSearch;

	LocationListener mLocationListener;

	MapView mMapView;// 鍦板浘鎺т欢

	ImageButton showMap;

	LinearLayout top;

	TextView searchPoi;

	TextView searchRoute;

	String tag;

	String startAdd;

	String endAdd;

	SharedPreferences currentMsg;

	SharedPreferences.Editor editor;

	private String currentLocAddress;

	Bitmap bitmap;

	ProgressDialog processDialog;

	private BMapManager mapManager = null;
	LibraryApplication app;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_mylocation);

	    app = (LibraryApplication) getApplication();
		super.initMapActivity(app.getMapManager());

		findViews();
		mapManager = app.getMapManager();
		if (mapManager != null) {
			mapManager.start();

			initMap();
		}
	}

	public void findViews() {
		mMapView = (MapView) findViewById(R.id.bmapsView);
		showMap = (ImageButton) findViewById(R.id.showMap);
		searchRoute = (TextView) findViewById(R.id.searchRoute);
		searchPoi = (TextView) findViewById(R.id.searchPoi);
		searchRoute.setText(R.string.menu_search_route);
		searchPoi.setText(R.string.menu_poi_search);

		top = (LinearLayout) findViewById(R.id.top);
		showMap.setOnClickListener(this);
		searchRoute.setOnClickListener(this);
		searchPoi.setOnClickListener(this);

	}

	public void initMap() {
		processDialog = new ProgressDialog(MyLocationActivity.this);
		processDialog
				.setMessage(getString(R.string.loc_loading));
		processDialog.show();
		mMapController = mMapView.getController();
		mMapView.setBuiltInZoomControls(true); // 璁剧疆鍚敤鍐呯疆鐨勭缉鏀炬帶浠?		mMapView.setDrawOverlayWhenZooming(true);
		mMapView.setSatellite(false);// 璁剧疆鍗槦鍥?		mMapView.setDragMode(0);// 璁剧疆0锛氭嫋鎷芥湁鍔ㄧ敾
		mMapController.setZoom(16); // 璁剧疆鍦板浘zoom绾у埆
		// mMapView.setTraffic(true);// 瀹炴椂浜ら?淇℃伅

		mKSearch = new MKSearch();
		mKSearch.init(app.getMapManager(),
				new MySearchListener());
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location loc) {
				if (loc != null) {
					if (processDialog != null && processDialog.isShowing()) {
						processDialog.dismiss();
					}
					geoPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
							(int) (loc.getLongitude() * 1E6));

					mKSearch.reverseGeocode(geoPoint);
					mMapController.animateTo(geoPoint);
					mMapController.setCenter(geoPoint); // 璁剧疆鍦板浘涓績鐐?
					mMapView.getOverlays().clear();

					addOverLay();

				}

			}
		};
		currentMsg = getSharedPreferences("currentMsg", 0);
		editor = currentMsg.edit();

	}

	@Override
	protected void onPause() {

		if (mapManager != null) {
			mapManager.getLocationManager().removeUpdates(mLocationListener);
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		if (mapManager != null) {
			mapManager.getLocationManager().requestLocationUpdates(
					mLocationListener);
			mapManager.start();
		}
		super.onResume();
	}

	public class MySearchListener implements MKSearchListener {

		public void onGetAddrResult(MKAddrInfo info, int error) {
			if (error != 0) {
				String str = String.format("错误号：%d", error);
				Log.e("error", str);
				return;
			}
			editor.putInt("lat", info.geoPt.getLatitudeE6());
			editor.putInt("lon", info.geoPt.getLongitudeE6());
			editor.putString("city", info.addressComponents.city);
			editor.putString("district", info.addressComponents.district);
			editor.putString("street", info.addressComponents.street);
			editor.putString("completeAdd", info.addressComponents.city
					+ info.addressComponents.district
					+ info.addressComponents.street);
			editor.commit();
			currentLocAddress = String.format("位置：%s\r\n",
					info.addressComponents.city
							+ info.addressComponents.district
							+ info.addressComponents.street);
			Toast.makeText(MyLocationActivity.this, currentLocAddress,
					Toast.LENGTH_LONG).show();

		}

		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		public void onGetRGCShareUrlResult(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int error) {
		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

    @Override
    public void onGetPoiDetailSearchResult(int arg0, int arg1) {
      // TODO Auto-generated method stub
      
    }
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location location) {
		location.getLatitude();
		location.getLongitude();

	}

	/*
	 * VISIBLE:0 鎰忔?鏄彲瑙佺殑 INVISIBILITY:4 鎰忔?鏄笉鍙鐨勶紝浣嗚繕鍗犵潃鍘熸潵鐨勭┖闂?GONE:8
	 * 鎰忔?鏄笉鍙鐨勶紝涓嶅崰鐢ㄥ師鏉ョ殑甯冨眬绌洪棿
	 */

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.showMap: {
			if (top.getVisibility() == 0) {
				showMap.setImageResource(R.drawable.handle_down);
				top.setVisibility(View.GONE);
			} else if (top.getVisibility() == 8) {
				showMap.setImageResource(R.drawable.handle_up);
				top.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.searchRoute: {
			i = new Intent(MyLocationActivity.this, SearchDialogActivity.class);
			i.putExtra("flag", "");
			i.putExtra("startOrendDialogFlag", "");
			i.putExtra("map", "");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case R.id.searchPoi: {
			i = new Intent(MyLocationActivity.this, PoiActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}

		default:
			break;
		}
	}

	void addOverLay() {
		List<Overlay> overlays = mMapView.getOverlays();
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
		MyOverlay overlay = new MyOverlay(MyLocationActivity.this, mMapView);
		overlay.enableCompass();
		overlay.enableMyLocation();
		overlays.add(overlay);// 娣诲姞鑷畾涔塷verlay
	}

	public class MyOverlay extends MyLocationOverlay {

		public MyOverlay(Context arg0, MapView arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void drawMyLocation(Canvas canvas, MapView mapView,
				Location lastFix, GeoPoint myLocation, long when) {
			try {
				Projection projection = mapView.getProjection();
				Point point = new Point();
				projection.toPixels(myLocation, point);

				int x = point.x - bitmap.getWidth() / 2;
				int y = point.y - bitmap.getHeight();
				canvas.drawBitmap(bitmap, x, y, new Paint());

			} catch (Exception e) {
				super.drawMyLocation(canvas, mapView, lastFix, myLocation, when);
			}
		}
	};

}
