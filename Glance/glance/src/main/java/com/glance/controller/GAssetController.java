package com.glance.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.bean.response.GGetAssetDetailResponse;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.ErrorCodes;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.google.gson.Gson;

public class GAssetController extends Controller {

	public static final String TAG = "GAssetController";
	public static final String ACTION_GET_ASSET_DETAIL = "getAssetDetail";

	private String user_id, tenant_id;

	public GAssetController(Context context) {
		super(context);
		SharedPreferences pref = Utils.getCredentials(mContext);

		user_id = pref.getString(
				mContext.getString(R.string.preference_user_id), "");
		tenant_id = pref.getString(
				mContext.getString(R.string.preference_tenant_id), "");
	}

	@Override
	protected void execute(Object[] params) {

	}

	/**
	 * Function to fetch detail of an asset
	 * **/

	public void getAssetDetail(String assetId) {

		JSONObject asset = new JSONObject();
		try {
			asset.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			asset.putOpt(Constants.ASSET_ID, assetId);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.GET_ASSET_DETAILS, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetAssetDetailResponse.class);
		bundle.setRequestObject(asset);

		ResponseBean rMailBean = fetchDataFromService(bundle);
		if (rMailBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK) {
			postResultToUI(STATUS_FAILED, "Network not available");
			return;
		}

		if (rMailBean.getStatusBean().isSuccess()) {
			parseResponse((GGetAssetDetailResponse) rMailBean.getResponse());
		} else {
			/*
			 * Toast.makeText(mContext,
			 * "Unable to receive the response. Loading from local space",
			 * Toast.LENGTH_SHORT).show(); GGetMailsResponse response = null;
			 * Gson gson_obj = new Gson(); response =
			 * gson_obj.fromJson(GFileManager.getStoryFromFile(
			 * GFileManager.MAILS_FILE, mContext), GGetMailsResponse.class);
			 * ResponseBean rBean = new ResponseBean(); StatusBean sBean ; if
			 * (response != null) { sBean = new StatusBean(200, "OK",
			 * response.getMessage()); rBean.setResponse(response);
			 * rBean.setStatusBean(sBean); }else{ sBean = new StatusBean();
			 * rBean.setResponse(null); sBean.setStatusCode(STATUS_FAILED);
			 * rBean.setStatusBean(sBean); }
			 * 
			 * postResultToUI(rBean);
			 */
		}

	}

	private void parseResponse(GGetAssetDetailResponse response) {

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
						GFileManager.ASSET_DETAIL_FILE, json);

			} else {
				/*
				 * 103 -- user is not privileged 100 -- no story tagged to the
				 * user
				 */
				if (!response.getStatus().equalsIgnoreCase("103")/*
																 * && !response.
																 * getStatus().
																 * equalsIgnoreCase
																 * ("100")
																 */) {

					Gson gson_obj = new Gson();
					response = gson_obj.fromJson(GFileManager.getStoryFromFile(
							GFileManager.ASSET_DETAIL_FILE, mContext),
							GGetAssetDetailResponse.class);
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
