/*package com.glance.faye;

import java.net.URI;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.glance.R;
import com.glance.model.GFayeRegResponse;
import com.glance.model.GlanceBaseBean;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Faye;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.google.gson.annotations.SerializedName;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

public class AlertNotificationService extends Service implements FayeListener {

	public final String TAG = "TCS";// this.getClass().getSimpleName();

	public static FayeClient fayeClient;

	public String CHANNEL = "";

	private Context mContext;

	final static int myID = 1234;

	@Override
	public void onCreate() {

		super.onCreate();
		
		mContext = this.getApplicationContext();
		Toast.makeText(mContext, "Faye Started : 1", Toast.LENGTH_SHORT).show();
		CHANNEL = Utils.getFromPreference(mContext,
				getString(R.string.preference_user_id));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG,
				"***************** FAYE STARTED for alert ******************* ");

		if (android.os.Build.VERSION.SDK_INT <= 8) {
			return 0;
		}

		// String baseUrl = Constants.WEB_SOCKET_URL;
		String baseUrl = Utils.getFayeUrl(mContext);
		URI uri = URI.create(String.format("wss://%s", baseUrl));//
		String channel = String.format("/%s", getSecureAndroidId());
		Log.d(TAG, "*** Channel **** " + channel);
		Log.d(TAG, "*** uri **** " + uri.getQuery());
		fayeClient = new FayeClient(new Handler(), uri, channel);
		fayeClient.setFayeListener(this);
		fayeClient.connectToServer(Utils.getOnlineUserMessage());

		// return super.onStartCommand(intent, flags, startId);
		showNotification();
		return START_STICKY;
	}

	private String getSecureAndroidId() {
		return Secure.getString(((ContextWrapper) mContext).getBaseContext()
				.getContentResolver(), Secure.ANDROID_ID);// +Utils.getAdditionalDeviceId();
	}

	@Override
	public void connectedToServer() {

		Log.i(TAG, "Connected to Server");
		Log.i(Constants.TAG, String.format("  Connected to Server  on Faye "));
	}

	@Override
	public void disconnectedFromServer() {

		Log.i(TAG, "Disonnected to Server");
		Log.i(Constants.TAG, String.format("Disonnected to Server "));
	}

	@Override
	public void subscribedToChannel(String subscription) {

		Log.i(TAG,
				String.format("Subscribed to channel  on Faye", subscription));
		Log.i(Constants.TAG, String.format("  Subscribed to channel  on Faye "));
	}

	@Override
	public void subscriptionFailedWithError(String error) {

		Log.i(TAG, String.format("Subscription failed with error: %s", error));
		Log.i(Constants.TAG,
				String.format("Subscription failed with error : ", error));
	}

	@Override
	public void messageReceived(JSONObject json) {

		Log.i(TAG, "Json recieved :: " + json.toString());

		try {
			String success = json.toString();
			if (success != null
					&& Pattern
							.compile(Pattern.quote(Keywords.SUCCESS),
									Pattern.CASE_INSENSITIVE).matcher(success)
							.find()) {
				GLog.d(TAG,
						String.format("Received message in if %s",
								json.toString()));
				final Intent intent = new Intent(Faye.DEVICE_REG_OK);
				intent.putExtra(Keywords.DATA, Keywords.SUCCESS);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(
						intent);
			} else {
				GFayeRegResponse responseObj = (GFayeRegResponse) GSONUtils
						.getInstance().fromJson(json.toString(),
								GFayeRegResponse.class);
				Log.i(TAG,
						"############TYPE IS ############"
								+ responseObj.getType());
				final Intent intent = new Intent(responseObj.getType());
				// intent.putExtra(Keywords.DATA, Keywords.FAILURE);
				intent.putExtra(Keywords.DATA, Keywords.SUCCESS);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(
						intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static FayeClient getFayeClient() {
		return fayeClient;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(mContext, "Faye Killed : 1", Toast.LENGTH_LONG).show();
		fayeClient.setFayeListener(null);
		fayeClient.disconnectFromServer();
		fayeClient = null;
		super.onDestroy();
	}

	public class GFayeResponse extends GlanceBaseBean {

		*//**
		 * 
		 *//*
		private static final long serialVersionUID = -2379843198423183485L;

		@SerializedName("message")
		private String message;

		public String getMessage() {
			return message;
		}

		public void setType(String message) {
			this.message = message;
		}

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
*/