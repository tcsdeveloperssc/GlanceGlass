package com.glance.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.bean.response.GGetUserListResponse;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.google.gson.Gson;

public class GNearbyUsersController extends Controller {

	public static final String TAG = "GUserTasksController";
	public static final String ACTION_USER_LIST = "getUserList";
	/*
	 * public static final String ACTION_CHANGE_TASKS_STATUS =
	 * "changeTaskStatus"; public static final String ACTION_ASSET_TASKS_QUEUE =
	 * "getAssetTasksQueue";
	 */
	private String user_id, tenant_id, longitude, latitude, distanceLimit;

	private boolean isAssetList = false;

	public GNearbyUsersController(Context context) {
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

	private void parseResponse(GGetUserListResponse response) {

		ResponseBean rBean = new ResponseBean();
		StatusBean sBean = new StatusBean(200, "OK", response.getMessage());

		try {

			if (response != null) {
				if ((Keywords.SUCCESS.equalsIgnoreCase(response.getMessage()))) {
					rBean.setResponse(response);
					rBean.setStatusBean(sBean);
				}
				else{
					rBean.setResponse(null);
					sBean.setStatusCode(STATUS_FAILED);
					rBean.setStatusBean(sBean);
				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			rBean.setResponse(null);
			sBean.setStatusCode(STATUS_FAILED);
			rBean.setStatusBean(sBean);
		}
		postResultToUI(rBean);

	}

	// -------
	/**
	 * Function to fetch User task Queue
	 * **/

	public void getUserList(String longitude, String latitude,
			String distanceLimit) {

		JSONObject userLists = new JSONObject();
		try {
			userLists.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			userLists.putOpt(Constants.DISTANCELIMIT, distanceLimit);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.GET_NEARBY_USERLIST, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetUserListResponse.class);
		bundle.setRequestObject(userLists);

		ResponseBean rStoryBean = fetchDataFromService(bundle);

		if (rStoryBean.getStatusBean().isSuccess()) {

			parseResponse((GGetUserListResponse) rStoryBean.getResponse());

		} else {
			GLog.e(TAG, "ResponseBean status is not success");
			Gson gson_obj = new Gson();

			GGetUserListResponse response = gson_obj.fromJson(GFileManager
					.getStoryFromFile(GFileManager.USER_LIST, mContext),
					GGetUserListResponse.class);
			parseResponse(response);

		}

	}

}
