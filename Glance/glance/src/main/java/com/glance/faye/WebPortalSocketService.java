package com.glance.faye;

import java.net.URI;

import org.json.JSONObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.glance.utils.Constants;
import com.glance.utils.Utils;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

public class WebPortalSocketService extends Service implements FayeListener {

	public final String TAG = this.getClass().getSimpleName();

	public static FayeClient fayeClientportal;

	public final String CHANNEL = "taskqueuestatuschange";

	private Context mContext;

	final static int myID = 1235;

	@Override
	public void onCreate() {

		super.onCreate();
		mContext = this.getApplicationContext();
//		Toast.makeText(mContext, "Visual Play : Faye Started", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "***************** FAYE STARTED portal ******************* ");
		if (android.os.Build.VERSION.SDK_INT <= 8) {
			return 0;
		}
		initialize();

		showNotification();
		return START_STICKY;
	}

	public void initialize() {

		String baseUrl = Utils.getFayeUrl(mContext);
		URI uri = URI.create(String.format("wss://%s", baseUrl));//
		String channel = String.format("/%s", CHANNEL);
		Log.d(TAG, "*** Channel **** " + channel);
		Log.d(TAG, "*** uri **** " + uri.getQuery());
		fayeClientportal = new FayeClient(new Handler(), uri, channel);
		fayeClientportal.setFayeListener(this);
		fayeClientportal.connectToServer(Utils.getOnlineUserMessage());

	}

	@Override
	public void connectedToServer() {
		System.out.println(" Connected to Server ");
		Log.i(TAG, "Connected to Server");
		Log.i(Constants.TAG, String.format("  Connected to Server  on Faye "));
	}

	@Override
	public void disconnectedFromServer() {
		System.out.println(" Disonnected to Server  ");
		Log.i(TAG, "Disonnected to Server");
		Log.i(Constants.TAG, String.format("Disonnected to Server "));
	}

	@Override
	public void subscribedToChannel(String subscription) {
		System.out.println(" Subscribed to channel  on Faye  ");
		Log.i(TAG,
				String.format("Subscribed to channel  on Faye", subscription));
		Log.i(Constants.TAG, String.format("  Subscribed to channel  on Faye "));
	}

	@Override
	public void subscriptionFailedWithError(String error) {
		System.out.println(" Subscription failed with error:  ");
		Log.i(TAG, String.format("Subscription failed with error: %s", error));
		Log.i(Constants.TAG,
				String.format("Subscription failed with error : ", error));
	}

	@Override
	public void messageReceived(JSONObject json) {

		Log.i(TAG, "Json recieved :: " + json.toString());

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static FayeClient getFayeClient() {
		return fayeClientportal;
	}

	@Override
	public void onDestroy() {
//		Toast.makeText(mContext, "Visual Play : Faye Killed", Toast.LENGTH_LONG).show();
		fayeClientportal.setFayeListener(null);
		fayeClientportal.disconnectFromServer();
		fayeClientportal = null;
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void showNotification() {

		// The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent();
		PendingIntent pendIntent = PendingIntent
				.getActivity(this, 0, intent, 0);

		// This constructor is deprecated. Use Notification.Builder instead
		// Notification notice = new Notification(R.drawable.app_icon,
		// "Fit4Life", System.currentTimeMillis());

		Notification notice = new Notification();
		notice.setLatestEventInfo(mContext, "", "", pendIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(myID, notice);
	}

}
