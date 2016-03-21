package com.glance.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.bean.model.GSuccessBean;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.bean.response.GGetUserTaskQueueResponse;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.google.gson.Gson;

public class GUserTasksController extends Controller {

	public static final String TAG = "GUserTasksController";
	public static final String ACTION_USER_TASKS_QUEUE = "getUserTasksQueue";
	public static final String ACTION_CHANGE_TASKS_STATUS = "changeTaskStatus";
	public static final String ACTION_ASSET_TASKS_QUEUE = "getAssetTasksQueue";
	public static final String ACTION_CHECK_SUBNODE = "checkSubNode";
	private String user_id, tenant_id;
	private boolean isAssetList = false;

	public GUserTasksController(Context context) {
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
	 * Function to change the status of tasks
	 * **/

	public void changeTaskStatus(String taskId, String status, String rootCause) {

		JSONObject changeStatus = new JSONObject();
		try {
			changeStatus.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			changeStatus.putOpt(Constants.TASK_ID, taskId);
			changeStatus.putOpt("action", status);
			changeStatus.putOpt("rootCause", rootCause);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.CHANGE_TASK_STATUS, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSuccessBean.class);
		bundle.setRequestObject(changeStatus);

		ResponseBean rChangeStatusBean = fetchDataFromService(bundle);
		/*
		 * if (rChangeStatusBean.getStatusBean().getStatusCode() ==
		 * ErrorCodes.NO_NETWORK){ postResultToUI(STATUS_FAILED,
		 * "Network not available"); return; }
		 */

		if (rChangeStatusBean.getStatusBean().isSuccess()) {
			GSuccessBean sBean = (GSuccessBean) rChangeStatusBean.getResponse();
			if (null != sBean) {
				if (Keywords.SUCCESS.equalsIgnoreCase(sBean.getMessage())) {
					if ("0".equals(sBean.getStatus())) {
						GLog.d("changeTask", "STATUS " + sBean.getStatus());
						postResultToUI(STATUS_SUCCESS, rChangeStatusBean
								.getStatusBean().getStatusMessage());
					}
				} else {
					postResultToUI(STATUS_FAILED, rChangeStatusBean
							.getStatusBean().getStatusMessage());
				}
			}
		}
	}
	
	public void checkSubNode(String taskId, String subNodeKey){
		JSONObject checkList = new JSONObject();
		try {
			checkList.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			checkList.putOpt(Constants.TASK_ID, taskId);
			checkList.putOpt("key", subNodeKey);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.updateTaskCheckList, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSuccessBean.class);
		bundle.setRequestObject(checkList);

		ResponseBean rChangeStatusBean = fetchDataFromService(bundle);
		if (rChangeStatusBean.getStatusBean().isSuccess()) {
			GSuccessBean sBean = (GSuccessBean) rChangeStatusBean.getResponse();
			if (null != sBean) {
				if (Keywords.SUCCESS.equalsIgnoreCase(sBean.getMessage())) {
					if ("0".equals(sBean.getStatus())) {
						GLog.d("changeTask", "STATUS " + sBean.getStatus());
						postResultToUI(STATUS_SUCCESS, rChangeStatusBean
								.getStatusBean().getStatusMessage());
					}
				} else {
					postResultToUI(STATUS_FAILED, rChangeStatusBean
							.getStatusBean().getStatusMessage());
				}
			}
		}

		
	}

	/**
	 * Function to fetch User task Queue
	 * **/

	public void getUserTasksQueue() {

		JSONObject userTasks = new JSONObject();
		try {
			userTasks.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.user_task_queue,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetUserTaskQueueResponse.class);
		bundle.setRequestObject(userTasks);

		ResponseBean rStoryBean = fetchDataFromService(bundle);
		/*
		 * if (rStoryBean.getStatusBean().getStatusCode() ==
		 * ErrorCodes.NO_NETWORK){ postResultToUI(STATUS_FAILED,
		 * "Network not available"); return; } If n/w is not available
		 */
		if (rStoryBean.getStatusBean().isSuccess()) {
			parseResponse((GGetUserTaskQueueResponse) rStoryBean.getResponse());
		} else {

			GGetUserTaskQueueResponse response = null;
			Gson gson_obj = new Gson();
			response = gson_obj.fromJson(GFileManager.getStoryFromFile(
					GFileManager.USER_TASKS, mContext),
					GGetUserTaskQueueResponse.class);
			ResponseBean rBean = new ResponseBean();
			StatusBean sBean;
			if (response != null) {
				sBean = new StatusBean(200, "OK", response.getMessage());
				rBean.setResponse(response);
				rBean.setStatusBean(sBean);
			} else {
				sBean = new StatusBean(STATUS_FAILED, "NOTOK", "failed");
				rBean.setResponse(null);
				rBean.setStatusBean(sBean);
			}

			postResultToUI(rBean);
		}

	}

	/**
	 * Function to fetch User task Queue
	 * **/

	public void getAssetTasksQueue(String assetId) {

		isAssetList = true;
		JSONObject userTasks = new JSONObject();
		try {
			userTasks.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			userTasks.putOpt(Constants.ASSET_ID, assetId);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.GET_ASSET_TASK_LIST, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetUserTaskQueueResponse.class);
		bundle.setRequestObject(userTasks);

		ResponseBean rStoryBean = fetchDataFromService(bundle);
		/*
		 * if (rStoryBean.getStatusBean().getStatusCode() ==
		 * ErrorCodes.NO_NETWORK){ postResultToUI(STATUS_FAILED,
		 * "Network not available"); return; } If n/w is not available
		 */
		if (rStoryBean.getStatusBean().isSuccess()) {
			parseResponse((GGetUserTaskQueueResponse) rStoryBean.getResponse());
		} else {

			/*
			 * GGetUserTaskQueueResponse response = null; Gson gson_obj = new
			 * Gson(); response =
			 * gson_obj.fromJson(GFileManager.getStoryFromFile(
			 * GFileManager.USER_TASKS, mContext),
			 * GGetUserTaskQueueResponse.class); ResponseBean rBean = new
			 * ResponseBean(); StatusBean sBean ; if (response != null) { sBean
			 * = new StatusBean(200, "OK", response.getMessage());
			 * rBean.setResponse(response); rBean.setStatusBean(sBean); }else{
			 */
			/*StatusBean sBean;
			ResponseBean rBean = new ResponseBean();
			sBean = new StatusBean(STATUS_FAILED, "NOTOK", "failed");
			rBean.setResponse(null);
			rBean.setStatusBean(sBean);
			postResultToUI(rBean);*/
			
			
			


			GGetUserTaskQueueResponse response = null;
			Gson gson_obj = new Gson();
			response = gson_obj.fromJson(GFileManager.getStoryFromFile(
					GFileManager.ASSET_TASKS, mContext),
					GGetUserTaskQueueResponse.class);
			ResponseBean rBean = new ResponseBean();
			StatusBean sBean;
			if (response != null) {
				sBean = new StatusBean(200, "OK", response.getMessage());
				rBean.setResponse(response);
				rBean.setStatusBean(sBean);
			} else {
				sBean = new StatusBean(STATUS_FAILED, "NOTOK", "failed");
				rBean.setResponse(null);
				rBean.setStatusBean(sBean);
			}

			postResultToUI(rBean);
		}

	}

	private void parseResponse(GGetUserTaskQueueResponse response) {

		ResponseBean rBean = new ResponseBean();
		StatusBean sBean = new StatusBean(200, "OK", response.getMessage());
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
			if ((Keywords.SUCCESS.equalsIgnoreCase(message))
					|| (message.equalsIgnoreCase("No tasks assigned for user"))
					|| (message
							.equalsIgnoreCase("No tasks found for the conditions"))) {

				Gson gson = new Gson();

				// convert java object to JSON format,
				// and returned as JSON formatted string
				String json = gson.toJson(response);
				 if(!isAssetList)
				GFileManager.writeStoryToFile(mContext,
						GFileManager.USER_TASKS, json);
				 else
					 GFileManager.writeStoryToFile(mContext,
								GFileManager.ASSET_TASKS, json);

			} 
			else {
				/*
				 * 103 -- user is not privileged 100 -- no story tagged to the
				 * user
				 */
				if (!response.getStatus().equalsIgnoreCase("103")
						&& !response.getStatus().equalsIgnoreCase("100")) {

					Gson gson_obj = new Gson();
					if (!isAssetList)
						response = gson_obj.fromJson(GFileManager
								.getStoryFromFile(GFileManager.USER_TASKS,
										mContext),
								GGetUserTaskQueueResponse.class);
					else
						response = gson_obj.fromJson(GFileManager
								.getStoryFromFile(GFileManager.ASSET_TASKS,
										mContext),
								GGetUserTaskQueueResponse.class);
				}

			}

			rBean.setResponse(response);
			rBean.setStatusBean(sBean);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rBean.setResponse(null);
			sBean.setStatusCode(STATUS_FAILED);
			rBean.setStatusBean(sBean);
		}
		postResultToUI(rBean);

	}
	

}
