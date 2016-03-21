package com.glance.controller;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.glance.R;
import com.glance.bean.response.GMultimediaUploadResponseBean;
import com.glance.controller.core.Controller;
import com.glance.utils.Constants;
import com.glance.utils.Utils;
import com.google.gson.Gson;

public class GMultimediaUploadController extends Controller {

	public static final String TAG = "GMultimediaUploadController";
	public static final String ACTION_TASK_MULTIMEDIA_UPLOAD = "uploadTaskMultimedia";
	
	private String user_id, tenant_id;
	private HttpClient mHttpClient;

	public GMultimediaUploadController(Context context) {
		super(context);
		SharedPreferences pref = Utils.getCredentials(mContext);

		user_id = pref.getString(
				mContext.getString(R.string.preference_user_id), "");
		tenant_id = pref.getString(
				mContext.getString(R.string.preference_tenant_id), "");
	}

	@Override
	protected void execute(Object[] params) {
		// TODO Auto-generated method stub

	}


	/**
	 * Function to upload Multimedia
	 * **/
	public void uploadTaskMultimedia(String taskId, String status, File file,
			Integer type) {

		JSONObject upLoadImage = new JSONObject();
		try {

			upLoadImage.putOpt(Constants.TASK_ID, taskId);
			upLoadImage.putOpt("userId", user_id);
			upLoadImage.putOpt(Constants.TENANT_ID, tenant_id);
			SharedPreferences pref = Utils.getCredentials(mContext);
			upLoadImage
					.putOpt("securityToken", pref.getString(mContext
							.getString(R.string.preference_security_token), ""));
			
			upLoadImage.putOpt("reqFrom", "G");
			upLoadImage.putOpt("status", status);
			switch (type) {
			case Constants.IMAGE:
				upLoadImage.putOpt("type", "TaskImage");
				
				upLoadImage.putOpt("title", "captured picture");
				break;
			case Constants.VIDEO:
				upLoadImage.putOpt("type", "TaskVideo");
				upLoadImage.putOpt("title", "captured video");
				break;
			case Constants.AUDIO:
				upLoadImage.putOpt("type", "TaskAudio");
				upLoadImage.putOpt("title", "captured audio");
				break;
			}

			Long time = System.currentTimeMillis();
			upLoadImage.putOpt("time", time.toString());

		} catch (JSONException e) {

			e.printStackTrace();
		}

		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(params);

		try {

			Log.d("TCS", "************* Request Object ************ ");
			Log.d("TCS", upLoadImage.toString() + " ");

			HttpPost httppost = new HttpPost(Constants.getServiceUrl(
					Constants.FILE_UPLOAD, mContext));

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			multipartEntity.addPart("request",
					new StringBody(upLoadImage.toString()));

			multipartEntity.addPart("file", new FileBody(file));

			httppost.setEntity(multipartEntity);

			Log.d("TCS", "************* multipartEntity ************ ");

			Log.d("TCS", multipartEntity.toString());

			mHttpClient
					.execute(httppost, new MultimediaUploadResponseHandler());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MultimediaUploadResponseHandler implements ResponseHandler {

		@Override
		public Object handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {

			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);

			Gson gson_obj = new Gson();
			GMultimediaUploadResponseBean response_obj = gson_obj.fromJson(
					responseString, GMultimediaUploadResponseBean.class);
			if (response_obj.getStatus().equalsIgnoreCase("0")) {
				postResultToUI(STATUS_SUCCESS, "OK");

			} else {
				postResultToUI(STATUS_FAILED, "NOTOK");
			}

			Log.d("UPLOAD", responseString);

			return null;
		}

	}

}
