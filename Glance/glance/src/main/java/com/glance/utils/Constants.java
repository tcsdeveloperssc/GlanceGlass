package com.glance.utils;

import java.util.ArrayList;

import android.content.Context;
import android.provider.Settings.Secure;

public final class Constants {

	public static final String BASE_URL_DEMO = "https://glance.tcsmobilitycloud.com";

	// testing server
	public static final String BASE_URL_TEST = "https://test-glance.tcsmobilitycloud.com";

	public static final String BASE_URL_PRODUCTION = "";

	public static String url;

	public static final String token_Reg_Service = "/webService/user/genKeyPair";

	public static final String auth_Glass_Service = "/webService/user/authGlassService";

	public static final String story_Service = "/webService/story/getStoryById";//

	public static final String tagStoryToSelfHelp_Service = "/webService/tasks/tagStoryToSelfHelp";

	public static final String story_list_Service = "/webService/story/getStoriesList";

	public static final String updateTaskCheckList = "/webService/tasks/updateTaskCheckLists";

	public static final String logout_Service = "/webService/user/logout";//

	public static final String get_Artifact_Service = "/webService/tenant/getArtifactById";

	public static final String get_TaggedStory_Service = "/webService/user/showTaggedStories";

	public static final String get_File_Service = "/fileServer?";

	public static final String user_task_queue = "/webService/tasks/getUserTasksQueue";

	public static final String get_mail = "/webService/alerts/getUserMails";

	public static final String send_mail = "/webService/alerts/sendUserAlerts";

	public static final String versionCheck_Service = "/webService/user/checkForUpdate";

	public static final String CHANGE_TASK_STATUS = "/webService/tasks/changeTaskStatus";

	public static final String CHECK_FOR_STORY_UPDATE = "/webService/story/checkForUpdate";

	public static final String FILE_UPLOAD = "/fileUpload";

	public static final String GET_ASSET_DETAILS = "/webService/assets/getAssetDetails";

	public static final String GET_ASSET_TASK_LIST = "/webService/assets/getAssetTasks";

	public static final String GET_NEARBY_USERLIST = "/webService/user/getNearByUsersDevice";

	public static final String SAVE_USER_LOCATION = "/webService/user/saveUserLocation";
	
	public static final String GET_ASSIGNED_SURVEY = "/webService/survey/getAssignedSurveys";
	
	public static final String SAVE_SURVEY = "/webService/survey/saveSurveyResult";
	
	public static final String SUBMIT_SURVEY = "/webService/survey/submitSurveyResult";
	
	public static final String GET_SURVEY_RESULTS = "/webService/survey/getSurveyResult";
	
	public static final String MENU_NEARBY_USERS = "Nearby Users";
	
	public static final String MENU_START_SURVEY = "Start Survey";

	public static String VIDEO_URL_KEY = "video_url";

	public static String AUDIO_URL_KEY = "audio_url";

	public static String IMAGE_URL_KEY = "image_url";

	public static final String MENU_LAUNCH = "Launch";

	public static final String MENU_TASKS = "Tasks";

	public static final String MENU_NOTIFICATIONS = "Notifications";

	public static final String MENU_SETTINGS = "Settings";

	public static final String MENU_LOGOUT = "Logout";

	public static final String MENU_ASSET_INSPECTION = "Asset Inspection";

	public static final String MENU_IMAGE_DETECTION = "Image Detection";

	public static final String MENU_AR = "Visual Inspection";

	public static final String MENU_SYNC = "Sync";

	public static final String SUBNODE_KEYS = "SubNodeKeys";

	public static final String MENU_SELF_HELP = "Self Help";

	public static final String OFFLINE_MODE = "OFFLINE_MODE";

	public static final String DRAFT_COMPLETE = "DRAFT_COMPLETE";
	public static final String RUNNING = "RUNNING";

	public static final String OFFLINE_EDIT = "OFFLINE_EDIT";

	public static final String NEW_MAIL = "NEW_MAIL";

	public static final String NO = "NO";

	public static final String YES = "YES";

	public static final String ASSIGNED = "assigned";

	public static String WEB_SOCKET_URL;
	// web socket
	public static String FAYE_DEMO_URL = "glance.tcsmobilitycloud.com/ws"; // "//"glance.tcsmobilitycloud.com:8000/ws";http://glance.tcsmobilitycloud.com:8000/ws
	// {'message':'success','deviceId':'62c489866837b277'}
	// public static String WEB_PORTAL_SOCKET_URL =
	// "mobileweb.tcsmobilitycloud.com:8000/faye";

	// public static String FAYE_TEST_URL =
	// "test-glance.tcsmobilitycloud.com:8000/ws";

	public static String FAYE_TEST_URL = "dev-glance.tcsmobilitycloud.com/ws";

	public static String CHANNEL = "";

	public static String TAG = "FaultRepair";

	public static final String device_type = "GoogleGlass";

	public static int SHOW_HOTSPOT_ONE = 1;

	public static int SHOW_HOTSPOT_TWO = 2;

	public static int SHOW_HOTSPOT_THREE = 3;

	public static final int SHOW_HOTSPOT_FOUR = 4;

	public static final int SHOW_HOTSPOT_FIVE = 5;

	public static int SHOW_TASK = 6;

	public static int NEXT = 7;

	public static int PREVIOUS = 8;

	public static int PLAY = 9;

	public static int PAUSE = 10;

	public static int STOP = 11;

	public static int SPOT_COMPLETED = 12;

	public static int TASK_COMPLETED = 13;

	public static int START_TASK = 14;

	public static int START_HOTSPOT_1 = 15;

	public static int START_HOTSPOT_2 = 16;

	public static int START_HOTSPOT_3 = 17;

	public static int START_HOTSPOT_4 = 18;

	public static int START_HOTSPOT_5 = 19;

	public static int STOP_HOTSPOT_1 = 20;

	public static int STOP_HOTSPOT_2 = 21;

	public static int STOP_HOTSPOT_3 = 22;

	public static int STOP_HOTSPOT_4 = 23;

	public static int STOP_HOTSPOT_5 = 24;

	public static int START_TO = 25;

	public static int SHOW_2 = 26;

	public static int TAKE_PICTURE = 27;

	public static int TAKE_A_PICTURE = 28;

	public static int DONE = 29;

	public static int MAKE_A_CALL = 30;

	public static int SHOW_1 = 31;

	public static final int LAUNCH = 32;

	public static final int SETTINGS = 33;

	public static final int HELP = 34;

	public static final int EXIT = 35;

	public static final int MAILBOX = 36;

	public static int SHOW_3 = 37;

	public static int SHOW_4 = 38;

	public static int SHOW_5 = 39;

	public static int NAVIGATE = 40;

	public static int CHECK = 41;

	public static int CHECKED = 42;

	public static int TAKE_A_VIDEO = 43;

	public static int TAKE_VIDEO = 44;

	public static int TAKE_AN_AUDIO = 45;

	public static int TAKE_AUDIO = 46;

	public static final int LOGOUT_KEY_1 = 1012131;

	public static final int LOGOUT_KEY_2 = 1012132;

	public static String ACCEPT_TYPE = "application/json";

	public static String MEMORY_ERROR = "error";

	public static final String USER_ID = "userId";

	public static final String TENANT_ID = "tenantId";

	public static final String REQ_FROM = "reqFrom";

	public static final String CONTEXT = "context";

	public static final String STORY_ID = "storyId";

	public static final String TASK_ID = "taskId";

	public static final String STATUS = "status";

	public static final String MESSAGE = "message";

	public static final String DEVICE_TOKEN = "deviceToken";

	public static final String DEVICE_TYPE = "deviceType";

	public static final String SPEECH_INTENT = "speech_intent";

	public static final String SPEECH_ARRAY = "speech_array";

	public static final String LOGIN_STATUS = "login status";

	public static final String ASSET_ID = "assetId";

	public static final String MENU_FRIDGE = "fridge4";

	public static final String MENU_CLUTCH = "clutch";

	public static final String TASK_LIST = "taskList";

	public static String ACTION = "action";

	public static String SELF_HELP = "selfHelp";

	public static final String LONGITUDE = "longitude";

	public static final String LATITUDE = "latitude";

	public static final String DISTANCELIMIT = "distanceLimit";
	
	public static final String SURVEY = "survey";

	public static final int IMAGE = 1;

	public static final int VIDEO = 2;

	public static final int AUDIO = 3;

	
	//option types
	public static final String OPT_RADIO = "Radio";
	public static final String OPT_LIST = "list";
	public static final String OPT_YES_NO = "YesOrNo";
	public static final String OPT_AUDIO = "audio";
	public static final String OPT_VIDEO = "video";
	public static final String OPT_IMAGE = "image";
	
	
	public static void deviceToken(Context context) {

		Constants.CHANNEL = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);

	}

	public class ErrorCodes {
		public static final int NO_NETWORK = 103;
	}

	public class Faye {
		public static final String DEVICE_REG_OK = "dev_reg_ok";
	}

	public class FayeAlert {
		public static final String DEVICE_ALERT_OK = "newTask";
		public static final String MAIL_ALERT_OK = "newAlert";
		public static final String SCREEN_STATUS_OK = "screenStatus";
		public static final String POINTS_ALERT = "pointAlert";
	}

	public class Keywords {
		public static final String RESPONSE = "response";
		public static final String SUCCESS = "success";
		public static final String TRUE = "true";
		public static final String FALSE = "false";
		public static final String FAILURE = "failure";
		public static final String DATA = "data";
		public static final String GLASS_REGISTERED = "glass_registered";
		public static final String STORY_UPDATED_TIME = "story_updated_time";
		public static final String STORY_STATUS = "story_status";
		public static final String USER_NAME = "username";
		public static final String SCREEN_STATUS = "GET_CURRENT_SCREEN";
		public static final String ROOT_CAUSE = "rootCause";
		public static final String ROOT_CAUSE_LIST = "rootCauseList";
		public static final String FILE_PATH = "filePath";
		public static final String SUBMIT_STATUS = "submitStatus";
	}

	public static void setBaseUrl(String baseUrl) {
		url = baseUrl;
	}

	public static String getBaseUrl() {
		return url;
	}

	public static String getFayeurl() {

		return Constants.WEB_SOCKET_URL;
	}

	public static void setFayeUrl(String url) {
		Constants.WEB_SOCKET_URL = url;
	}

	public static String getServiceUrl(String serviceName, Context mContext) {
		// Constants.setBaseUrl(Utils.getSavedUrl(mContext));
		String url = Utils.getSavedUrl(mContext);

		if (token_Reg_Service.equals(serviceName)) {
			url += token_Reg_Service;
		} else if (auth_Glass_Service.equals(serviceName)) {
			url += auth_Glass_Service;
		} else if (story_Service.equals(serviceName)) {
			url += story_Service;
		} else if (logout_Service.equals(serviceName)) {
			url += logout_Service;
		} else if (get_Artifact_Service.equals(serviceName)) {
			url += get_Artifact_Service;
		} else if (get_TaggedStory_Service.equals(serviceName)) {
			url += get_TaggedStory_Service;
		} else if (get_File_Service.equals(serviceName)) {
			url += get_File_Service;
		} else if (user_task_queue.equals(serviceName)) {
			url += user_task_queue;
		} else if (get_mail.equals(serviceName)) {
			url += get_mail;
		} else if (send_mail.equals(serviceName)) {
			url += send_mail;
		} else if (CHANGE_TASK_STATUS.equals(serviceName)) {
			url += CHANGE_TASK_STATUS;
		} else if (CHECK_FOR_STORY_UPDATE.equals(serviceName)) {
			url += CHECK_FOR_STORY_UPDATE;
		} else if (FILE_UPLOAD.equals(serviceName)) {
			url += FILE_UPLOAD;
		} else if (GET_ASSET_DETAILS.equals(serviceName)) {
			url += GET_ASSET_DETAILS;
		} else if (versionCheck_Service.equals(serviceName)) {
			url += versionCheck_Service;
		} else if (GET_ASSET_TASK_LIST.equalsIgnoreCase(serviceName)) {
			url += GET_ASSET_TASK_LIST;
		} else if (story_list_Service.equals(serviceName)) {
			url += story_list_Service;
		} else if (GET_NEARBY_USERLIST.equals(serviceName)) {
			url += GET_NEARBY_USERLIST;
		} else if (SAVE_USER_LOCATION.equals(serviceName)) {
			url += SAVE_USER_LOCATION;
		} else if (tagStoryToSelfHelp_Service.equals(serviceName)) {
			url += tagStoryToSelfHelp_Service;
		} else if (updateTaskCheckList.equals(serviceName)) {
			url += updateTaskCheckList;
		}else if (GET_ASSIGNED_SURVEY.equals(serviceName)) {
			url += GET_ASSIGNED_SURVEY;
		}else if (GET_SURVEY_RESULTS.equals(serviceName)) {
			url += GET_SURVEY_RESULTS;
		}else if (SUBMIT_SURVEY.equals(serviceName)) {
			url += SUBMIT_SURVEY;
		}else if (SAVE_SURVEY.equals(serviceName)) {
			url += SAVE_SURVEY;
		}


		return url;
	}

	public static int getVideoIndex(String videourl) {

		ArrayList<String> list = Utils.getVideoUrl();
		if (list.contains(videourl)) {
			return list.indexOf(videourl);
		}
		return -1;
	}

}
