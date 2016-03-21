package com.glance.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.bean.model.GSuccessBean;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.Utils;

public class GsaveUserLocationController extends Controller {

	public static final String TAG = "save" ;
	public static final String SAVE_USER_LOCATION = "updateUserLocation";

	private String user_id, tenant_id, longitude, latitude, distanceLimit;

	private boolean isAssetList = false;

	public GsaveUserLocationController(Context context) {
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

	private void parseResponse(GSuccessBean response) {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			rBean.setResponse(null);
			sBean.setStatusCode(STATUS_FAILED);
			rBean.setStatusBean(sBean);
		}
		postResultToUI(rBean);

	}


	public void updateUserLocation(String longitude, String latitude) {

		JSONObject userData = new JSONObject();
		try {
			userData.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			userData.putOpt(Constants.LONGITUDE, longitude);
			userData.putOpt(Constants.LATITUDE, latitude);
			
		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.SAVE_USER_LOCATION, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSuccessBean.class);
		bundle.setRequestObject(userData);
		ResponseBean rStoryBean = fetchDataFromService(bundle);
		parseResponse((GSuccessBean) rStoryBean.getResponse());

	}
}
