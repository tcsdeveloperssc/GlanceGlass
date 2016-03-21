package com.glance.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.glance.R;
import com.glance.faye.WebPortalSocketService;
import com.glance.utils.Constants;
import com.glance.utils.Constants.FayeAlert;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.glance.view.ImageScreen;
import com.glance.view.VideoScreen;
import com.saulpower.fayeclient.FayeClient;

public abstract class BaseActivity extends FragmentActivity {

	// static Intent speechService ;

	ProgressDialog pd;

	String user_id = "", security_token = "", tenant_id = "";

	public static Handler serverMemoryOutHandler;

	public Context mContext;

	/*
	 * public CallBackListener mailListener; public CallBackListener
	 * alertListener;
	 */
	public static boolean isReceiverRegistered = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(android.R.attr.progressBarStyleHorizontal);
		GLog.d("TESTING", "Base Activity : on Create");
		mContext = this;

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		SharedPreferences pref = Utils.getCredentials(getApplicationContext());

		user_id = pref.getString(getString(R.string.preference_user_id), "");
		security_token = pref.getString(
				getString(R.string.preference_security_token), "");
		// story_id=pref.getString(getString(R.string.preference_story_id), "");
		tenant_id = pref
				.getString(getString(R.string.preference_tenant_id), "");

	}

	@Override
	protected void onResume() {
		GLog.d("MailAlert", "Base Activity : on Create");
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mAlertReceiver, new IntentFilter(FayeAlert.DEVICE_ALERT_OK));

		LocalBroadcastManager.getInstance(this).registerReceiver(mMailReceiver,
				new IntentFilter(FayeAlert.MAIL_ALERT_OK));

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mScreenStatusReceiver,
				new IntentFilter(FayeAlert.SCREEN_STATUS_OK));

		super.onResume();

	}

	@Override
	protected void onPause() {
		GLog.d("MailAlert", "Base Activity : onPause");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mAlertReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMailReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mScreenStatusReceiver);
		super.onPause();
	}

	public void showCustomisedDialog(String message) {
		/*
		 * if(pd == null){ pd = new ProgressDialog(this);
		 * pd.setProgressStyle(android.R.attr.progressBarStyleHorizontal); }
		 * pd.setMessage(message); pd.setCancelable(false); pd.show();
		 */
	}

	public void dismissDialog() {
		/*
		 * if (pd != null && pd.isShowing()) { pd.dismiss(); pd.cancel(); }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (R.id.action_logout == item.getItemId()) {

		}
		return true;
	}

	public void logOut() {
		Utils.clearSharedPref(mContext);
		Intent mainPage = new Intent(mContext, MenuActivity.class);
		/*
		 * mainPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		 * mainPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 */
		GLog.d("TCS", "************ LOGOUT IN BASE CALLED ************* ");
		startActivity(mainPage);
		finish();
	}

	public BroadcastReceiver mMailReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// Get extra data included in the Intent
			if (null != intent) {
				if (Keywords.SUCCESS == intent.getStringExtra(Keywords.DATA)) {
					Toast.makeText(mContext, "New Mail Received",
							Toast.LENGTH_SHORT).show();
					Utils.putToPreference(mContext, Constants.NEW_MAIL, Constants.YES);
					getMails();
				} else {
					// Do SOmething else
				}
			}

		}

	};

	public BroadcastReceiver mAlertReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// Get extra data included in the Intent
			if (null != intent) {
				if (Keywords.SUCCESS == intent.getStringExtra(Keywords.DATA)) {

					GLog.d("TASK RECEIVED", "NEW TASK********************");
					Toast.makeText(mContext, "New Task Received",
							Toast.LENGTH_SHORT).show();
					Utils.putToPreference(mContext, Constants.NEW_MAIL, Constants.YES);
					getUserTasks();

				} else {
					// Do SOmething else
				}
			}

		}

	};

	public BroadcastReceiver mScreenStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d("mScreenStatusReceiver", "Came Here");
			if (null != intent) {
				if (Keywords.SCREEN_STATUS == intent
						.getStringExtra(Keywords.DATA)) {

					if (mContext instanceof HotSpotActivity) {
						HotSpotActivity activity = ((HotSpotActivity) mContext);
						if (activity.getCurrentHspot() == null)
							activity.sendOnlineStatusToSocket("hotspotrunning",
									"nil", null, null, null, null);
						else
							activity.sendOnlineStatusToSocket("hotspotrunning",
									activity.getCurrentHspot(), null, null,
									null, null);
					} 
					else if (mContext instanceof VideoScreen){
						VideoScreen activity = ((VideoScreen) mContext);
						activity.sendOnlineStatusToSocket();
					}else if (mContext instanceof ImageScreen){
						ImageScreen activity = ((ImageScreen) mContext);
						activity.sendOnlineStatusToSocket();
					}
						else {

						sendOnlineStatusToSocket("hotspotnotrunning", "nil",
								null, null, null, null);
					}

				}
			}

		}

	};

	public void sendOnlineStatusToSocket(String status, String hotspot_id,
			String link_node, String tId, String stId, String pNode) {

		JSONObject olineJsonObj = new JSONObject();
		try {
			if (null == tId) {
				olineJsonObj.put("taskId", null);
				olineJsonObj.put("storyId", null);
			} else {
				olineJsonObj.put("taskId", tId);
				olineJsonObj.put("storyId", stId);
			}
			olineJsonObj.put("status", status);
			olineJsonObj.put("hotspotId", hotspot_id);
			SharedPreferences pref = Utils.getCredentials(mContext);
			user_id = pref.getString(
					mContext.getString(R.string.preference_user_id), "");
			olineJsonObj.put("userId", user_id);
			if (link_node != null) {
				olineJsonObj.put("currentNode", link_node);

			} else if (pNode != null) {
				olineJsonObj.put("currentNode", pNode);
			} else {
				olineJsonObj.put("currentNode", null);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		GLog.d("WEBPORTAL", "Json req is " + olineJsonObj.toString());
		FayeClient fayeClient = WebPortalSocketService.fayeClientportal;
		if (null != fayeClient) {
			fayeClient.sendMessage(olineJsonObj);
		} else {
			MenuActivity.fayeWebPortalIntent = new Intent(
					getApplicationContext(), WebPortalSocketService.class);
			startActivity(MenuActivity.fayeWebPortalIntent);
			fayeClient = WebPortalSocketService.fayeClientportal;
			if (null != fayeClient) {
				fayeClient.sendMessage(olineJsonObj);
			}
		}

	}

	public abstract void getUserTasks();

	public abstract void getMails();

	

}
