package com.glance.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.glance.R;
import com.glance.activity.BaseActivity;
import com.glance.controller.core.StreamListener;
import com.glance.faye.WebPortalSocketService;
import com.glance.utils.ArtiFactImageTask;
import com.glance.utils.Constants;
import com.glance.utils.Utils;
import com.saulpower.fayeclient.FayeClient;

public class VideoScreen extends BaseActivity {

	private WifiManager wifiManager;
	private WifiLock wifiLock;
	public static VideoView videoView = null;
	private ProgressBar pBar;
	private ArtiFactImageTask artTask;
	private Context mContext;
	private String taskId, storyId, status, currentNode, hotspotId;
	private ArrayList<String> currentScreenStatus = null;
	private StreamListener videoListener = new StreamListener() {

		@Override
		public void onSuccess(String imageName) {
			if (pBar != null)
				pBar.setVisibility(View.GONE);

			String videoUrl = null;
			videoUrl = imageName;
			int index = Constants.getVideoIndex(videoUrl);
			if (index == 0) {
				videoUrl = "android.resource://" + getPackageName() + "/"
						+ R.raw.video1;
			} else {
				videoUrl = "android.resource://" + getPackageName() + "/"
						+ R.raw.videone;
			}
			System.out.println("videoUrl->" + videoUrl);
			// videoUrl = Utils.getVideoResourceUrl(VideoScreen.this,videoUrl);
			// /// For getting resource url. Remove this line after demo

			videoView = (VideoView) findViewById(R.id.video_view);

			// videoUrl = "android.resource://" + getPackageName() + "/" +
			// R.raw.videone; hard video in resource
			if (null != videoUrl && !"".equals(videoUrl)) {

				videoView.setVideoURI(Uri.parse(videoUrl));
				videoView.setMediaController(new MediaController(mContext));
				videoView.requestFocus();

				videoView.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {

						finish();

					}
				});

				videoView.start();
			}

			wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL,
					"LockTag");
			getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		}

		@Override
		public void onFinish() {

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContext = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_screen);
		// For sending work started message to user

		pBar = (ProgressBar) findViewById(R.id.pb_video);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		currentScreenStatus = intent
				.getStringArrayListExtra("currentScreenStatus");

		String id = (String) bundle.get("id");
		if (pBar != null)
			pBar.setVisibility(View.VISIBLE);
		artTask = new ArtiFactImageTask(mContext, "video", videoListener);
		artTask.execute(id);

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
		if (null != wifiLock && !wifiLock.isHeld()) {
			wifiLock.acquire();
		}
//		sendOnlineStatusToSocket();

		super.onResume();
	}

	@Override
	protected void onPause() {

		if (null != wifiLock && wifiLock.isHeld()) {
			wifiLock.release();
		}

		super.onPause();
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
