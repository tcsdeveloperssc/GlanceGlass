package com.glance.faye;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.glance.bean.model.GlanceBaseBean;
import com.glance.bean.response.GAnnotationResponse;
import com.glance.utils.Constants;
import com.glance.utils.Constants.FayeAlert;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.google.gson.annotations.SerializedName;
import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.FayeClient.FayeListener;

public class AnnotationService extends Service implements FayeListener {

	public final String TAG = "AnnotationService";//this.getClass().getSimpleName();

	public static FayeClient fayeClient;

	 public final String CHANNEL = "annotationpoints";

	private Context mContext;

	@Override
	public void onCreate() {

		super.onCreate();
		mContext = this.getApplicationContext();
//		Toast.makeText(mContext, "Faye Started : 2", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "***************** FAYE STARTED for annotation ******************* ");

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

		return super.onStartCommand(intent, flags, startId);
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

		Log.i(TAG, "Json recieved :: " + json.toString());
		
		try {
			String success = json.toString();
			//success = loadJSONFromAsset();
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
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static FayeClient getFayeClient() {
		return fayeClient;
	}

	@Override
	public void onDestroy() {
//		Toast.makeText(mContext, "Faye Killed : 2", Toast.LENGTH_LONG).show();
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
	
	public String loadJSONFromAsset() {
	    String json = null;
	    try {

	        InputStream is = getAssets().open("points.json");

	        int size = is.available();

	        byte[] buffer = new byte[size];

	        is.read(buffer);

	        is.close();

	        json = new String(buffer, "UTF-8");


	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	    return json;

	}

}
