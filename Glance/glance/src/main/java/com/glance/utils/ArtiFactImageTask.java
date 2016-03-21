package com.glance.utils;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.glance.R;
import com.glance.activity.MenuActivity;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.StreamListener;
import com.glance.parser.BasePostParser;
import com.glance.utils.Utils.GLog;

public class ArtiFactImageTask extends AsyncTask<String, String, String> {
	String message = "";
	private String user_id = "", security_token = "", tenant_id = "",
			imageName = "", type = "";
	private Context mContext;
	private View layoutView;
	private ProgressBar pBar;
	private StreamListener streamListener = null;
	private CallBackListener imageLoadListener;

	public ArtiFactImageTask() {
		// TODO Auto-generated constructor stub
	}

	public ArtiFactImageTask(Context mContext, View layoutView,
			CallBackListener listener) {
		this.mContext = mContext;
		this.layoutView = layoutView;
		pBar = (ProgressBar) layoutView.findViewById(R.id.pbHotSpot);
		imageLoadListener = listener;
	}

	public ArtiFactImageTask(Context mContext, View layoutView, String type) {
		this.mContext = mContext;
		this.layoutView = layoutView;
		this.type = type;

	}

	public ArtiFactImageTask(Context mContext, String type,
			StreamListener streamListener) {
		this.mContext = mContext;
		this.type = type;
		this.streamListener = streamListener;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		showCustomisedDialog(mContext.getString(R.string.loading));

	}

	@Override
	protected String doInBackground(String... arg0) {

		try {
			SharedPreferences pref = Utils.getCredentials(mContext);

			user_id = pref.getString(
					mContext.getString(R.string.preference_user_id), "");
			security_token = pref.getString(
					mContext.getString(R.string.preference_security_token), "");
			tenant_id = pref.getString(
					mContext.getString(R.string.preference_tenant_id), "");

			JSONObject input_json = new JSONObject();
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));

			input_json.putOpt("artifactId", arg0[0]);

			BasePostParser bparser = new BasePostParser(mContext);

			String postData = bparser.postData(input_json.toString(), Constants
					.getServiceUrl(Constants.get_Artifact_Service, mContext));

			GLog.d("TCS",
					"*************** ARTIFACT IMAGE RESPONSE ***************** ");
			GLog.d("TCS", "AIR *** " + postData);

			JSONObject obj = new JSONObject(postData);
			if (type.equalsIgnoreCase("image")) {
				imageName = "image" + "=" + obj.optString("url");
			} else if (type.equalsIgnoreCase("video")) {
				imageName = obj.optString("url");
			} else {
				imageName = obj.optString("type") + "=" + obj.optString("url");
			}
			if (Utils.databaseHelper == null)
				Utils.createDatabase(mContext);
			
			
			Utils.databaseHelper.insertUrl(arg0[0], obj.optString("url"));
			// Utils.databaseHelper.insertUrl("asda", "wsdewa");

			message = obj.optString(Constants.MESSAGE);

			String status = obj.optString("status");

			if (status.equals("103")) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mContext, message, Toast.LENGTH_LONG)
								.show();
						Utils.clearSharedPref(mContext);
						mContext.startActivity(new Intent(mContext,
								MenuActivity.class));
						if (mContext != null) {

							// getActivity().finish();
						}
					}
				});

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

		return message;
	}

	@Override
	protected void onPostExecute(String result) {

		GLog.d("TCS", "art post exec" + message);

		if (message != null && message.equalsIgnoreCase("success")
				&& !type.equalsIgnoreCase("video")
				&& !type.equalsIgnoreCase("audio")
				&& !type.equalsIgnoreCase("image")) {
			ImageLoad downloader = new ImageLoad(layoutView, imageLoadListener);
			downloader.execute(
					Constants.getServiceUrl(Constants.get_File_Service,
							mContext) + imageName, imageName);// http://glance.tcsmobilitycloud.com/Glance/fileServer?image=Glance_tenant_525_IMAGE_20131104070248.827.jpg
		} else if (type == "video") {
			dismissDialog();

			/*
			 * Intent videoIntent = new Intent(mContext, VideoScreen.class);
			 * videoIntent.putExtra(Constants.VIDEO_URL_KEY,imageName);
			 */
			streamListener.onSuccess(imageName);
			// mContext.startActivity(videoIntent);

		} else if (type == "audio") {
			dismissDialog();

			streamListener.setStreamName(imageName);
			streamListener.onSuccess(imageName);

		} else if (type == "image") {
			ImageLoad downloader = new ImageLoad(layoutView, "image");
			Log.i("NAMITHA_MK",
					Constants.getServiceUrl(Constants.get_File_Service,
							mContext) + imageName);
			downloader.execute(
					Constants.getServiceUrl(Constants.get_File_Service,
							mContext) + imageName, imageName);

		}

	}

	public void showCustomisedDialog(String message) {

		if (pBar != null) {
			pBar.setVisibility(View.VISIBLE);
		}

	}

	public void dismissDialog() {
		if (pBar != null) {
			pBar.setVisibility(View.GONE);
		}
	}
}