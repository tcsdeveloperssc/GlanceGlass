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

package com.glance.controller;

/**
 27-Nov-2013
 528937vnkm
 Class Description
 **/

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.glance.R;
import com.glance.bean.model.HotSpots;
import com.glance.bean.model.MainNode;
import com.glance.bean.model.NodeList;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.bean.model.SubNodes;
import com.glance.bean.model.TextSpots;
import com.glance.bean.model.TimeLocCondition;
import com.glance.bean.response.GGetStoryListResponseBean;
import com.glance.bean.response.GGetStoryResponseBean;
import com.glance.bean.response.GGetStoryResponseBean.NodeData;
import com.glance.bean.response.GGetStoryResponseBean.NodeData.SubNodeBean;
import com.glance.bean.response.GGetStoryResponseBean.NodeData.SubNodeBean.HotSpotBean;
import com.glance.bean.response.GGetStoryResponseBean.NodeData.SubNodeBean.TextSpotBean;
import com.glance.controller.core.Controller;
import com.glance.gps.GLocationManager;
import com.glance.services.GServiceBundle;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.google.gson.Gson;

public class GStoryController extends Controller {

	public static final String ACTION_GET_STORY = "getStory";
	public static final String ACTION_GET_TAGGED_STORY = "getTaggedStoryForSelfHelp";
	public static final String ACTION_GET_STORIES_LIST = "getStoriesList";
	public static final String ACTION_CHECK_FOR_UPDATE = "checkForUpdates";

	private String user_id;
	private String story_id;
	private String tenant_id;
	private String last_modified_time;
	private ArrayList<MainNode> listCriteriaSatisfiedNodes = null;
	private NodeList mNodeList;

	ArrayList<TimeLocCondition> timeconditionList = new ArrayList<TimeLocCondition>();

	public GStoryController(Context context) {
		super(context);

		SharedPreferences pref = Utils.getCredentials(mContext);

		user_id = pref.getString(
				mContext.getString(R.string.preference_user_id), "");
		/*
		 * story_id = pref.getString(
		 * mContext.getString(R.string.preference_story_id), "");
		 */

		/*
		 * story_id = GFileManager.getStoryFromFile( GFileManager.STORY_ID,
		 * mContext);
		 */
		tenant_id = pref.getString(
				mContext.getString(R.string.preference_tenant_id), "");
		last_modified_time = Utils.getFromPreference(context,
				Keywords.STORY_UPDATED_TIME);
	}

	@Override
	protected void execute(Object[] params) {
	}

	/**
	 * Function to fetch Story Details
	 * **/
	public void getStory(String story_Id) {

		this.story_id = story_Id;
		JSONObject input_json = new JSONObject();
		try {
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			input_json.putOpt(Constants.STORY_ID, story_id);

		} catch (JSONException e) {
			// WHAT TO DO ??
			// return ??
			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.story_Service,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetStoryResponseBean.class);
		bundle.setRequestObject(input_json);

		ResponseBean rStoryBean = fetchDataFromService(bundle);
		parseResponse((GGetStoryResponseBean) rStoryBean.getResponse());

	}

	public void getTaggedStoryForSelfHelp(String task_id, String story_Id) {
		this.story_id = story_Id;
		JSONObject input_json = new JSONObject();
		try {
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			input_json.putOpt(Constants.STORY_ID, story_id);
			input_json.putOpt(Constants.TASK_ID, task_id);

		} catch (JSONException e) {
			// WHAT TO DO ??
			// return ??
			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(Constants.tagStoryToSelfHelp_Service,
				mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetStoryResponseBean.class);
		bundle.setRequestObject(input_json);

		ResponseBean rStoryBean = fetchDataFromService(bundle);
		parseResponse((GGetStoryResponseBean) rStoryBean.getResponse());
	}

	public void getStoriesList() {
		JSONObject input_json = new JSONObject();
		try {
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			input_json.putOpt(Constants.STORY_ID, story_id);

		} catch (JSONException e) {
			// WHAT TO DO ??
			// return ??
			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.story_list_Service, mContext));
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setResponseType(GGetStoryListResponseBean.class);
		bundle.setRequestObject(input_json);
		ResponseBean rStoryBean = fetchDataFromService(bundle);

		ResponseBean rBean = new ResponseBean();
		GGetStoryListResponseBean response = (GGetStoryListResponseBean) rStoryBean
				.getResponse();
		StatusBean sBean = new StatusBean(200, "OK", response.getMessage());

		if (response.getStatus().equalsIgnoreCase("0")) {
			Gson gson = new Gson();

			// convert java object to JSON format,
			// and returned as JSON formatted string
			String json = gson.toJson((GGetStoryListResponseBean) rStoryBean
					.getResponse());

			GFileManager.writeStoryToFile(mContext, GFileManager.STORY_LIST,
					json);
			rBean.setResponse(response);
			rBean.setStatusBean(sBean);
		} else {

			GGetStoryListResponseBean response1 = null;
			Gson gson_obj = new Gson();
			response1 = gson_obj.fromJson(GFileManager.getStoryFromFile(
					GFileManager.STORY_LIST, mContext),
					GGetStoryListResponseBean.class);

			if (response1 != null) {
				rBean.setResponse(response);
				rBean.setStatusBean(sBean);
			} else {
				sBean.setStatusCode(STATUS_FAILED);
				sBean.setStatus("NOTOK");
				sBean.setStatusMessage("failed");
				rBean.setResponse(null);
				rBean.setStatusBean(sBean);
			}
		}
		postResultToUI(rBean);

	}

	// obj = new JSONObject(Utils.readStringFromAssetsFile("SampleStory.json",
	// mContext));
	/**
	 * Function to 1. Parse the WebService Response 2. Write response to local
	 * file 3. Check Criteria Satisfaction 4. Push the data to UI
	 * **/

	private void parseResponse(GGetStoryResponseBean response) {
		listCriteriaSatisfiedNodes = new ArrayList<MainNode>();
		mNodeList = new NodeList();
		StatusBean sBean = null;
		ResponseBean rBean = new ResponseBean();
		if (null != response)
			sBean = new StatusBean(200, "OK", response.getMessage());
		else {
			Gson gson_obj = new Gson();
			response = gson_obj.fromJson(
					GFileManager.getStoryFromFile(GFileManager.STORY_FILE
							+ story_id, mContext), GGetStoryResponseBean.class);

		}

		try {
			String message = response.getMessage();
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

				GFileManager.writeStoryToFile(mContext, GFileManager.STORY_FILE
						+ story_id, json);

				String modifiedTime = response.getModifiedTime();
				if (!TextUtils.isEmpty(modifiedTime)) {
					Utils.putToPreference(mContext,
							Keywords.STORY_UPDATED_TIME, modifiedTime);
				}
			} else {
				// obj = new JSONObject(getStoryFromLocalFile());
				/*
				 * 103 -- user is not privileged
				 */
				/*Toast.makeText(
						mContext,
						"Not able to fetch data. Loading data from local space",
						Toast.LENGTH_SHORT).show();*/
				if (!response.getStatus().equalsIgnoreCase("103") /*
																 * && !response.
																 * getStatus().
																 * equalsIgnoreCase
																 * ("100")
																 */) {
					Gson gson_obj = new Gson();
					response = gson_obj.fromJson(GFileManager.getStoryFromFile(
							GFileManager.STORY_FILE, mContext),
							GGetStoryResponseBean.class);
				}
			}

			List<NodeData> stories_Array = response.getNodeData(); // story
			

			if (stories_Array != null) {
				int stories_length = stories_Array.size();
				for (int i = 0; i < stories_length; i++) {
					MainNode main_node = new MainNode();
					NodeData story_Obj = stories_Array.get(i);
					main_node.setNodeKey(story_Obj.getNodeKey());
					main_node.setnodeTitle(story_Obj.getNodeTitle());
//					main_node.setRootCause(story_Obj.getRootCause());

					List<SubNodeBean> subNode_Array = story_Obj.getSubNodes();
					ArrayList<SubNodes> listSubNodes = new ArrayList<SubNodes>();

					if (subNode_Array != null && subNode_Array.size() > 0) {
						for (int j = 0; j < subNode_Array.size(); j++) {

							SubNodes sub_nodes = new SubNodes();
							SubNodeBean subNode_Obj = subNode_Array.get(j);
							sub_nodes
									.setsubNodeKey(subNode_Obj.getSubNodeKey());
							sub_nodes.setsubNodetitle(subNode_Obj
									.getSubNodeTitle());
							sub_nodes
									.setartifactId(subNode_Obj.getArtifactId());
							sub_nodes.setCheckList(subNode_Obj.getCheckList());
							if (sub_nodes.getCheckList() != null) {
								if (sub_nodes.getCheckList().equalsIgnoreCase(
										"yes")) {
									sub_nodes.setCheckListDone(false);
								} else {
									sub_nodes.setCheckListDone(true);
								}
							} else {
								sub_nodes.setCheckListDone(true);
							}

							sub_nodes
									.setColor_code(subNode_Obj.getColor_code());
							sub_nodes.setSelfhelp(subNode_Obj.getSelfHelp());

							if (null != subNode_Obj.getHotSpots()) {
								List<HotSpotBean> hotSpot_Array = subNode_Obj
										.getHotSpots();
								ArrayList<HotSpots> listHotSpots = new ArrayList<HotSpots>();

								for (int k = 0; k < hotSpot_Array.size(); k++) {
									HotSpots hot_spots = new HotSpots();
									HotSpotBean hotSpot_Obj = hotSpot_Array
											.get(k);
									hot_spots.setKey(hotSpot_Obj.getKey());
									hot_spots.setx(hotSpot_Obj.getX());
									hot_spots.sety(hotSpot_Obj.getY());
									GLog.i("GStoryController", "Radius is "
											+ hotSpot_Obj.getRadius());
									// hot_spots.setRadius((int)
									// Double.parseDouble(hotSpot_Obj.getRadius()));

									String rad = (hotSpot_Obj.getRadius());
									Double radDub = Double.parseDouble(rad);

									hot_spots
											.setRadius(Math.round(radDub) + "");

									GLog.i("GStoryController",
											"Radius is ************************  "
													+ hot_spots.getRadius());

									// hot_spots.setRadius((hotSpot_Obj.getRadius()));

									if (null != hotSpot_Obj.getLinks()) {
										Gson gson = new Gson();
										String json = gson.toJson((hotSpot_Obj
												.getLinks()));

										// hot_spots.setlinks((hotSpot_Obj.getLinks()).toString());
										hot_spots.setlinks(json);
										// System.out.println("hot story obj->"+hotSpot_Obj.getString("key"));
									} else {
										hot_spots.setlinks("");
									}
									listHotSpots.add(hot_spots);
								}

								sub_nodes.sethotspots(listHotSpots);

							}
							if (null != subNode_Obj.getTextSpots()) {
								List<TextSpotBean> textSpot_Array = subNode_Obj
										.getTextSpots();
								ArrayList<TextSpots> listTextSpots = new ArrayList<TextSpots>();
								if (textSpot_Array != null) {
									for (int p = 0; p < textSpot_Array.size(); p++) {
										TextSpots textSpots = new TextSpots();
										TextSpotBean textSpot_Obj = textSpot_Array
												.get(p);
										textSpots.setKey(textSpot_Obj.getKey());
										textSpots.setContent(textSpot_Obj
												.getContent());
										textSpots.setHeight(textSpot_Obj
												.getHeight());
										textSpots.setWidth(textSpot_Obj
												.getWidth());
										textSpots.setX(textSpot_Obj.getX());
										textSpots.setY(textSpot_Obj.getY());
										listTextSpots.add(textSpots);
									}
								}
								sub_nodes.setTextSpots(listTextSpots);
							}
							listSubNodes.add(sub_nodes);
						}
					}

					main_node.setSubNodes(listSubNodes);

					if (null != story_Obj.getCriteria()) {
						main_node.setCriteria(story_Obj.getCriteria());
						if (criteriaSatisfied(story_Obj.getCriteria())) {
							listCriteriaSatisfiedNodes.add(main_node);
						}
					} else {
						// If there is no criteria, take the default one.
						listCriteriaSatisfiedNodes.add(main_node);
					}

				}
			}
			
			mNodeList.setMainNodeList(listCriteriaSatisfiedNodes);
			mNodeList.setRootCause(response.getRootCause());
			rBean.setResponse(mNodeList);
//			rBean.setResponse(listCriteriaSatisfiedNodes);
			rBean.setStatusBean(sBean);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mNodeList.setMainNodeList(listCriteriaSatisfiedNodes);
			mNodeList.setRootCause(response.getRootCause());
			rBean.setResponse(mNodeList);
//			rBean.setResponse(listCriteriaSatisfiedNodes);
			if (null != sBean) {
				sBean.setStatusCode(STATUS_FAILED);
				rBean.setStatusBean(sBean);
			}
		}
		postResultToUI(rBean);
	}

	public boolean criteriaSatisfied(String criteria) {

		int len = 0;

		if (criteria.split("[|&]").length > 1) {
			timeconditionList.clear();
			for (String s : criteria.split("[|&]")) {
				if (s.trim().length() > 0) {
					TimeLocCondition tloc = new TimeLocCondition();

					len = (len == 0) ? len + s.length() : len + s.length() + 2;

					if (len < criteria.length())
						tloc.setCondition(criteria.substring(len, len + 2));
					else
						tloc.setCondition(".");

					tloc.setConditionVal(timeGpsCondition(s));

					timeconditionList.add(tloc);
				}

			}
			if (timeconditionList.size() > 1) {
				boolean value = timeconditionList.get(0).getConditionVal();

				for (int i = 1; i < timeconditionList.size(); i++) {

					value = timeconditionList.get(i - 1).getCondition()
							.equals("||") ? value
							|| timeconditionList.get(i).getConditionVal()
							: value
									&& timeconditionList.get(i)
											.getConditionVal();

				}
				return value;
			} else {
				return timeconditionList.get(0).getConditionVal();
			}
		} else {
			return timeGpsCondition(criteria);
		}

	}

	public boolean timeGpsCondition(String cond) {
		if (cond.startsWith("GPS")) {

			// return gpsParsing(cond.substring(cond.lastIndexOf(":") + 1));
			return true;
		} else {
			return timeParsing(cond.substring(cond.lastIndexOf(":") + 1));
		}

	}

	private boolean gpsParsing(String gps) {
		StringTokenizer st2 = new StringTokenizer(gps, ";");
		JSONObject gps_obj = new JSONObject();
		try {
			while (st2.hasMoreElements()) {
				String val = st2.nextElement().toString();
				gps_obj.putOpt(val.substring(0, val.indexOf("=")),
						val.substring(val.indexOf("=") + 1));

			}

			GLog.d("TCS", gps_obj.toString());
			GLog.d("TCS", "****** Device Current Location ****** "
					+ GLocationManager.CurrentLocation.getLatitude() + ":"
					+ GLocationManager.CurrentLocation.getLongitude());

			int distance = (int) GLocationManager.getDistanceBetweenTwoLatLon(
					gps_obj.getDouble("LAT"), gps_obj.getDouble("LONG"),
					GLocationManager.CurrentLocation.getLatitude(),
					GLocationManager.CurrentLocation.getLongitude());

			// int radius = gps_obj.getInt("DIST");
			int radius = 2000;

			if (radius >= distance) {
				GLog.d("distance satisfied", "radius->" + radius + "dist->"
						+ distance);
				return true;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Math.round(pointA.distanceTo(pointB));
		return false;
	}

	private boolean timeParsing(String time) {

		String dtstart = time.split(";")[0];

		String tz = time.split(";")[1];

		String rrule = time.substring(time.indexOf(time.split(";")[2]));

		GLog.d("dtstart", dtstart);
		GLog.d("tz-->", tz);
		GLog.d("rrule", rrule);

		LocalDate ld = new LocalDate(Long.parseLong(dtstart.substring(dtstart
				.indexOf("=") + 1)));

		return Utils.icalFormat(ld, rrule, tz.substring(tz.indexOf("=") + 1));
		// return true;
	}

	/**
	 * Function to check for Story Updates
	 * **/
	private void checkForUpdates() {
		JSONObject input_json = new JSONObject();
		try {
			input_json.putOpt(Constants.CONTEXT,
					Utils.getUserTenantObject(user_id, tenant_id));
			input_json.putOpt(Constants.STORY_ID, story_id);
			input_json.putOpt("timeUpdated", last_modified_time);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GServiceBundle bundle = new GServiceBundle();
		bundle.setConnectionType(GServiceBundle.POST);
		bundle.setServiceUrl(Constants.getServiceUrl(
				Constants.CHECK_FOR_STORY_UPDATE, mContext));
		bundle.setResponseType(GGetStoryResponseBean.class);
		bundle.setRequestObject(input_json);

		ResponseBean rBean = fetchDataFromService(bundle);

		if (rBean.getStatusBean().isSuccess()) {
			parseResponse((GGetStoryResponseBean) rBean.getResponse());
		} else {
			Gson gson_obj = new Gson();
			GGetStoryResponseBean response = gson_obj.fromJson(GFileManager
					.getStoryFromFile(GFileManager.STORY_FILE, mContext),
					GGetStoryResponseBean.class);
			parseResponse(response);
		}

	}

}
