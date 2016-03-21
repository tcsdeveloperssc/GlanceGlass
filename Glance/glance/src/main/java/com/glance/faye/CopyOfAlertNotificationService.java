package com.glance.faye;

import java.net.URI;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.glance.bean.response.GFayeRegResponse;
import com.glance.utils.Constants.Faye;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.google.gson.Gson;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

public class CopyOfAlertNotificationService extends IntentService {

	private static final String LOG_TAG = "CopyOfAlertNotificationService";
	private LocalBroadcastManager mLocalBroadcastManager = null;
	private boolean isStarted;
	private FayeClient mClient;
	private static String currentServerURL;
	private static String currentChannelId;
	private static String currentServerStatus;
	private static boolean currentServerStatusSuccess;
	private static boolean isFatalServerError;
	private static String connected = "Connected";
	private static String notConnected = "Not Connected";
	Intent fayeAlertIntent;
	
	public final String TAG = "TCS";//this.getClass().getSimpleName();

	public static FayeClient fayeClient;

	public  String CHANNEL = "";

	private Context mContext;

	
	public CopyOfAlertNotificationService(String name) {
		super(name);
		
		try {
			mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
			} catch(Exception e) {
				// ignore 
			}
			isStarted = false;
	}
	
	public CopyOfAlertNotificationService() {
	super("Service");
	}
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(LOG_TAG, "Inside handler");
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		Log.i(LOG_TAG, "onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(LOG_TAG, "onStart");
		if(!isStarted) {
			isStarted = true;
			startServer(intent);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG, "onStartCommand() " + mClient);
//		Toast.makeText(mContext, "Alert : Faye Started", Toast.LENGTH_LONG).show();
		if(!isStarted) {
			isStarted = true;
			startServer(intent);
		}
		return START_REDELIVER_INTENT;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
	
		Log.i(LOG_TAG, "onHandleIntent");
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy()");
//		Toast.makeText(mContext, "Alert : Faye Killed", Toast.LENGTH_LONG).show();
		if(isStarted) {
			isStarted = false;
			stopServer();
		}
		//FayeUtil.startFayeService(this, FayeService.getCurrentServerAddress(), FayeService.getCurrentChannelId());
		super.onDestroy();
	}

	public void stopServer() {
		if (mClient != null) {
			Log.i(LOG_TAG, "Disconnecting from existing faye client");
			mClient.unsubscribe();
			mClient.disconnect();
			mClient.disconnectFromServer();
			mClient.closeWebSocketConnection();
			mClient.setFayeListener(null);
		}
	}

	public void startServer(Intent intent) {
		String baseUrl = Utils.getFayeUrl(mContext);
		URI uri = URI.create(String.format("wss://%s", baseUrl));//
		String channel  =String.format("/%s", getSecureAndroidId());
		Log.d(TAG, "*** Channel **** "+ channel);
		Log.d(TAG, "*** uri **** "+ uri.getQuery());
		fayeClient = new FayeClient(new Handler(),uri, channel);
		fayeClient.setFayeListener(fayeListener);
		fayeClient.connectToServer(Utils.getOnlineUserMessage());

	}

	private String getSecureAndroidId(){
		return Secure.getString(((ContextWrapper) mContext).
				getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);//+Utils.getAdditionalDeviceId();
	}
	
	private FayeListener fayeListener = new FayeListener() {

		@Override
		public void connectedToServer() {
			Log.i(LOG_TAG, "Connected to Server");
			currentServerStatus = connected;
			currentServerStatusSuccess = true;
			isFatalServerError = false;
		}

		@Override
		public void disconnectedFromServer() {
			Log.i(LOG_TAG, "Disonnected from Server");
			currentServerStatus = notConnected;
			currentServerStatusSuccess = false;
			isFatalServerError = false;
		}

		@Override
		public void subscribedToChannel(String subscription) {
			Log.i(LOG_TAG, String.format("Subscribed to channel %s on Faye",
					subscription));
			currentServerStatus = "Subscribed to" + subscription;
			currentServerStatusSuccess = true;
			isFatalServerError = false;
		}

		@Override
		public void subscriptionFailedWithError(String error) {
			Log.i(LOG_TAG,
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
		Gson gson = new Gson();
		Log.i(TAG, "Json recieved :: "+json.toString());


		try{
			String success=json.toString();
			if ( success!=null && Pattern.compile(Pattern.quote(Keywords.SUCCESS), Pattern.CASE_INSENSITIVE).matcher(success).find())
			{
				GLog.d(TAG, String.format("Received message in if %s", json.toString()));
				final Intent intent = new Intent(Faye.DEVICE_REG_OK);
				intent.putExtra(Keywords.DATA, Keywords.SUCCESS);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
			}
			else{
				GFayeRegResponse responseObj = (GFayeRegResponse) GSONUtils.getInstance().fromJson(json.toString(), GFayeRegResponse.class);
				Log.i(TAG, "############TYPE IS ############"+responseObj.getType());
				final Intent intent = new Intent(responseObj.getType());
				//intent.putExtra(Keywords.DATA, Keywords.FAILURE);
				intent.putExtra(Keywords.DATA, Keywords.SUCCESS);		       
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
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
