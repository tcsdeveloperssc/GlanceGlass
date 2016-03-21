/**
Copyright (c) 2013, TATA Consultancy Services Limited (TCSL)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of TCSL nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

/**
 21-Nov-2013
 528937vnkm
 Class Description
 **/
package com.glance.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.glance.R;
import com.glance.bean.model.GSuccessBean;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.request.GAuthRequestBean;
import com.glance.bean.response.GAuthResponseBean;
import com.glance.bean.response.GTokenResponseBean;
import com.glance.bean.response.GVersionCheckResponseBean;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.ErrorCodes;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;

public class GRegistrationController extends Controller {

	public static final String TAG = "GRegistrationController";
	public static final String ACTION_TOKEN_REGISTRATION = "doTokenRegistration";
	public static final String ACTION_AUTHENTICATION = "doAuthentication";
	public static final String ACTION_LOGOUT = "doLogout";
	public static final String ACTION_VERSION_CHECK = "versionCheck";

	public GRegistrationController(Context context) {
		super(context);

	}

	@Override
	protected void execute(Object[] params) {
	}

	public void doTokenRegistration() {

		try {
			// Utils.startFaye(mContext);
			String android_id = getSecureAndroidId();

			JSONObject input_json = new JSONObject();
			input_json.putOpt(Constants.DEVICE_TOKEN, android_id);
			input_json.putOpt(Constants.DEVICE_TYPE, Constants.device_type);
			GLog.d("TCS", "device token->" + android_id);

			GServiceBundle tokenBundle = new GServiceBundle();
			tokenBundle.setServiceUrl(Constants.getServiceUrl(
					Constants.token_Reg_Service, mContext));
			tokenBundle.setRequestObject(input_json);
			tokenBundle.setResponseType(GTokenResponseBean.class);

			ResponseBean rBean = fetchDataFromService(tokenBundle);
			if (rBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK) {
				postResultToUI(STATUS_FAILED, "Network not available");
				return;
			}

			GLog.d(TAG, "***************** rBean ************** "
					+ rBean.getStatusBean().isSuccess());
			if (rBean.getStatusBean().isSuccess()) {
				postResultToUI(rBean);
			} else {
				postResultToUI(Controller.STATUS_ERROR, rBean.getStatusBean()
						.getStatusMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Should plan doing something here // TODO
		}
	}

	private String getSecureAndroidId() {
		return Secure.getString(((ContextWrapper) mContext).getBaseContext()
				.getContentResolver(), Secure.ANDROID_ID);
				//+ Utils.getAdditionalDeviceId();
	}

	public void doAuthentication() {
		GAuthRequestBean bean = new GAuthRequestBean();
		bean.setDeviceToken(getSecureAndroidId());

		JSONObject input_json = GSONUtils.getJSONObjectFromRequestBean(bean);

		GServiceBundle tokenBundle = new GServiceBundle();
		tokenBundle.setServiceUrl(Constants.getServiceUrl(
				Constants.auth_Glass_Service, mContext));
		tokenBundle.setRequestObject(input_json);
		tokenBundle.setResponseType(GAuthResponseBean.class);

		ResponseBean rBean = fetchDataFromService(tokenBundle);
		/*
		 * if (rBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK){
		 * postResultToUI(STATUS_FAILED, "Network not available"); return; }
		 */
		if (rBean.getStatusBean().isSuccess()) {
			GAuthResponseBean authBean = (GAuthResponseBean) rBean
					.getResponse();
			if (authBean != null && !TextUtils.isEmpty(authBean.getStatus())
					&& authBean.getStatus().equals("0")) {
				Utils.putToPreference(mContext, Keywords.GLASS_REGISTERED,
						Keywords.SUCCESS);
				String username = authBean.getFirstName() + " "
						+ authBean.getLastName();
				Utils.putToPreference(mContext, Keywords.USER_NAME, username);
				Utils.setUserCredentials(authBean, mContext);
				postResultToUI(rBean);
			} else {
				postResultToUI(STATUS_ERROR, authBean.getMessage());
			}
		} else {
			postResultToUI(STATUS_ERROR, rBean.getStatusBean()
					.getStatusMessage());
		}
	}

	public void doLogout() {

		SharedPreferences pref = Utils.getCredentials(mContext);

		String user_id = pref.getString(
				mContext.getString(R.string.preference_user_id), "");
		String tenant_id = pref.getString(
				mContext.getString(R.string.preference_tenant_id), "");

		JSONObject input_json = new JSONObject();
		try {
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));

		} catch (JSONException e) {
			// WHAT TO DO ??
			// return ??
			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.logout_Service,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSuccessBean.class);
		bundle.setRequestObject(input_json);
		boolean shouldLogout = false;
		GSuccessBean sBean = null;
		ResponseBean rBean = fetchDataFromService(bundle);
		if (rBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK) {
			postResultToUI(STATUS_FAILED, "Network not available");
			return;
		}

		if (rBean.getStatusBean().isSuccess()) {
			sBean = (GSuccessBean) rBean.getResponse();
			if (null != sBean) {
				if (Keywords.SUCCESS.equalsIgnoreCase(sBean.getMessage())) {
					if ("0".equals(sBean.getStatus())) {
						GLog.d("LOGOUT", "STATUS " + sBean.getStatus());
						shouldLogout = true;
					}
				}

			}
		}
		if (shouldLogout) {
			GLog.d("LOGOUT", "POST RESULT TO UI");
			postResultToUI(STATUS_SUCCESS, "OK");
		} else {

			if ((sBean.getStatus().equalsIgnoreCase("103")) && (sBean != null)) {
				postResultToUI(STATUS_USER_NOT_PRIVILAGED, rBean
						.getStatusBean().getStatusMessage());
			} else {
				postResultToUI(STATUS_FAILED, rBean.getStatusBean()
						.getStatusMessage());
			}

		}
	}

	public void versionCheck(String version) {
		/*
		 * GVersionCheckRequestBean bean = new GVersionCheckRequestBean();
		 * bean.setVersion(version); long time = System.currentTimeMillis();
		 * String timeUpdated = Long.toString(time);
		 * bean.setTimeUpdated(timeUpdated);
		 */

		SharedPreferences pref = Utils.getCredentials(mContext);

		String user_id = pref.getString(
				mContext.getString(R.string.preference_user_id), "");
		String tenant_id = pref.getString(
				mContext.getString(R.string.preference_tenant_id), "");

		long time = System.currentTimeMillis();
		String timeUpdated = Long.toString(time);
		JSONObject input_json = new JSONObject();
		try {
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));

			input_json.putOpt("timeUpdated", timeUpdated);
			input_json.putOpt("version", version);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		// JSONObject input_json = GSONUtils.getJSONObjectFromRequestBean(bean);

		GServiceBundle tokenBundle = new GServiceBundle();
		tokenBundle.setServiceUrl(Constants.getServiceUrl(
				Constants.versionCheck_Service, mContext));
		tokenBundle.setRequestObject(input_json);
		tokenBundle.setResponseType(GVersionCheckResponseBean.class);

		ResponseBean rBean = fetchDataFromService(tokenBundle);
		if (rBean.getStatusBean().getStatusCode() == ErrorCodes.NO_NETWORK) {
			postResultToUI(STATUS_FAILED, "Network not available");
			return;
		}
		if (rBean.getStatusBean().isSuccess()) {
			postResultToUI(rBean);

		} else {
			postResultToUI(STATUS_ERROR, rBean.getStatusBean()
					.getStatusMessage());
		}
	}
}
