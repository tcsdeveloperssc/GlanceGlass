package com.glance.controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.bean.model.GSuccessBean;
import com.glance.bean.model.Recipient;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.bean.response.GGetMailsResponse;
import com.glance.bean.response.GSendMailsResponse;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.ErrorCodes;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.google.gson.Gson;

public class GMailController extends Controller {

	public static final String TAG = "GMailController";
	public static final String ACTION_GET_MAIL = "getMail";
	public static final String ACTION_SEND_MAIL = "sendMail";
	private String user_id, tenant_id;

	public GMailController(Context context) {
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
	 * Function to fetch User task Queue
	 * **/

	public void getMail() {

		JSONObject mails = new JSONObject();
		try {
			mails.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.get_mail,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetMailsResponse.class);
		bundle.setRequestObject(mails);

		ResponseBean rMailBean = fetchDataFromService(bundle);
		/*if (rMailBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK) {
			postResultToUI(STATUS_FAILED, "Network not available");
			return;
		}*/

		if (rMailBean.getStatusBean().isSuccess()) {
			parseResponse((GGetMailsResponse) rMailBean.getResponse());
		} else {
			/*Toast.makeText(mContext,
					"Unable to receive the response. Loading from local space",
					Toast.LENGTH_SHORT).show();*/
			GGetMailsResponse response = null;
			Gson gson_obj = new Gson();
			response = gson_obj
					.fromJson(GFileManager.getStoryFromFile(
							GFileManager.MAILS_FILE, mContext),
							GGetMailsResponse.class);
			ResponseBean rBean = new ResponseBean();
			StatusBean sBean;
			if (response != null) {
				sBean = new StatusBean(200, "OK", response.getMessage());
				rBean.setResponse(response);
				rBean.setStatusBean(sBean);
			} else {
				sBean = new StatusBean();
				rBean.setResponse(null);
				sBean.setStatusCode(STATUS_FAILED);
				rBean.setStatusBean(sBean);
			}

			postResultToUI(rBean);
		}

	}

	/**
	 * Function to fetch User task Queue
	 * **/

	public void sendMail(ArrayList<Recipient> recepientList, String text) {

		JSONObject mails = new JSONObject();
		JSONArray recepientListObj = new JSONArray();

		try {
			mails.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			mails.putOpt("text", text);
			mails.putOpt("name",
					Utils.getFromPreference(mContext, Keywords.USER_NAME));

			for (int i = 0; i < recepientList.size(); i++) {
				JSONObject receiver = new JSONObject();
				receiver.putOpt("userId", recepientList.get(i).getUserId());
				receiver.put("name", recepientList.get(i).getName());
				recepientListObj.put(receiver);

			}
			mails.putOpt("userList", recepientListObj);
			/*
			 * String receiversListObj ; Gson gson_obj = new Gson();
			 * receiversListObj = gson_obj.toJson(recepientList);
			 * GLog.d("MAILBOX", "string is " +receiversListObj);
			 * mails.putOpt("userList", receiversListObj);
			 */
			/*
			 * JSONObject recepientListObj = new JSONObject(receiversListObj);
			 * mails.putOpt("userList", recepientListObj);
			 */

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.send_mail,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSendMailsResponse.class);
		bundle.setRequestObject(mails);
		ResponseBean rMailBean = fetchDataFromService(bundle);

		if (rMailBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK) {
			postResultToUI(STATUS_FAILED, "Network not available");
			return;
		}

		GSuccessBean sBean = null;
		boolean sentSuccess = false;

		if (rMailBean.getStatusBean().isSuccess()) {
			sentSuccess = true;
			/*
			 * sBean = (GSuccessBean) rMailBean.getResponse(); if(null !=
			 * sBean){
			 * if(Keywords.SUCCESS.equalsIgnoreCase(sBean.getMessage())){
			 * if("0".equals(sBean.getStatus())){ sentSuccess = true; } }
			 * 
			 * }
			 */
		}
		if (sentSuccess) {
			postResultToUI(STATUS_SUCCESS, "OK");
		} else {
			/*
			 * if (sBean != null){ if
			 * (sBean.getStatus().equalsIgnoreCase("103")){
			 * postResultToUI(STATUS_USER_NOT_PRIVILAGED, "NOTOK"); } else{
			 * postResultToUI(STATUS_FAILED, "NOTOK"); } }
			 */
			postResultToUI(STATUS_FAILED, "NOTOK");

		}

	}

	private void parseResponse(GGetMailsResponse response) {

		ResponseBean rBean = new ResponseBean();
		StatusBean sBean = new StatusBean(200, response.getStatus(),
				response.getMessage());
		// StatusBean sBean = new StatusBean(200, response.getStatus(),
		// response.getMessage());

		try {
			String message = "";
			if (response != null)

				message = response.getMessage();

			/**
			 * If message is SUCCESS, overwrite the local file with latest data
			 * from WebService. Else, parse the local file and populate the data
			 * into UI
			 * **/
			if (Keywords.SUCCESS.equalsIgnoreCase(message)) {

				Gson gson = new Gson();

				// convert java object to JSON format,
				// and returned as JSON formatted string
				String json = gson.toJson(response);

				GFileManager.writeStoryToFile(mContext,
						GFileManager.MAILS_FILE, json);

			} else {
				/*
				 * 103 -- user is not privileged 100 -- no story tagged to the
				 * user
				 */
				if (!response.getStatus().equalsIgnoreCase("103")
						&& !response.getStatus().equalsIgnoreCase("100")
						&& !response.getMessage().equalsIgnoreCase(
								"No alerts available for user")) {

					Gson gson_obj = new Gson();
					response = gson_obj.fromJson(GFileManager.getStoryFromFile(
							GFileManager.MAILS_FILE, mContext),
							GGetMailsResponse.class);
				}

			}

			rBean.setResponse(response);
			rBean.setStatusBean(sBean);

		} catch (Exception e) {
			e.printStackTrace();
			rBean.setResponse(null);
			sBean.setStatusCode(STATUS_FAILED);
			rBean.setStatusBean(sBean);
		}
		postResultToUI(rBean);

	}

}
