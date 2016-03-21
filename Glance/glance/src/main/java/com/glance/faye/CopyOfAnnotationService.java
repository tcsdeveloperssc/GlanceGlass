/*package com.glance.faye;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.glance.model.GAnnotationResponse;
import com.glance.model.GlanceBaseBean;
import com.glance.utils.Constants.FayeAlert;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.google.gson.annotations.SerializedName;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

public class CopyOfAnnotationService extends IntentService {

	public CopyOfAnnotationService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public final String TAG = "AnnotationService";// this.getClass().getSimpleName();

	public static FayeClient fayeClient;

	private static String currentServerURL;
	private static String currentChannelId;
	private static String currentServerStatus;
	private static boolean currentServerStatusSuccess;
	private static boolean isFatalServerError;
	private static String connected = "Connected";
	private static String notConnected = "Not Connected";
	Intent fayeAlertIntent;

	public final String CHANNEL = "annotationpoints";

	private Context mContext;

	@Override
	public void onCreate() {

		super.onCreate();
		mContext = this.getApplicationContext();
		// Toast.makeText(mContext, "Faye Started : 2",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG,
				"***************** FAYE STARTED for annotation ******************* ");

		if (android.os.Build.VERSION.SDK_INT <= 8) {
			return 0;
		}

		// String baseUrl = Constants.WEB_SOCKET_URL;
		String baseUrl = Utils.getFayeUrl(mContext);
		URI uri = URI.create(String.format("wss://%s", baseUrl));//
		String channel = String.format("/%s", CHANNEL);
		Log.d(TAG, "*** Channel **** " + channel);
		Log.d(TAG, "*** uri **** " + uri.getQuery());
		fayeClient = new FayeClient(new Handler(), uri, channel);
		fayeClient.connectToServer(Utils.getOnlineUserMessage());

		return super.onStartCommand(intent, flags, startId);
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
		// Toast.makeText(mContext, "Faye Killed : 2",
		// Toast.LENGTH_LONG).show();
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

		@SerializedName("text")
		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}
	public void startServer(Intent intent) {
		Log.d(TAG,
				"***************** FAYE STARTED for annotation ******************* ");

		if (android.os.Build.VERSION.SDK_INT <= 8) {
			return;
		}

		// String baseUrl = Constants.WEB_SOCKET_URL;
		String baseUrl = Utils.getFayeUrl(mContext);
		URI uri = URI.create(String.format("wss://%s", baseUrl));//
		String channel = String.format("/%s", CHANNEL);
		Log.d(TAG, "*** Channel **** " + channel);
		Log.d(TAG, "*** uri **** " + uri.getQuery());
		fayeClient = new FayeClient(new Handler(), uri, channel);
		fayeClient.connectToServer(Utils.getOnlineUserMessage());

	}

	private FayeListener fayeListener = new FayeListener() {

		@Override
		public void connectedToServer() {
			Log.i(TAG, "Connected to Server");
			currentServerStatus = connected;
			currentServerStatusSuccess = true;
			isFatalServerError = false;
		}

		@Override
		public void disconnectedFromServer() {
			Log.i(TAG, "Disonnected from Server");
			currentServerStatus = notConnected;
			currentServerStatusSuccess = false;
			isFatalServerError = false;
		}

		@Override
		public void subscribedToChannel(String subscription) {
			Log.i(TAG, String.format("Subscribed to channel %s on Faye",
					subscription));
			currentServerStatus = "Subscribed to" + subscription;
			currentServerStatusSuccess = true;
			isFatalServerError = false;
		}

		@Override
		public void subscriptionFailedWithError(String error) {
			Log.i(TAG,
					String.format("Subscription failed with error: %s", error));
			currentServerStatus = "Subscribed failed due to" + error;
			currentServerStatusSuccess = false;
			isFatalServerError = false;
		}

		@Override
		public void messageReceived(JSONObject json) {
			Log.i(LOG_TAG, String.format("Received message %s", json.toString()));
			updateMessage(json);
		}
	};


	private void updateMessage(final JSONObject json) {

		Log.i(TAG, "Json recieved :: " + json.toString());

		try {
			String success = json.toString();
			// success = loadJSONFromAsset();
			if (success != null) {
				GAnnotationResponse response = (GAnnotationResponse) GSONUtils
						.getInstance().fromJson(success,
								GAnnotationResponse.class);
				if (response != null) {

					Intent intent = new Intent(FayeAlert.POINTS_ALERT);
					intent.putExtra(Keywords.DATA, response);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(
							intent);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

	public static String getCurrentServerAddress() {
		return currentServerURL;
	}

	public static String getCurrentChannelId() {
		return currentChannelId;
	}

	public static String getCurrentServerStatus() {
		return currentServerStatus;
	}

	public static boolean isCurrentServerStatusSuccess() {
		return currentServerStatusSuccess;
	}

	public static boolean isFatalServerError() {
		return isFatalServerError;
	}

}
*/