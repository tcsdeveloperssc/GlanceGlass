package com.glance.controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.glance.R;
import com.glance.bean.model.GSurveyAnswer;
import com.glance.bean.model.GSurveyOption;
import com.glance.bean.model.GSurveyResultResponse;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.bean.response.GAssignedSurveyResponse;
import com.glance.bean.response.GSubmitSurveyResponse;
import com.glance.bean.response.GSurveyResponse;
import com.glance.controller.core.Controller;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.google.gson.Gson;

public class GSurveyController extends Controller {

	public static final String TAG = "GSurveyController";
	public static final String ACTION_GET_ASSIGNED_SURVEY = "getAssignedSurvey";
	public static final String ACTION_GET_SURVEY_RESULT = "getSurveyResult";
	public static final String ACTION_SAVE_SURVEY = "saveSurvey";
	public static final String ACTION_SUBMIT_SURVEY = "submitSurvey";
	private String user_id, tenant_id;
	
	public GSurveyController(Context context) {
	
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

	public void getAssignedSurvey(){
		
		JSONObject surveys = new JSONObject();
		try {
			surveys.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.GET_ASSIGNED_SURVEY,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GAssignedSurveyResponse.class);
		bundle.setRequestObject(surveys);

		ResponseBean assignedSurvey = fetchDataFromService(bundle);
		

		if (assignedSurvey.getStatusBean().isSuccess()) {
			parseResponse((GAssignedSurveyResponse) assignedSurvey.getResponse());
		} else {
			GAssignedSurveyResponse response = null;
			Gson gson_obj = new Gson();
			response = gson_obj
					.fromJson(GFileManager.getStoryFromFile(
							GFileManager.SURVEY_LIST, mContext),
							GAssignedSurveyResponse.class);
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
	
	public void getSurveyResult(String surveyId){
		
		JSONObject surveys = new JSONObject();
		try {
			surveys.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			surveys.putOpt("surveyId",surveyId);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.GET_SURVEY_RESULTS,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSurveyResultResponse.class);
		bundle.setRequestObject(surveys);

		ResponseBean surveyResults = fetchDataFromService(bundle);
		

		if (surveyResults.getStatusBean().isSuccess()) {
			
			GSurveyResultResponse response = (GSurveyResultResponse)surveyResults.getResponse();
			ResponseBean rBean = new ResponseBean();
			StatusBean sBean = new StatusBean(200, response.getStatus(),
					response.getMessage());

			try {
				String message = "";
				if (response != null)

					message = response.getMessage();
				if (Keywords.SUCCESS.equalsIgnoreCase(message)) {

					Gson gson = new Gson();

					// convert java object to JSON format,
					// and returned as JSON formatted string
					String json = gson.toJson(response);

					//write to db
					

				} else {
					
					//retrieve from db

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
	
	public void saveSurvey(GSurveyResponse response){
		
		JSONObject surveyDetail = getSurveyJson(response);
		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.SAVE_SURVEY,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GSubmitSurveyResponse.class);
		bundle.setRequestObject(surveyDetail);

		ResponseBean savedSurvey = fetchDataFromService(bundle);
		

		if (savedSurvey.getStatusBean().isSuccess()) {
			ResponseBean rBean = new ResponseBean();
			StatusBean sBean = new StatusBean(200, response.getStatus(),
					response.getMessage());

			try {
				String message = "";
				if (response != null)

					message = response.getMessage();
				if (Keywords.SUCCESS.equalsIgnoreCase(message)) {

					Gson gson = new Gson();

					// convert java object to JSON format,
					// and returned as JSON formatted string
					String json = gson.toJson(response);

					//write to db
					

				} else {
					
					//retrieve from db

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
	
	public void submitSurvey(GSurveyResponse response){
		
		JSONObject surveyDetail = getSurveyJson(response);
		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.SUBMIT_SURVEY,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GAssignedSurveyResponse.class);
		bundle.setRequestObject(surveyDetail);

		ResponseBean savedSurvey = fetchDataFromService(bundle);
		

		if (savedSurvey.getStatusBean().isSuccess()) {
			ResponseBean rBean = new ResponseBean();
			StatusBean sBean = new StatusBean(200, response.getStatus(),
					response.getMessage());

			try {
				String message = "";
				if (response != null)

					message = response.getMessage();
				if (Keywords.SUCCESS.equalsIgnoreCase(message)) {

					Gson gson = new Gson();

					// convert java object to JSON format,
					// and returned as JSON formatted string
					String json = gson.toJson(response);

					//write to db
					

				} else {
					
					//retrieve from db

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
	
	private JSONObject getSurveyJson(GSurveyResponse response) {

		JSONObject surveyDetail = new JSONObject();
		JSONObject surveyJson = new JSONObject();
		try {

			ArrayList<GSurveyAnswer> answerList = response.getAnswers();

			JSONArray answerArray = new JSONArray();
			JSONObject answerJson = new JSONObject();
			for (int i = 0, j = answerList.size(); i < j; i++) {

				GSurveyAnswer currentAnswer = answerList.get(i);
				answerJson.put("questionNumber",currentAnswer.getQuestionNumber());
				answerJson.put("optionType", currentAnswer.getOptionType());

				ArrayList<GSurveyOption> optionList = currentAnswer.getOptionList();

				JSONArray optionArray = new JSONArray();
				JSONObject optionJson = new JSONObject();
				for (int p = 0, q = optionList.size(); p < q; p++) {

					GSurveyOption currentOption = optionList.get(p);
					optionJson.put("optionNo", currentOption.getOptionNo());
					optionJson.put("optionText", currentOption.getOptionText());

					optionArray.put(optionJson);
				}
				answerJson.put("option", optionArray);
				answerArray.put(answerJson);
			}

			surveyJson.put("surveyId", response.getSurveyId());
			surveyJson.put("answers", answerArray);

			surveyDetail.putOpt(Constants.CONTEXT,Utils.getUserTenantObject(user_id, tenant_id));
			surveyDetail.putOpt(Constants.SURVEY,surveyJson);
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
		
		return surveyDetail;
	}
	
	private void parseResponse(GAssignedSurveyResponse response) {

		ResponseBean rBean = new ResponseBean();
		StatusBean sBean = new StatusBean(200, response.getStatus(),
				response.getMessage());

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
						GFileManager.SURVEY_LIST, json);

			} else {
				
				 /** 103 -- user is not privileged 100 -- no story tagged to the
				 * user*/
				 
				if (!response.getStatus().equalsIgnoreCase("103")
						&& !response.getStatus().equalsIgnoreCase("100")
						&& !response.getMessage().equalsIgnoreCase(
								"No alerts available for user")) {

					Gson gson_obj = new Gson();
					response = gson_obj.fromJson(GFileManager.getStoryFromFile(
							GFileManager.SURVEY_LIST, mContext),
							GAssignedSurveyResponse.class);
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
