package com.glance.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.glance.R;
import com.glance.activity.BaseActivity;
import com.glance.faye.WebPortalSocketService;
import com.glance.utils.ArtiFactImageTask;
import com.glance.utils.LoadImageFromDatabaseTask;
import com.glance.utils.Utils;
import com.saulpower.fayeclient.FayeClient;

public class ImageScreen extends BaseActivity {

	private String artifactId = null;
	private RelativeLayout rl;
	private String taskId, storyId, status, currentNode, hotspotId;
	private ArrayList<String> currentScreenStatus = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.image_screen);
		rl = (RelativeLayout) findViewById(R.id.image_link_hotspot);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (!bundle.isEmpty()) {
			if (bundle.containsKey("artifactId")) {
				artifactId = bundle.getString("artifactId");
				System.out.println("artifactId->" + artifactId);

			}
		}
		currentScreenStatus = intent
				.getStringArrayListExtra("currentScreenStatus");

		if (Utils.getFromPreference(getApplicationContext(), "OFFLINE_MODE")
				.equalsIgnoreCase("true")) {
			new LoadImageFromDatabaseTask(ImageScreen.this, rl,
					LoadImageFromDatabaseTask.IMAGE_SCREEN).execute(artifactId,
					"ImageTable");

		} else {
			ArtiFactImageTask artTask = new ArtiFactImageTask(
					getApplicationContext(), rl, "image");
			artTask.execute(artifactId);
		}

	}

	public void sendOnlineStatusToSocket() {
		if (currentScreenStatus != null) {
			currentNode = currentScreenStatus.get(0);
			taskId = currentScreenStatus.get(1);
			storyId = currentScreenStatus.get(2);
			hotspotId = currentScreenStatus.get(3);
			status = currentScreenStatus.get(4);

			JSONObject olineJsonObj = new JSONObject();
			try {

				olineJsonObj.put("taskId", taskId);
				olineJsonObj.put("storyId", storyId);

				olineJsonObj.put("status", status);
				olineJsonObj.put("hotspotId", hotspotId);
				SharedPreferences pref = Utils.getCredentials(mContext);
				String user_id = pref.getString(
						mContext.getString(R.string.preference_user_id), "");
				olineJsonObj.put("userId", user_id);
				olineJsonObj.put("currentNode", currentNode);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			FayeClient fayeClient = WebPortalSocketService.fayeClientportal;
			if (null != fayeClient) {
				fayeClient.sendMessage(olineJsonObj);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
			// Toast.makeText(getApplicationContext(), "On swipe down",
			// Toast.LENGTH_SHORT).show();
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
