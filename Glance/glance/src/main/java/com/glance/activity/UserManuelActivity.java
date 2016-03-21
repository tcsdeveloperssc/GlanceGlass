package com.glance.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.SubNodeAdapter;
import com.glance.bean.model.GAnnotatePoint;
import com.glance.bean.model.HotSpots;
import com.glance.bean.model.MainNode;
import com.glance.bean.model.SubNodes;
import com.glance.bean.response.GAnnotationResponse;
import com.glance.controller.GStoryController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.controller.core.StreamListener;
import com.glance.faye.AnnotationService;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.ArtiFactImageTask;
import com.glance.utils.Constants;
import com.glance.utils.Constants.FayeAlert;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.glance.view.ImageScreen;
import com.glance.view.RobotoTextView;
import com.glance.view.VideoScreen;
import com.google.android.glass.widget.CardScrollView;

public class UserManuelActivity extends BaseActivity {

	private ArrayList<SubNodes> subNodeList = new ArrayList<SubNodes>();
	private ArrayList<SubNodes> mainSubNodeList = new ArrayList<SubNodes>();
	public static ArrayList<MainNode> listCriteriaSatisfiedNodes = new ArrayList<MainNode>();
	private TextView error_txt;
	private LinearLayout mStoryLayout;
	private CardScrollView mCardScrollView;
	private SubNodeAdapter mAdapter;

	private static MediaPlayer mediaPlayer;
	private int commandIndex;
	private ArrayList<HotSpots> hotSpotList = new ArrayList<HotSpots>();
	// private ArrayList<HotSpotStatus> hotSpotStatus = new
	// ArrayList<HotSpotStatus>();

	public final static int MAIN_PAGER = 0;
	public final static int LINK_PAGER = 1;
	private int currentPageInMain = 0;
	private int currentPageInLink = 0;
	public boolean isAudioPlayingStarted = false;

	public static boolean isImageLoaded = false;
	private String url;

	private String streamType;
	private ArrayList<String> subnodeKeys = new ArrayList<String>();
	public static boolean inLinkNodeActivity = false;
	private String currentSubNodeKey = null;
	private boolean fromSpeechRecognizer = false;

	private ArtiFactImageTask artTask;
	public String story_Id;
	private String prev_node;
	public Intent cameraIntent;
	public boolean btakePicture = false;
	private String messageShown = "Options : Show HotSpot No / Take A Picture / Completed";
	private Intent annotateIntent;
	private ProgressBar pBar;
	private String currentHspot = null;

	public BroadcastReceiver mPointsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// Get extra data included in the Intent
			if (null != intent) {
				GAnnotationResponse response = (GAnnotationResponse) intent
						.getSerializableExtra(Keywords.DATA);
//				drawAnnotation(response);
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isImageLoaded = false;
		setContentView(R.layout.story_activity);

		pBar = (ProgressBar) findViewById(R.id.pb_getstory);
		Utils.putToPreference(mContext, Keywords.STORY_STATUS, "started");
		if (!inLinkNodeActivity) {
			story_Id = getIntent().getStringExtra(Constants.STORY_ID);
			error_txt = (RobotoTextView) findViewById(R.id.error_txt);
			getStoryProcess();
		} else {
			error_txt = (RobotoTextView) findViewById(R.id.error_txt_link_node);
		}
		mContext = UserManuelActivity.this;
		// startFayeServices();
	}

	/*public void sendOnlineStatusToSocket(String status, String hotspot_id,
			String link_node, String tId, String stId, String pNode) {

		JSONObject olineJsonObj = new JSONObject();
		try {
			if (null == tId) {
				olineJsonObj.put("storyId", story_Id);
			} else {
				olineJsonObj.put("taskId", tId);
				olineJsonObj.put("storyId", stId);
			}
			olineJsonObj.put("status", status);
			olineJsonObj.put("hotspotId", hotspot_id);
			SharedPreferences pref = Utils.getCredentials(mContext);
			user_id = pref.getString(
					mContext.getString(R.string.preference_user_id), "");
			olineJsonObj.put("userId", user_id);
			if (link_node != null) {
				olineJsonObj.put("currentNode", link_node);

			} else if (pNode != null) {
				olineJsonObj.put("currentNode", pNode);
			} else {
				olineJsonObj.put("currentNode", currentSubNodeKey);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		FayeClient fayeClient = WebPortalSocketService.fayeClientportal;
		if (null != fayeClient) {
			fayeClient.sendMessage(olineJsonObj);
		}

	}*/

	public void startFayeServices() {
		annotateIntent = new Intent(this, AnnotationService.class);
		startService(annotateIntent);
	}

	public void stopFayeServices() {
		GLog.d("FAYE", "Stopping services");
		stopService(annotateIntent);
	}


	@Override
	protected void onResume() {

		super.onResume();
		startFayeServices();
		// Toast.makeText(mContext, "Tap and speak a command",
		// Toast.LENGTH_SHORT).show();
		messageShown = "Options : Show Hotspot No ";
		if (!fromSpeechRecognizer) {
			String story_status = Utils.getFromPreference(this,
					Keywords.STORY_STATUS);
			if (story_status.equalsIgnoreCase("completed")) {
				UserManuelActivity.this.finish();
			}
		} else {
			fromSpeechRecognizer = false;
		}

		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(mPointsReceiver,
						new IntentFilter(FayeAlert.POINTS_ALERT));
		if (isImageLoaded == true) {
			/*if (currentHspot == null)
				sendOnlineStatusToSocket("enablePaint", "nil", null, null,
						null, null);
			else
				sendOnlineStatusToSocket("enablePaint", currentHspot, null,
						null, null, null);*/
		}
	}

	@Override
	protected void onPause() {
		stopFayeServices();

		GLog.d("SPEECH_RECEIVER", "Calling base activity on pause : HotSpot");
		killMediaPlayer();
		String story_status = Utils.getFromPreference(this,
				Keywords.STORY_STATUS);
		if (story_status.equalsIgnoreCase("completed") == false) {
			/*if (currentHspot == null)
				sendOnlineStatusToSocket("disablePaint", "nil", null, null,
						null, null);
			else
				sendOnlineStatusToSocket("disablePaint", currentHspot, null,
						null, null, null);*/
		}

		super.onPause();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mPointsReceiver);

	}

	private void getStoryProcess() {

		ControllerRequest cRequest = new ControllerRequest(mContext,
				GStoryController.ACTION_GET_STORY, new Object[] { story_Id });
		cRequest.setCallbackListener(storyListener);

		Controller.executeAsync(cRequest, GStoryController.class);
	}

	private CallBackListener storyListener = new CallBackListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ControllerResponse response) {

			dismissDialog();
			if (pBar != null)
				pBar.setVisibility(View.GONE);
			listCriteriaSatisfiedNodes = (ArrayList<MainNode>) response
					.getResult();

			if (null != listCriteriaSatisfiedNodes
					&& listCriteriaSatisfiedNodes.size() > 0) {
				subNodeList = listCriteriaSatisfiedNodes.get(0).getSubNodes();
				mainSubNodeList = subNodeList;

				// initializeHotSpotStatus(subNodeList);

				/******************************************************************************/
				if (subNodeList != null) {
					for (int i = 0; i < subNodeList.size(); i++) {
						subnodeKeys.add((subNodeList.get(i)).getsubNodeKey());
						GLog.d("SUBNODE KEYS",
								(subNodeList.get(i)).getsubNodeKey());
					}
				}
				/******************************************************************************/

				if (null != subNodeList && !subNodeList.isEmpty()
						&& subNodeList.size() > 0) {
					GLog.d("SPEECH_SERVICE", "Calling initialize ---1");
					initialize(subNodeList, currentPageInMain);
					setupLayout(subNodeList, MAIN_PAGER);

				} else {

					error_txt
							.setText(getString(R.string.stories_not_available));
					error_txt.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(),
							getString(R.string.stories_not_available),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				error_txt.setText(getString(R.string.stories_not_available));
				error_txt.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(),
						getString(R.string.stories_not_available),
						Toast.LENGTH_SHORT).show();
				finish();
			}

		}

		public void onStart() {
			showCustomisedDialog("loading");
			error_txt.setVisibility(View.GONE);
		};

		public void onError(ControllerResponse response) {
			dismissDialog();
			if (pBar != null)
				pBar.setVisibility(View.GONE);
			/*Toast.makeText(getApplicationContext(),
					response.getErrorMessage()
											 * getString(R.string.
											 * stories_not_available)
											 , Toast.LENGTH_SHORT).show();*/
			error_txt.setText(getString(R.string.stories_not_available));
			error_txt.setVisibility(View.GONE);
			finish();
			GLog.d("TCS",
					"*************** on error occured  ****************** ");

		};
	};

	public void initialize(ArrayList<SubNodes> subNodeArray, int currentPage) {

		GLog.d("SPEECH_SERVICE", "Inside initialize -- currentPage = "
				+ currentPage);

		currentSubNodeKey = subNodeArray.get(currentPage).getsubNodeKey();
		hotSpotList = subNodeArray.get(currentPage).gethotspots();

	}

	public void setupLayout(final ArrayList<SubNodes> subNodeArray,
			final int flag) {

		final LinearLayout dots_scrollbar_holder;

		mAdapter = new SubNodeAdapter(getApplicationContext(), subNodeArray, imageLoadListener);

		if (flag == MAIN_PAGER) {
			mStoryLayout = (LinearLayout) findViewById(R.id.story_card_layout);
			dots_scrollbar_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder));

		} else {
			mStoryLayout = (LinearLayout) findViewById(R.id.link_story_card_layout);
			dots_scrollbar_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder_link_node));

		}

		mCardScrollView = new CardScrollView(UserManuelActivity.this);
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.setOnItemClickListener(new CardClickListener());
		mCardScrollView.setOnItemSelectedListener(new CardSelectedListener(
				subNodeArray, flag, dots_scrollbar_holder));
		mCardScrollView.activate();
		mStoryLayout.addView(mCardScrollView);
		if (null != error_txt)
			error_txt.setVisibility(View.GONE);

		updateIndicator(mAdapter, dots_scrollbar_holder, 0);

	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {
			displaySpeechRecognizer();
		}
	}
	
	
	public class CardSelectedListener implements OnItemSelectedListener {
		private ArrayList<SubNodes> subNodeArray;
		private int flag;
		private LinearLayout dots_scrollbar_holder;

		public CardSelectedListener(final ArrayList<SubNodes> subNodeArray,
				final int flag, LinearLayout dots_scrollbar_holder) {
			this.subNodeArray = subNodeArray;
			this.flag = flag;
			this.dots_scrollbar_holder = dots_scrollbar_holder;
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {

			if (flag == MAIN_PAGER) {
				currentPageInMain = pos;
				initialize(subNodeArray, currentPageInMain);
				prev_node = subNodeArray.get(pos).getsubNodeKey();
				/*sendOnlineStatusToSocket("hotspotrunning", "nil", null, null,
						null, null);*/
			} else {

				currentPageInLink = pos;
				initialize(subNodeArray, currentPageInLink);
				/*sendOnlineStatusToSocket("hotspotrunning", "nil", null, null,
						null, null);*/
				prev_node = subNodeArray.get(pos).getsubNodeKey();
			}
			updateIndicator(mAdapter, dots_scrollbar_holder, pos);
			GLog.d("SPEECH_SERVICE", "Calling initialize ---2");

			killMediaPlayer();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	}

	public void updateIndicator(SubNodeAdapter mAdapter,
			LinearLayout dots_scrollbar_holder, int currentPage) {

		dots_scrollbar_holder.removeAllViews();
		DotsScrollBar.createDotScrollBar(mContext, dots_scrollbar_holder,
				currentPage, mAdapter.getCount());
	}

	public void hotspotClickSpeechEvent(HotSpots hspot) {
		if ((hspot == null) || (hspot.getlinks() == null)) {
			Toast.makeText(mContext, "Nothing to display in the hotspot",
					Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			GLog.d("TCS",
					"************** ON CLICK OF HOTSPOT ******************** ");
			System.out.println(hspot.getlinks());
			JSONArray link_array = new JSONArray(hspot.getlinks());

			for (int i = 0; i < link_array.length(); i++) {

				streamType = link_array.getJSONObject(i).getString("type");
				// streamType = "link";
				if (streamType.equalsIgnoreCase("video")) {
					// updateStatus(hspot,"start",null);
					/*sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							null, null, null, null);*/
					

					Intent videoIntent = new Intent(mContext, VideoScreen.class);
					videoIntent.putExtra("id", link_array.getJSONObject(i)
							.getString("artifactId"));
					startActivity(videoIntent);
					

				} else if (streamType.equalsIgnoreCase("audio")) {
					// updateStatus(hspot,"start");
					/*sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							null, null, null, null);*/
					artTask = new ArtiFactImageTask(mContext, "audio",
							audioListener);

					artTask.execute(link_array.getJSONObject(i).getString(
							"artifactId"));

				} else if (streamType.equalsIgnoreCase("image")) {
					/*sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							null, null, null, null);*/
					Intent imageIntent = new Intent(mContext, ImageScreen.class);
					imageIntent.putExtra("artifactId", link_array
							.getJSONObject(i).getString("artifactId"));// "http://glance.tcsmobilitycloud.com/Glance/fileServer?"

					mContext.startActivity(imageIntent);

					//

				} else if (streamType.equalsIgnoreCase("link")) {

					String link_node = link_array.getJSONObject(i).getString(
							"action");
					/*sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							link_node, null, null, null);*/
					// updateStatus(hspot,"start");
					// String link_node = "1-0";
					GLog.d("GLANCE", "calling navigateToLink" + link_node);
					// Toast.makeText(mContext,
					// "calling navigateToLink"+link_node,
					// Toast.LENGTH_SHORT).show();
					navigateToLink(link_node);
				} else {
					Toast.makeText(mContext,
							"Nothing to display in the hotspot",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (JSONException e) {
			Toast.makeText(mContext, "Nothing to display in the hotspot",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void navigateToLink(String link_node) {

		GLog.d("GLANCE", "inside navigateToLink" + link_node);
		SubNodes linkSubnode = null;

		if (subnodeKeys != null) {
			for (int i = 0; i < subnodeKeys.size(); i++) {
				if ((subnodeKeys.get(i)).equalsIgnoreCase(link_node)) {
					// Toast.makeText(mContext,
					// "SubNode link associated with this hotspot is VALID",
					// Toast.LENGTH_SHORT).show();
					linkSubnode = mainSubNodeList.get(i);
					break;
				}
			}
		}

		if (linkSubnode == null) {
			Toast.makeText(mContext,
					"SubNode link associated with this hotspot is invalid",
					Toast.LENGTH_SHORT).show();
		} else {
			ArrayList<SubNodes> subNodeArray = new ArrayList<SubNodes>();
			subNodeArray.add(linkSubnode);
			currentPageInLink = 0;
			// setContentView(R.layout.link_node_activity);
			// RelativeLayout relativeLayout =
			// (RelativeLayout)getLayoutInflater().inflate(R.layout.link_node_activity,
			// null);
			/*
			 * FrameLayout frameLayout =
			 * (FrameLayout)findViewById(R.id.pager_holder);
			 * frameLayout.inflate(mContext, R.layout.link_node_activity, null);
			 */

			Intent linkNodeIntent = new Intent(mContext, LinkNodeActivity.class);
			linkNodeIntent.putParcelableArrayListExtra("subNodeArray",
					subNodeArray);

			linkNodeIntent.putExtra("prev_node", prev_node);
			linkNodeIntent.putExtra("story_id", story_Id);
			startActivity(linkNodeIntent);

			/*
			 * initialize(subNodeArray,currentPageInLink);
			 * setupLayout(subNodeArray,LINK_PAGER);
			 */

		}

	}

	public int getCurrentPageInLink() {
		return currentPageInLink;
	}

	public void setCurrentPageInLink(int currentPageInLink) {
		this.currentPageInLink = currentPageInLink;
	}

	private StreamListener audioListener = new StreamListener() {

		@Override
		public void onSuccess(String imageName) {
			if (pBar != null)
				pBar.setVisibility(View.GONE);
			try {
				url = Constants.get_File_Service + getStreamName();
				playAudio();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onFinish() {
		}
	};

	private StreamListener videoListener = new StreamListener() {

		@Override
		public void onSuccess(String imageName) {
			if (pBar != null)
				pBar.setVisibility(View.GONE);
		}

		@Override
		public void onFinish() {

		}
	};

	private void playAudio() throws Exception {
		killMediaPlayer();

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(url);
		mediaPlayer.prepare();
		isAudioPlayingStarted = true;
		mediaPlayer.start();
	}

	protected void killMediaPlayer() {
		if (isAudioPlayingStarted) {
			if (mediaPlayer != null) {
				try {
					GLog.d("leapkh", "killMediaPlayer");
					isAudioPlayingStarted = false;
					mediaPlayer.release();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void getSpeechResults(ArrayList<String> strlist) {
		GLog.d("SPEECH_SERVICE***", "Inside getSpeechResults : HotSpot");

		commandIndex = Utils.keyFinder(strlist);
		if (hotSpotList != null){

		if ((Constants.SHOW_HOTSPOT_ONE == commandIndex)
				|| (Constants.SHOW_1 == commandIndex)) {

			if (hotSpotList.size() > 0 && null != hotSpotList.get(0)) {
				currentHspot = hotSpotList.get(0).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(0));
			}

		} else if (Constants.SHOW_HOTSPOT_TWO == commandIndex
				|| Constants.SHOW_2 == commandIndex) {

			if (hotSpotList.size() > 1 && null != hotSpotList.get(1)) {
				currentHspot = hotSpotList.get(1).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(1));
			}
		} else if ((Constants.SHOW_HOTSPOT_THREE == commandIndex)
				|| (Constants.SHOW_3 == commandIndex)) {

			if (hotSpotList.size() > 2 && null != hotSpotList.get(2)) {
				currentHspot = hotSpotList.get(2).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(2));
			}

		} else if ((Constants.SHOW_HOTSPOT_FOUR == commandIndex)
				|| (Constants.SHOW_4 == commandIndex)) {

			if (hotSpotList.size() > 3 && null != hotSpotList.get(3)) {
				currentHspot = hotSpotList.get(3).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(3));
			}
		} else if ((Constants.SHOW_HOTSPOT_FIVE == commandIndex)
				|| (Constants.SHOW_5 == commandIndex)) {

			if (hotSpotList.size() > 4 && null != hotSpotList.get(4)) {
				currentHspot = hotSpotList.get(4).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(4));
			}
		}
		}

		

	}

	
	@Override
	protected void onStop() {
		Log.d("TCS", "***** onStop in HOTSPOT ACTIVITY");
		super.onStop();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

			if (event.getAction() == KeyEvent.ACTION_UP) {
				
					displaySpeechRecognizer();
				return true;
			}
		}

		if (keyCode == 4) {
			// Toast.makeText(mContext, "on Swipe Down",
			// Toast.LENGTH_SHORT).show();
			/*sendOnlineStatusToSocket("hotspotnotrunning", "nil", null, null,
					null, null);*/
			if (isAudioPlayingStarted)
				killMediaPlayer();
			else
				finish();
			return true;
		}
		return false;
	}

	private static final int SPEECH_REQUEST = 0;

	protected void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, messageShown);
		fromSpeechRecognizer = true;
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				getSpeechResults((ArrayList<String>) results);
			
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	
	public String getCurrentHspot() {
		return currentHspot;
	}

	public void setCurrentHspot(String currentHspot) {
		this.currentHspot = currentHspot;
	}

	
	
	@Override
	public void getUserTasks() {

	}

	@Override
	public void getMails() {

	}

	public void drawAnnotation(GAnnotationResponse response) {
		
		if (null != response) {
			SharedPreferences pref = Utils.getCredentials(mContext);
			user_id = pref.getString(
					mContext.getString(R.string.preference_user_id), "");
			if (null == response.getUserId()){
				return;
			}
			else{
				if (!(response.getUserId().equalsIgnoreCase(user_id))) {
					return;
				}
			}

		}

		ArrayList<String> pointList = response.getPointList();
		ArrayList<GAnnotatePoint> changedList = new ArrayList<GAnnotatePoint>();
		for (String point : pointList) {
			changedList.add(new GAnnotatePoint(point));
		}
		
		LinearLayout ll = null;

		ll = (LinearLayout) findViewById(R.id.story_card_layout);
		if (ll == null)
			ll = (LinearLayout) findViewById(R.id.link_story_card_layout);

		if (ll != null) {
			CardScrollView c = (CardScrollView) ll.getChildAt(0);
			ImageView imageView = (ImageView) c.getSelectedView().findViewById(
					R.id.hot_img);

			if (((BitmapDrawable) imageView.getDrawable()) == null) {
				return;
			}
			Bitmap image = ((BitmapDrawable) imageView.getDrawable())
					.getBitmap();

			image = image.copy(image.getConfig(), true);
			Canvas canvas = new Canvas(image);
			Paint paint = new Paint();
			// paint.setColor(Integer.parseInt(response.getColor()));
			try {
				paint.setColor(Color.parseColor(response.getColor()));
			} catch (IllegalArgumentException e) {
				paint.setColor(Color.RED);
			}
			paint.setStrokeWidth(Integer.parseInt(response.getToolsize()));
			canvas.drawBitmap(image, new Matrix(), null);

			GAnnotatePoint first, second;
			first = changedList.get(0);
			for (int i = 0; i < changedList.size(); i++) {

				if ((i + 1) < changedList.size()) {
					second = changedList.get(i + 1);

					canvas.drawLine(first.getX(), first.getY(), second.getX(),
							second.getY(), paint);
					first = second;
				}

			}

			imageView.setImageBitmap(image);

		}

	}
	private CallBackListener imageLoadListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
/*
			if (currentHspot == null)
				sendOnlineStatusToSocket("enablePaint", "nil", null, null,
						null, null);
			else
				sendOnlineStatusToSocket("enablePaint", currentHspot, null,
						null, null, null);*/
		}
	};

	
}
