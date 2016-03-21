package com.glance.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.glance.R;
import com.glance.bean.response.GAuthResponseBean;
import com.glance.database.DatabaseHelper;
import com.glance.faye.CopyOfAlertNotificationService;
import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

public class Utils {

	static boolean isDevelopment = false;
	public static DatabaseHelper databaseHelper = null;
	public static String URL_PREF = "urlPrefs";
	public static String URL = "url";
	public static String FAYE_URL = "fayeurl";

	public static void createDatabase(Context context) {
		databaseHelper = new DatabaseHelper(context);

	}

	public static void deleteDatabase() {
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}

	}

	public static void log(String tagName, String value) {
		if (isDevelopment)
			System.out.println(tagName + "->" + value);
	}

	/*public static String getAdditionalDeviceId() {
		String devid = "";
		if (isDevelopment)
			devid = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "83";
		else
			devid = "";
		return devid;
	}*/

	public static String readStringFromAssetsFile(String fileName, Context ctx) {
		String data = "";
		try {
			InputStream inputStream = ctx.getAssets().open(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String line = null;
			StringBuffer stringBuffer = new StringBuffer();

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			data = stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			data = "";
		}
		return data;
	}

	public static int keyFinder(ArrayList<String> speechToTextList) {

		HashMap<String, Integer> videoKeys = getHotspotsKeys();

		//changes to lowercase because method getHotspotskeys() has lower case
		//strings but Glass capitalizes the first letter in voice input string
		for(int i = 0; i < speechToTextList.size(); i++)
		{
			speechToTextList.set(i, speechToTextList.get(i).toLowerCase());
		}

		System.out.println(" speechToTextList : " + speechToTextList);
		for (String videoKey : videoKeys.keySet()) {
			int value = videoKeys.get(videoKey);
			System.out.println(" videoKey : " + value);

			if (speechToTextList.contains(videoKey.trim())) {
				System.out.println(" keyFinder : Contains value----returning "
						+ value);
				return value;
			}
		}
		System.out.println(" keyFinder : Returning true");
		return 0;
	}

	public static boolean logOutkeyFinder(ArrayList<String> speechToTextList) {

		HashMap<String, Integer> videoKeys = getLogoutKeys();
		System.out.println(" speechToTextList : " + speechToTextList);
		for (String videoKey : videoKeys.keySet()) {
			int value = videoKeys.get(videoKey);
			System.out.println(" Logout : " + value);

			if (speechToTextList.contains(videoKey.trim())) {
				return true;
			}
		}
		System.out.println(" Logout : Returning false");
		return false;
	}

	public static HashMap<String, Integer> getHotspotsKeys() {

		HashMap<String, Integer> videoKeys = new HashMap<String, Integer>();
		videoKeys.put("show one", Constants.SHOW_HOTSPOT_ONE);
		videoKeys.put("show two", Constants.SHOW_HOTSPOT_TWO);
		videoKeys.put("show three", Constants.SHOW_HOTSPOT_THREE);
		videoKeys.put("show four", Constants.SHOW_HOTSPOT_FOUR);
		videoKeys.put("show five", Constants.SHOW_HOTSPOT_FIVE);
		videoKeys.put("one", Constants.SHOW_HOTSPOT_ONE);
		videoKeys.put("hotspot one", Constants.SHOW_HOTSPOT_ONE);
		videoKeys.put("show", Constants.SHOW_TASK);
		videoKeys.put("next", Constants.NEXT);
		videoKeys.put("previous", Constants.PREVIOUS);
		videoKeys.put("play", Constants.PLAY);
		videoKeys.put("pause", Constants.PAUSE);
		videoKeys.put("stop", Constants.STOP);
		videoKeys.put("spot completed", Constants.SPOT_COMPLETED);
		videoKeys.put("completed", Constants.TASK_COMPLETED);
		videoKeys.put("start task", Constants.START_TASK);
		videoKeys.put("start one", Constants.START_HOTSPOT_1);
		videoKeys.put("start two", Constants.START_HOTSPOT_2);
		videoKeys.put("start to", Constants.START_TO);
		videoKeys.put("start three", Constants.START_HOTSPOT_3);
		videoKeys.put("start four", Constants.START_HOTSPOT_4);
		videoKeys.put("start five", Constants.START_HOTSPOT_5);
		videoKeys.put("stop hotspot one", Constants.STOP_HOTSPOT_1);
		videoKeys.put("stop hotspot two", Constants.STOP_HOTSPOT_2);
		videoKeys.put("stop hotspot three", Constants.STOP_HOTSPOT_3);
		videoKeys.put("stop hotspot four", Constants.STOP_HOTSPOT_4);
		videoKeys.put("stop hotspot five", Constants.STOP_HOTSPOT_5);
		videoKeys.put("show 2", Constants.SHOW_2);
		videoKeys.put("show 3", Constants.SHOW_3);
		videoKeys.put("show 4", Constants.SHOW_4);
		videoKeys.put("show 5", Constants.SHOW_5);
		videoKeys.put("take picture", Constants.TAKE_PICTURE);
		videoKeys.put("take a picture", Constants.TAKE_A_PICTURE);
		videoKeys.put("done", Constants.DONE);
		videoKeys.put("make a call", Constants.MAKE_A_CALL);
		videoKeys.put("show 1", Constants.SHOW_1);
		videoKeys.put("launch", Constants.LAUNCH);
		videoKeys.put("help", Constants.HELP);
		videoKeys.put("settings", Constants.SETTINGS);
		videoKeys.put("exit", Constants.EXIT);
		videoKeys.put("mail box", Constants.MAILBOX);
		videoKeys.put("navigate", Constants.NAVIGATE);
		videoKeys.put("check", Constants.CHECK);		
		videoKeys.put("checked", Constants.CHECKED);
		videoKeys.put("take video", Constants.TAKE_VIDEO);
		videoKeys.put("take a video", Constants.TAKE_A_VIDEO);
		videoKeys.put("take audio", Constants.TAKE_AUDIO);
		videoKeys.put("take an audio", Constants.TAKE_AN_AUDIO);
		videoKeys.put("show task", Constants.SHOW_TASK);
		videoKeys.put("show tasks", Constants.SHOW_TASK);
		videoKeys.put("show to", Constants.SHOW_2);

		/*
		 * videoKeys.put("video", Global.SHOW_VIDEO_CODE); videoKeys.put("how",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show video",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("help",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("help me",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("tutorial",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show the video",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show the videos",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show me the video",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show me the videos",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show the clue",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("show the clues",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("ok glass show the video",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("ok glass show me the video",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("ok glass show the me videos",
		 * Global.SHOW_VIDEO_CODE); videoKeys.put("ok glass show the videos",
		 * Global.SHOW_VIDEO_CODE);
		 */

		return videoKeys;
	}

	public static HashMap<String, Integer> getLogoutKeys() {

		HashMap<String, Integer> videoKeys = new HashMap<String, Integer>();
		videoKeys.put("Logout", Constants.LOGOUT_KEY_1);
		videoKeys.put("Log out", Constants.LOGOUT_KEY_2);
		videoKeys.put("logout", Constants.LOGOUT_KEY_2);
		return videoKeys;
	}

	public static JSONObject getOnlineUserMessage() {

		JSONObject olineJsonObj = new JSONObject();

		return olineJsonObj;
	}

	public static JSONObject getUserTenantObject(String user_id,
			String tenant_id) {

		JSONObject ctx_input_json = new JSONObject();
		try {
			ctx_input_json.putOpt(Constants.USER_ID, user_id);
			ctx_input_json.putOpt(Constants.TENANT_ID, tenant_id);
			ctx_input_json.putOpt(Constants.REQ_FROM, "G");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ctx_input_json;
	}

	public static void setCredentials(JSONObject obj, Context mContext) {

		Context context = mContext.getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		try {

			editor.putString(
					context.getString(R.string.preference_security_token),
					obj.getString("authCode"));
			editor.putString(context.getString(R.string.preference_user_id),
					obj.getString("userId"));
			editor.putString(
					context.getString(R.string.preference_tenant_id),
					obj.getJSONArray("regTenants").getJSONObject(0)
							.getString("tenantId"));

			editor.commit();
		} catch (Exception e) {

		}
	}

	public static void setUserCredentials(GAuthResponseBean bean,
			Context mContext) {

		Context context = mContext.getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		try {

			editor.putString(
					context.getString(R.string.preference_security_token),
					bean.getAuthCode());
			editor.putString(context.getString(R.string.preference_user_id),
					bean.getUserId());
			editor.putString(context.getString(R.string.preference_tenant_id),
					bean.getRegTenants().get(0).getTenantId());
			editor.putString(context.getString(R.string.preference_story_id),
					bean.getStoryId());
			editor.commit();
		} catch (Exception e) {

		}
	}

	public static SharedPreferences getCredentials(Context mContext) {
		Context context = mContext.getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		return sharedPref;
	}

	public static boolean icalFormat(LocalDate start, String RRule, String tz) {
		LocalDate current = new LocalDate();

		try {
			if (tz.equalsIgnoreCase("local")) {
				for (LocalDate date : LocalDateIteratorFactory
						.createLocalDateIterable("RRULE:" + RRule, start, true)) {
					if (current.equals(date)) {
						return true;
					} else if (date.isAfter(current)) {
						return false;
					}
				}
			} else {
				DateTimeZone zone = DateTimeZone.forID(tz);
				for (LocalDate date : LocalDateIteratorFactory
						.createLocalDateIterable("RRULE:" + RRule, start, zone,
								true)) {
					if (current.equals(date)) {
						return true;
					} else if (date.isAfter(current)) {
						return false;
					}
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static final class GLog {
		public static void e(String tag, String errorMessage) {
			if (isDevelopment) {
				Log.e(tag, errorMessage);
			}
		}

		public static void i(String tag, String infoMessage) {
			if (isDevelopment) {
				Log.i(tag, infoMessage);
			}
		}

		public static void d(String tag, String debugMessage) {
			if (isDevelopment) {
				Log.d(tag, debugMessage);
			}
		}

		public static void w(String tag, String warningMessage) {
			if (isDevelopment) {
				Log.w(tag, warningMessage);
			}
		}

	}

	public static boolean isValidString(String stringToCheck) {
		return !TextUtils.isEmpty(stringToCheck);
	}

	private static String SHARED_PREF_NAME = "glance";

	public static void putToPreference(Context mContext, String key,
			String value) {
		SharedPreferences sharedPreference = mContext.getSharedPreferences(
				SHARED_PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getFromPreference(Context mContext, String key) {
		SharedPreferences sharedPreference = mContext.getSharedPreferences(
				SHARED_PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreference.getString(key, "");
	}

	public static void clearSharedPref(Context mContext) {
		SharedPreferences sharedPref = mContext.getSharedPreferences(
				mContext.getString(R.string.preference_file_key),
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.clear();
		editor.commit();
		sharedPref = mContext.getSharedPreferences(SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		editor.clear();
		editor.commit();
		GFileManager.deleteFile(GFileManager.STORY_FILE, mContext);
	}

	public static ArrayList<String> getUrlList() {
		ArrayList<String> urlList = new ArrayList<String>();
		urlList.add(Constants.BASE_URL_DEMO);
		urlList.add(Constants.BASE_URL_TEST);
		return urlList;
	}

	public static ArrayList<String> getMenuList(Context mContext) {
		ArrayList<String> urlList = new ArrayList<String>();
		
			urlList.add(mContext.getResources().getString(R.string.menu_tasks));

			urlList.add(mContext.getResources().getString(
					R.string.menu_notifications));
			urlList.add(mContext.getResources().getString(R.string.menu_ar));

			urlList.add(mContext.getResources().getString(
					R.string.menu_asset_inspection));
			urlList.add(mContext.getResources().getString(
					R.string.menu_self_help));
			urlList.add(mContext.getResources().getString(
					R.string.menu_start_survey));
			if (getFromPreference(mContext, Constants.OFFLINE_MODE).equals(
					"false"))
				urlList.add(mContext.getResources().getString(
						R.string.menu_sync));
			urlList.add(mContext.getResources().getString(R.string.menu_nearby_users));
			urlList.add(mContext.getResources().getString(R.string.menu_image_detection));
			urlList.add(mContext.getResources().getString(
					R.string.menu_settings));

			urlList.add(mContext.getResources().getString(R.string.menu_logout));
			
		return urlList;
	}

	public static ArrayList<String> getVideoUrl() {

		ArrayList<String> list = new ArrayList<String>();
		list.add("Glance_tenant_525_VIDEO_20131231023731.070.m4v");
		list.add("Glance_tenant_525_VIDEO_20131227022252.698.m4v");
		return list;
		

	}

	public static boolean checkNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public static String getSavedUrl(Context context) {

		SharedPreferences prefs = context.getSharedPreferences(URL_PREF, Context.MODE_PRIVATE);
		return prefs.getString(URL, Constants.BASE_URL_TEST);

	}

	public static String getFayeUrl(Context context) {

		SharedPreferences prefs = context.getSharedPreferences(URL_PREF, Context.MODE_PRIVATE);
		return prefs.getString(FAYE_URL,Constants.FAYE_TEST_URL);

	}

	public void startFayeServices(Context mContext) {
		Intent fayeAlertIntent = new Intent(mContext, CopyOfAlertNotificationService.class);
		mContext.startService(fayeAlertIntent);	

	}
	
	public static void saveUrl(Context mContext,String url) {

		SharedPreferences prefs = mContext.getSharedPreferences(URL_PREF,
				Context.MODE_PRIVATE);

		if (Constants.BASE_URL_DEMO.equals(url)) {
			Constants.setFayeUrl(Constants.FAYE_DEMO_URL);
		} else {
			Constants.setFayeUrl(Constants.FAYE_TEST_URL);
		}
		prefs.edit().putString(URL, url).commit();
		prefs.edit().putString(FAYE_URL, Constants.getFayeurl()).commit();
		Constants.setBaseUrl(url);
	}

	
}
