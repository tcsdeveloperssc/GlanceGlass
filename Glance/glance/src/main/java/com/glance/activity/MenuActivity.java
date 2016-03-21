package com.glance.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.MenuAdapter;
import com.glance.bean.response.GTokenResponseBean;
import com.glance.bean.response.GVersionCheckResponseBean;
import com.glance.controller.GRegistrationController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.faye.CopyOfAlertNotificationService;
import com.glance.faye.ScreenStatusService;
import com.glance.faye.WebPortalSocketService;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Faye;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.CustomTypefaceSpan;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollView;

public class MenuActivity extends BaseActivity {

	private MenuAdapter mAdapter;
	private Context mContext;
	private LinearLayout dots_holder;
	private int currentPage;
	private ProgressBar pBarMenu;
	private ArrayList<String> list;
	// private static int mode = 0;
	private Intent fayeAlertIntent = null;
	public static Intent fayeWebPortalIntent = null;
	private Intent fayeScreenStausIntent;
	private CardScrollView mCardScrollView = null;
	private boolean isMenuItemSelected = false;
	private boolean isFromAR = false;
	private String currentMenu;
	private Handler handler = new Handler();
	private Intent gpsIntent = null;
	private RelativeLayout mainPager, deviceRegLayout;
	// MainActivity
	/*************************************/
	private RobotoTextView tvAuthKey, tvAssistUser, tvHelpLink;

	private ProgressBar pBar;

	public static Handler tokenRegiterHandler;

	private ControllerRequest regRequest;

	private ControllerRequest authRequest;

	private String url;
	private boolean open = false;

	private boolean isNotificationSet = false;
	// public static boolean TaskListActivityStarted = false;

	String key = "key yet to come", android_id = "", message = null,
			storyId = "", userId = "", tenantId = "";

	public static int VISUAL_INSPECTION_REQUEST = 1;

	public static int SETTINGS_CODE = 2;

	GTokenResponseBean tBean;
	private String apkName;

	Intent speechService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;

		super.onCreate(savedInstanceState);

		regRequest = new ControllerRequest(getApplicationContext(),
				GRegistrationController.ACTION_TOKEN_REGISTRATION);
		regRequest.setCallbackListener(regListener);
		authRequest = new ControllerRequest(getApplicationContext(),
				GRegistrationController.ACTION_AUTHENTICATION);
		authRequest.setCallbackListener(authListener);

		Utils.createDatabase(getApplicationContext());
		Constants.deviceToken(this);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(mFayeMessageReceiver,
						new IntentFilter(Faye.DEVICE_REG_OK));

		if (Utils.checkNetwork(mContext)) {
			Utils.putToPreference(mContext, Constants.OFFLINE_MODE, "false");
			
		} else {
			Utils.putToPreference(mContext, Constants.OFFLINE_MODE, "true");
			Toast.makeText(mContext,
					"Do you want to continue in Offline mode ?",
					Toast.LENGTH_SHORT).show();
			open = true;
			handler.postDelayed(runnable, 1000);

		}
		setContentView(R.layout.menu_pager);
		supportInvalidateOptionsMenu();
		Constants.setBaseUrl(Utils.getSavedUrl(mContext));
		Constants.setFayeUrl(Utils.getFayeUrl(mContext));
		startFayeServices();

		setupPager();
		setupApp();

	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (open == true)
				openOptionsMenu();

		}
	};

	public void startFayeServices() {
		fayeAlertIntent = new Intent(this, CopyOfAlertNotificationService.class);
		startService(fayeAlertIntent);
		fayeWebPortalIntent = new Intent(this, WebPortalSocketService.class);
		startService(fayeWebPortalIntent);
		fayeScreenStausIntent = new Intent(this, ScreenStatusService.class);
		startService(fayeScreenStausIntent);

	}

	public void stopFayeServices() {
		GLog.d("FAYE", "Stopping services");
		if (null != fayeAlertIntent)
			stopService(fayeAlertIntent);
		if (null != fayeWebPortalIntent)
			stopService(fayeWebPortalIntent);
		if (null != fayeScreenStausIntent)
			stopService(fayeScreenStausIntent);

	}

	@Override
	protected void onResume() {
		if (null != mCardScrollView)
			mCardScrollView.setVisibility(View.VISIBLE);
		if (null != dots_holder)
			dots_holder.setVisibility(View.VISIBLE);
		RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
		if (null != tv_userName)
			tv_userName.setVisibility(View.VISIBLE);
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		stopFayeServices();

		if (null != gpsIntent)
			stopService(gpsIntent);
		Utils.deleteDatabase();
		System.exit(0);
		super.onDestroy();
	}

	public void setupPager() {
		if (Utils.getFromPreference(mContext, Constants.OFFLINE_MODE)
				.equalsIgnoreCase("true")) {
			RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
			String userName = Utils.getFromPreference(mContext,
					Keywords.USER_NAME);
			tv_userName.setText("Welcome " + userName);
		}

		currentPage = 0;
		LinearLayout card_view = (LinearLayout) findViewById(R.id.card_view);
		dots_holder = (LinearLayout) findViewById(R.id.ll_scrollbar_menu);
		list = Utils.getMenuList(mContext);

		if (card_view != null)
			card_view.removeAllViews();
		mCardScrollView = new CardScrollView(this);
		mAdapter = new MenuAdapter(getApplicationContext(), list);
		mAdapter.notifyDataSetChanged();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.setOnItemClickListener(new CardClickListener());
		mCardScrollView.setOnItemSelectedListener(new CardSelectedListener());
		mCardScrollView.activate();
		card_view.addView(mCardScrollView);

		updateIndicator(currentPage);
		deviceRegLayout = (RelativeLayout) findViewById(R.id.rl_device_token);
		mainPager = (RelativeLayout) findViewById(R.id.rl_main_pager);
		deviceRegLayout.setVisibility(View.GONE);

		mainPager.setVisibility(View.VISIBLE);

		pBarMenu = (ProgressBar) findViewById(R.id.pbMainMenu);

	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {

			System.out.println("------>" + position + "-position");
			View view = mCardScrollView.getSelectedView();

			String menu_item = (String) ((RobotoTextView) view
					.findViewById(R.id.tv_menu_item)).getText();
			ArrayList<String> a = list;

			if ((menu_item.equalsIgnoreCase(Constants.MENU_TASKS))) {

				if (Utils.getFromPreference(mContext, Constants.OFFLINE_MODE)
						.equals("false")) {
					if (Utils.databaseHelper == null)
						Utils.createDatabase(mContext);
					if (Utils.databaseHelper.getTaskList().size() <= 0) {

						GLog.d("TAG",
								"Starting task list activity**********************");
						Intent i = new Intent(MenuActivity.this,
								TaskListActivity.class);
						i.putExtra("activity", "launch");
						startActivity(i);
					} else {
						Toast.makeText(getApplicationContext(),
								"Please sync your offline data",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					GLog.d("TAG",
							"Starting task list activity**********************");
					Intent i = new Intent(MenuActivity.this,
							TaskListActivity.class);
					i.putExtra("activity", "launch");
					startActivity(i);
				}
			} else if (menu_item
					.equalsIgnoreCase(Constants.MENU_ASSET_INSPECTION)) {
				Intent scanIntent = new Intent(MenuActivity.this,
						AssetScanActivity.class);
				startActivity(scanIntent);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_NOTIFICATIONS)) {
				isNotificationSet = true;
				open = true;
				if (null != mCardScrollView)
					mCardScrollView.setVisibility(View.INVISIBLE);
				if (null != dots_holder)
					dots_holder.setVisibility(View.INVISIBLE);
				RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
				if (null != tv_userName)
					tv_userName.setVisibility(View.INVISIBLE);
				openOptionsMenu();
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_SETTINGS)) {
				Intent settingsIntent = new Intent(MenuActivity.this,
						SettingsActivity.class);
				startActivityForResult(settingsIntent, SETTINGS_CODE);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_LOGOUT)) {
				logout();
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_AR)) {
				Intent intent = new Intent("com.glance.augmented.launch");
				// startActivity(intent);
				startActivityForResult(intent, VISUAL_INSPECTION_REQUEST);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_SYNC)) {
				Intent i = new Intent(MenuActivity.this, TaskListActivity.class);
				i.putExtra("activity", Constants.OFFLINE_MODE);
				startActivity(i);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_SELF_HELP)) {
				Intent i = new Intent(MenuActivity.this, SelfHelpActivity.class);
				startActivity(i);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_NEARBY_USERS)) {
				Intent i = new Intent(MenuActivity.this,
						NearbyUserListActivity.class);
				startActivity(i);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_START_SURVEY )) {
				Intent i = new Intent(MenuActivity.this,
						SurveyActivity.class);
				startActivity(i);
			} else if (menu_item.equalsIgnoreCase(Constants.MENU_IMAGE_DETECTION)){
				Log.d("glance.tom", "model: " + Build.MODEL.toString());
				Intent i = new Intent(MenuActivity.this,
						ImageDetectionActivity.class);
				startActivity(i);
			}

		}

	}

	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
		
			currentPage = pos;
			updateIndicator(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void updateIndicator(int currentPage) {

		if (dots_holder != null && mAdapter != null) {
			dots_holder.removeAllViews();
			DotsScrollBar.createDotScrollBar(mContext, dots_holder,
					currentPage, mAdapter.getCount());
		}

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
			finish();
			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VISUAL_INSPECTION_REQUEST) {
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				if ("fridge4".equalsIgnoreCase(result)) {
					currentMenu = Constants.MENU_FRIDGE;
					isFromAR = true;

				} else {
					currentMenu = Constants.MENU_CLUTCH;
					isFromAR = true;

				}
				GLog.d("Tinoj", "Reply:" + result);

				openOptionsMenu();
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		} else if (requestCode == SETTINGS_CODE) {

			if (resultCode == RESULT_OK) {
				if (data.getExtras() != null) {
					url = data.getStringExtra(Utils.URL);
				}
				logout();
			}else {
				
			}

		}
		// super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		open = false;
		handler.removeCallbacks(runnable);
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		if (isFromAR) {

			isFromAR = false;
			Typeface font = Typeface.createFromAsset(getAssets(),
					"fonts/Roboto-Light.ttf");
			if (Constants.MENU_CLUTCH.equals(currentMenu)) {
				inflater.inflate(R.menu.topdrive_menu, menu);

				SpannableStringBuilder text = new SpannableStringBuilder();
				text.append("Disc wear");
				text.setSpan(new CustomTypefaceSpan("", font), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
			
				MenuItem item1 = menu.findItem(R.id.topdrive_prob_1);
				item1.setTitle(text);

				MenuItem item2 = menu.findItem(R.id.topdrive_prob_2);
				text = new SpannableStringBuilder();
				text.append("Worn bearings");
				text.setSpan(new CustomTypefaceSpan("", font), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				item2.setTitle(text);

			} else {
				inflater.inflate(R.menu.torq_menu, menu);

				SpannableStringBuilder text = new SpannableStringBuilder();
				text.append("Bad bearing");
				text.setSpan(new CustomTypefaceSpan("", font), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

				MenuItem item1 = menu.findItem(R.id.torq_prob_1);
				item1.setTitle(text);

				MenuItem item2 = menu.findItem(R.id.torq_prob_2);
				text = new SpannableStringBuilder();
				text.append("Loose Valve");
				text.setSpan(new CustomTypefaceSpan("", font), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				item2.setTitle(text);

			}
		}

		else if (isNotificationSet) {
			isNotificationSet = false;
			inflater.inflate(R.menu.main_menu_options, menu);
			SpannableStringBuilder text = new SpannableStringBuilder();
			text.append("Inbox");
			text.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
					text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			MenuItem item1 = menu.findItem(R.id.inbox_menu_items);
			item1.setTitle(text);

			MenuItem item2 = menu.findItem(R.id.sent_items_menu_items);
			text = new SpannableStringBuilder();
			text.append("Sent Items");
			text.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
					text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			item2.setTitle(text);
		} else {
			if (Utils.getFromPreference(mContext, Constants.OFFLINE_MODE)
					.equalsIgnoreCase("true")) {
				inflater.inflate(R.menu.offlinebranch, menu);
				SpannableStringBuilder text = new SpannableStringBuilder();
				text.append(getString(R.string.yes));
				text.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
						text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				MenuItem item1 = menu.findItem(R.id.offline_yes);
				item1.setTitle(text);

				MenuItem item2 = menu.findItem(R.id.offline_no);
				text = new SpannableStringBuilder();
				text.append(getString(R.string.no));
				text.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
						text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				item2.setTitle(text);

			}
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if (!isMenuItemSelected) {
			if (null != mCardScrollView)
				mCardScrollView.setVisibility(View.VISIBLE);
			if (null != dots_holder)
				dots_holder.setVisibility(View.VISIBLE);
			RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
			if (null != tv_userName)
				tv_userName.setVisibility(View.VISIBLE);
		}
		isMenuItemSelected = false;
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		isMenuItemSelected = true;
		// Handle item selection. Menu items typically start another
		// activity, start a service, or broadcast another intent.
		switch (item.getItemId()) {
		case R.id.offline_yes:

			setupPager();
			return true;
		case R.id.offline_no:

			finish();
			return true;
		case R.id.inbox_menu_items:
			Intent inboxIntent = new Intent(MenuActivity.this,
					MailboxActivity.class);
			inboxIntent.putExtra("activity", "inbox");
			startActivity(inboxIntent);
			return true;
		case R.id.sent_items_menu_items:
			Intent sentItemsIntent = new Intent(MenuActivity.this,
					MailboxActivity.class);
			sentItemsIntent.putExtra("activity", "sentItems");
			startActivity(sentItemsIntent);
			return true;
		case R.id.topdrive_prob_1:/*
								 * Intent topdrive_intent_1 = new Intent (
								 * MenuActivity.this, AddTaskActivity.class);
								 * topdrive_intent_1.putExtra("PROBLEM_NAME",
								 * "clutchProb1");
								 * startActivity(topdrive_intent_1);
								 */
			return true;

		case R.id.topdrive_prob_2:
			/*
			 * Intent topdrive_intent_2 = new Intent ( MenuActivity.this,
			 * AddTaskActivity.class);
			 * topdrive_intent_2.putExtra("PROBLEM_NAME", "clutchProb2");
			 * startActivity(topdrive_intent_2);
			 */
			return true;

		case R.id.torq_prob_1:
			/*
			 * Intent torq_intent_1 = new Intent ( MenuActivity.this,
			 * AddTaskActivity.class); torq_intent_1.putExtra("PROBLEM_NAME",
			 * "fridgeProb1"); startActivity(torq_intent_1);
			 */
			return true;

		case R.id.torq_prob_2:
			/*
			 * Intent torq_intent_2 = new Intent ( MenuActivity.this,
			 * AddTaskActivity.class); torq_intent_2.putExtra("PROBLEM_NAME",
			 * "fridgeProb2"); startActivity(torq_intent_2);
			 */
			return true;
		default:
			return true;
		}
	}

	protected void logout() {

		ControllerRequest cRequest = new ControllerRequest(MenuActivity.this,
				GRegistrationController.ACTION_LOGOUT);
		cRequest.setCallbackListener(new CallBackListener() {

			@Override
			public void onSuccess(ControllerResponse response) {
				pBarMenu.setVisibility(View.GONE);
				RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
				tv_userName.setText("");
				GLog.d("TCS",
						"************ LOGOUT IN TASK LIST CALLED ************* ");
				Utils.clearSharedPref(MenuActivity.this);
				Utils.putToPreference(mContext, Constants.LOGIN_STATUS, "false");
				Toast.makeText(mContext, "Logged out successfully",
						Toast.LENGTH_SHORT).show();
				currentPage = 0;
				key = null;
				Utils.saveUrl(mContext, url);
				registerApp(response);
			
			}

			@Override
			public void onStart() {
				pBarMenu.setVisibility(View.VISIBLE);
			}

			@Override
			public void onError(ControllerResponse response) {
				pBarMenu.setVisibility(View.GONE);
				

				if (response.getStatus() == Controller.STATUS_USER_NOT_PRIVILAGED) {
					Utils.clearSharedPref(MenuActivity.this);
					Intent mainPage = new Intent(MenuActivity.this,
							MenuActivity.class);
					mainPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					mainPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					finish();
					startActivity(mainPage);
				} else {
					Toast.makeText(MenuActivity.this,
							"Unable to logout. " + response.getErrorMessage(),
							Toast.LENGTH_LONG).show();
				}
			}

		});
		Controller.executeAsync(cRequest, GRegistrationController.class);
	}

	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}

	public void checkForUpdates() {
		gpsIntent = new Intent(mContext, GPSService.class);
		startService(gpsIntent);
		try {
			String current_app_version = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			ControllerRequest request = new ControllerRequest(this,
					GRegistrationController.ACTION_VERSION_CHECK,
					new Object[] { current_app_version });
			request.setCallbackListener(versionListener);
			Controller.executeAsync(request, GRegistrationController.class);

		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

	}

	private CallBackListener versionListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			hideProgress();
			GVersionCheckResponseBean versionResponse = (GVersionCheckResponseBean) response
					.getResult();
			String message = versionResponse.getMode();
			apkName = versionResponse.getName();
			if ((message != null)) {

				if ((message.equalsIgnoreCase("major"))
						|| (message.equalsIgnoreCase("minor"))) {
					setupPager();
					/*
					 * Toast.makeText(getApplicationContext(),
					 * "There is a new apk in portal. Please download",
					 * Toast.LENGTH_SHORT).show();
					 */
				}
				// openOptionsMenu();

			} else {
				setupPager();
			}
		}

		public void onStart() {
			showProgress();
		};

		public void onError(ControllerResponse response) {
			hideProgress();
			setupPager();
		};
	};

	private void startRegistrationProcess() {
		Controller.executeAsync(regRequest, GRegistrationController.class);
	}

	private void startAuthenticationProcess() {

		Controller.executeAsync(authRequest, GRegistrationController.class);
	}

	/**
	 * Broadcast via Faye Response after 4 letter/digit Glass Registration
	 * **/
	private BroadcastReceiver mFayeMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			GLog.d("SEQUENCE",
					"***************** GOT FAYE RESPONSE ************** ");
			// Get extra data included in the Intent
			if (null != intent) {
				if (Keywords.SUCCESS == intent.getStringExtra(Keywords.DATA)) {

					startAuthenticationProcess();
				} else {
					// Do SOmething else
				}
			}
			// Get extra data included in the Intent
			try {
				if (mFayeMessageReceiver != null) {
					LocalBroadcastManager.getInstance(getApplicationContext())
							.unregisterReceiver(mFayeMessageReceiver);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private CallBackListener regListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			hideProgress();
			tBean = (GTokenResponseBean) response.getResult();
			tvAuthKey.setVisibility(TextView.VISIBLE);
			if (!TextUtils.isEmpty(tBean.getKey())) {
				key = tBean.getKey();
				tvAuthKey.setText(tBean.getKey());
				GLog.d("SEQUENCE",
						"*************** Reg : setText******************* ");
			} else {
				tvAuthKey.setVisibility(TextView.GONE);
				tvHelpLink.setVisibility(TextView.GONE);
				tvAssistUser.setText("Key not found");
			}

		}

		public void onStart() {
			showProgress();
		};

		public void onError(ControllerResponse response) {
			hideProgress();
			tvAuthKey.setVisibility(TextView.GONE);
			tvHelpLink.setVisibility(TextView.GONE);
			if (TextUtils.isEmpty(response.getErrorMessage())) {
				tvAssistUser.setText(" Some error occured");
			} else {
				tvAssistUser.setText(response.getErrorMessage());
			}

		};
	};

	private CallBackListener authListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

			hideProgress();
			Utils.putToPreference(getApplicationContext(),
					Constants.LOGIN_STATUS, "true");
			RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
			String userName = Utils.getFromPreference(mContext,
					Keywords.USER_NAME);
			tv_userName.setText("Welcome " + userName);

			checkForUpdates();
		}

		public void onStart() {
			showProgress();
		};

		public void onError(ControllerResponse response) {
			registerApp(response);
		}

	};

	private void showProgress() {
		if (pBar != null)
			pBar.setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		if (pBar != null)
			pBar.setVisibility(View.GONE);
	}

	private void setupApp() {

		mainPager.setVisibility(View.GONE);
		deviceRegLayout.setVisibility(View.VISIBLE);
		tvAuthKey = (RobotoTextView) findViewById(R.id.response_txt);
		tvHelpLink = (RobotoTextView) findViewById(R.id.link_txt);
		tvAssistUser = (RobotoTextView) findViewById(R.id.rtvHelpLine1);

		if (Constants.BASE_URL_DEMO.equals(Utils.getSavedUrl(mContext)))
			tvHelpLink.setText(getString(R.string.glance_demo_link));
		else
			tvHelpLink.setText(getString(R.string.glance_test_link));

		pBar = (ProgressBar) findViewById(R.id.pBar);

		GLog.d("SEQUENCE",
				"*************** Main  onActivity onCreate ******************* ");

		if (Keywords.SUCCESS.equalsIgnoreCase(Utils.getFromPreference(
				getApplicationContext(), Keywords.GLASS_REGISTERED))) {
			
			RobotoTextView tv_userName = (RobotoTextView) findViewById(R.id.tv_user_name);
			String userName = Utils.getFromPreference(mContext,
					Keywords.USER_NAME);
			tv_userName.setText("Welcome " + userName);
			checkForUpdates();

		} else {
			GLog.d("SEQUENCE",
					"*************** StartAuthentication ******************* ");
			startAuthenticationProcess();
		}
	}

	private void registerApp(ControllerResponse response) {

		mainPager.setVisibility(View.GONE);
		deviceRegLayout.setVisibility(View.VISIBLE);
		
		if (Constants.BASE_URL_DEMO.equals(Utils.getSavedUrl(mContext)))
			tvHelpLink.setText(getString(R.string.glance_demo_link));
		else
			tvHelpLink.setText(getString(R.string.glance_test_link));

		tvAuthKey.setVisibility(View.VISIBLE);
		tvHelpLink.setVisibility(View.VISIBLE);
		tvAssistUser.setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.iv_glance_logo))
				.setVisibility(View.GONE);

		GLog.d("SEQUENCE",
				"*************** Authentication OnError******************* ");
		hideProgress();

		if (null != key && !TextUtils.isEmpty(key) && key.length() == 4) {
			if (!TextUtils.isEmpty(response.getErrorMessage()))
				Toast.makeText(getApplicationContext(),
						response.getErrorMessage(), Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(),
						"Unable to register the Glass", Toast.LENGTH_LONG)
						.show();

		} else {
			GLog.d("SEQUENCE", "*************** StartReg******************* ");
			startRegistrationProcess();
		}

	}
}
