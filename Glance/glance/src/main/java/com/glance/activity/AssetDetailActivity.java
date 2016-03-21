package com.glance.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glance.R;
import com.glance.bean.response.GGetAssetDetailResponse;
import com.glance.controller.GAssetController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.view.RobotoTextView;

public class AssetDetailActivity extends BaseActivity {

	private static String TAG = "AssetDetailActivity";
	private TextView error_txt;
	private LinearLayout dots_holder;
	private Context mContext;
	private ProgressBar pBar;
	private String assetId;
	public static String ASSET_ID="assetId";

	private CallBackListener usermailListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			RelativeLayout l1 = (RelativeLayout)findViewById(R.id.asset_layout);
			
			
			/*if (pBar != null) {
				pBar.setVisibility(View.GONE);
			}
			Log.d("TCS", "****** Connecting complete ************ ");
			error_txt.setVisibility(View.GONE);*/

			/* GGetMailResponse */
			GGetAssetDetailResponse response2 = (GGetAssetDetailResponse) response
					.getResult();
			
			dismissDialog();
			if (response2 != null) {
				if (null != l1)
				l1.setVisibility(View.VISIBLE);
				if (null != error_txt)
				error_txt.setVisibility(View.GONE);
//				setContentView(R.layout.asset_detail);
				RobotoTextView mcName = (RobotoTextView)findViewById(R.id.machineName);
				mcName.setText(response2.getName());
				
				RobotoTextView type = (RobotoTextView)findViewById(R.id.tv_temp_val);
				type.setText(response2.getType());
				
				RobotoTextView loc = (RobotoTextView)findViewById(R.id.tv_humidity_val);
				loc.setText(response2.getLocation());
				
				/*RobotoTextView desc = (RobotoTextView)findViewById(R.id.tv_runnning_status);
				desc.setText(response2.getDescription());*/
				
			}else{
				if (error_txt != null){
					error_txt.setText("No details available for this asset");
					RelativeLayout l = (RelativeLayout)findViewById(R.id.asset_layout);
					if (l != null)
					l.setVisibility(View.GONE);
					error_txt.setVisibility(View.VISIBLE);
				}
			}

		}

		public void onError(ControllerResponse response) {
			if (error_txt != null){
				error_txt.setText("No details available for this asset");
				error_txt.setVisibility(View.VISIBLE);
			}
			if (pBar != null) {
				pBar.setVisibility(View.GONE);
			}
			dismissDialog();
			Log.i(TAG, "Got response");
		};
	};
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.asset_detail);
		error_txt = (TextView) findViewById(R.id.tv_asset_error);
		
		/*error_txt = (TextView) findViewById(R.id.tv_asset_error);
		mContext = this;
		
		pBar = (ProgressBar) findViewById(R.id.pb_asset_detail);*/
		assetId = getIntent().getStringExtra(AssetScanActivity.ASSET_ID);
		getAssetDetail();
	}

	@Override
	public void onResume() {

		super.onResume();

		if (dots_holder != null) {
			dots_holder.removeAllViews();
		}
		

		mContext = this;
	}

	private void getAssetDetail() {

		if (pBar != null) {
			pBar.setVisibility(View.VISIBLE);
		}

		ControllerRequest request = new ControllerRequest(getApplicationContext(),
				GAssetController.ACTION_GET_ASSET_DETAIL,
				new Object[] { assetId });
		request.setCallbackListener(usermailListener);
		Controller.executeAsync(request, GAssetController.class);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

			if (event.getAction() == KeyEvent.ACTION_UP) {
				Intent assetIntent = new Intent(mContext, TaskListActivity.class);
				assetIntent.putExtra(ASSET_ID, assetId);
				startActivity(assetIntent);
				return true;
			}
		}

		if (keyCode == 4) {
			finish();
			return true;
		}

		return false;
	}

	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}
	
	

}
