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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.glance.bean.model.GlanceBaseBean;
import com.glance.utils.Constants;
import com.glance.utils.Constants.FayeAlert;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.google.gson.annotations.SerializedName;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

public class ScreenStatusService extends Service implements FayeListener {

	public final String TAG = "ScreenStatusService";//this.getClass().getSimpleName();

	public static FayeClient fayeClient;

	 public final String CHANNEL = "getcurrentscreen";

	private Context mContext;
	
	final static int myID = 1236;

	@Override
	public void onCreate() {

		super.onCreate();
		mContext = this.getApplicationContext();
//		Toast.makeText(mContext, "Scrren Status Faye Started", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "***************** FAYE STARTED for screen status ******************* ");

		if (android.os.Build.VERSION.SDK_INT <= 8){
			return 0;
		}

//		String baseUrl = Constants.WEB_SOCKET_URL;
		String baseUrl = Utils.getFayeUrl(mContext);
		URI uri = URI.create(String.format("wss://%s", baseUrl));//
		String channel  =String.format("/%s", CHANNEL);
		Log.d(TAG, "*** Channel **** "+ channel);
		Log.d(TAG, "*** uri **** "+ uri.getQuery());
		fayeClient = new FayeClient(new Handler(),uri, channel);
		fayeClient.setFayeListener(this);
		fayeClient.connectToServer(Utils.getOnlineUserMessage());
		showNotification();
		return START_STICKY;
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

		Log.i(TAG, String.format("Subscribed to channel  on Faye", subscription));
		Log.i(Constants.TAG, String.format("  Subscribed to channel  on Faye "));
	}

	@Override
	public void subscriptionFailedWithError(String error) {

		Log.i(TAG, String.format("Subscription failed with error: %s", error));
		Log.i(Constants.TAG, String.format("Subscription failed with error : ", error));
	}
	@Override
	public void messageReceived(JSONObject json) {

		Log.i(TAG, "Json recieved :: "+json.toString());


		try{
			String success=json.toString();
			success = "{text:'GET_CURRENT_SCREEN'}";
			if ( success!=null)
			{
				GFayeResponse response = (GFayeResponse) GSONUtils.getInstance().fromJson(success, GFayeResponse.class);
				if(response != null){
					if(response.getText().equalsIgnoreCase(Keywords.SCREEN_STATUS)){
						Intent intent = new Intent(FayeAlert.SCREEN_STATUS_OK);
						intent.putExtra(Keywords.DATA, Keywords.SCREEN_STATUS);	
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
					}
				}
			}
		}
		catch(Exception e)
		{
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
//		Toast.makeText(mContext, "Scrren Status Faye Killed", Toast.LENGTH_LONG).show();
		fayeClient.setFayeListener(null);
		fayeClient.disconnectFromServer();
		fayeClient = null;
		super.onDestroy();
	}

	public class GFayeResponse extends GlanceBaseBean{

		/**
		 * 
		 */
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
	
	@SuppressWarnings("deprecation")
	private void showNotification() {

		//The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent();
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

		//This constructor is deprecated. Use Notification.Builder instead
//		Notification notice = new Notification(R.drawable.app_icon, "Fit4Life", System.currentTimeMillis());

		Notification notice = new Notification();
		notice.setLatestEventInfo(mContext, "", "", pendIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(myID, notice);
	}

}
