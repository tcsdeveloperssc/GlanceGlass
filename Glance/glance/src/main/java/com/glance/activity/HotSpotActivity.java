package com.glance.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.glass.content.Intents;

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
import android.os.FileObserver;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.SubNodeAdapter;
import com.glance.bean.model.GAnnotatePoint;
import com.glance.bean.model.HotSpots;
import com.glance.bean.model.MainNode;
import com.glance.bean.model.NodeList;
import com.glance.bean.model.SubNodes;
import com.glance.bean.response.GAnnotationResponse;
import com.glance.controller.GMultimediaUploadController;
import com.glance.controller.GStoryController;
import com.glance.controller.GUserTasksController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.controller.core.StreamListener;
import com.glance.faye.AnnotationService;
import com.glance.faye.WebPortalSocketService;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.ArtiFactImageTask;
import com.glance.utils.Constants;
import com.glance.utils.Constants.FayeAlert;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.RecordAudio;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.glance.view.ImageScreen;
import com.glance.view.RobotoTextView;
import com.glance.view.VideoScreen;
import com.google.android.glass.widget.CardScrollView;
import com.saulpower.fayeclient.FayeClient;

public class HotSpotActivity extends BaseActivity {

	private ArrayList<SubNodes> subNodeList = new ArrayList<SubNodes>();
	private ArrayList<SubNodes> mainSubNodeList = new ArrayList<SubNodes>();
	private NodeList mNodeList;
	public static ArrayList<MainNode> listCriteriaSatisfiedNodes = new ArrayList<MainNode>();
	private TextView error_txt;
	private LinearLayout mStoryLayout;
	private CardScrollView mCardScrollView;
	private SubNodeAdapter mAdapter;
	private static MediaPlayer mediaPlayer;
	private int commandIndex;
	private ArrayList<HotSpots> hotSpotList = new ArrayList<HotSpots>();
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
	private ArtiFactImageTask artTask;
	public String story_Id, task_Id;
	private String prev_node;
	public Intent cameraIntent;
	private Intent annotateIntent;
	private ProgressBar pBar;
	private String currentHspot = null;
	private boolean isCheckListDone = true;
	private String messageShown = "Options : Show HotSpot No / Take A Picture / Completed";
	private ArrayList<String> uncheckedSubnodeKeys = new ArrayList<String>();
	private ArrayList<String> currentScreenStatus = new ArrayList<String>();
	private boolean isBackPressed = false;
	private static final int TAKE_VIDEO_REQUEST = 1;
	private static final int TAKE_AUDIO_REQUEST = 2;
	private static final int GET_ROOT_CAUSE_REQUEST = 3;
	private static final int TAKE_PICTURE_REQUEST = 4;
	private ArrayList<String> rootCauseList = null;

	public BroadcastReceiver mPointsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (null != intent) {
				GAnnotationResponse response = (GAnnotationResponse) intent
						.getSerializableExtra(Keywords.DATA);
				drawAnnotation(response);
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
		Utils.putToPreference(mContext, Constants.NEW_MAIL, Constants.NO);
		if (!inLinkNodeActivity) {
			story_Id = getIntent().getStringExtra(Constants.STORY_ID);
			task_Id = getIntent().getStringExtra(Constants.TASK_ID);
			error_txt = (RobotoTextView) findViewById(R.id.error_txt);
			getStoryProcess();
		} else {
			error_txt = (RobotoTextView) findViewById(R.id.error_txt_link_node);
		}
		mContext = HotSpotActivity.this;

	}

	public void sendOnlineStatusToSocket(String status, String hotspot_id,
			String link_node, String tId, String stId, String pNode) {

		JSONObject olineJsonObj = new JSONObject();
		try {
			if (null == tId) {
				olineJsonObj.put("taskId", task_Id);
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

	}

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
		String story_status = Utils.getFromPreference(this,
				Keywords.STORY_STATUS);
		if ((!story_status.equalsIgnoreCase("completed"))) {
			ImageView iv = (ImageView) (findViewById(R.id.iv_checkList));
			if (isCheckListDone) {
				if (iv != null) {
					iv.setVisibility(View.GONE);
				}
				messageShown = "Options : Show Hotspot No / Completed / Take a Picture";
			} else {
				if (iv != null) {
					iv.setVisibility(View.VISIBLE);
				}
				messageShown = "Options : Show Hotspot No / Completed / Take a Picture / Check";
			}

			LocalBroadcastManager.getInstance(getApplicationContext())
					.registerReceiver(mPointsReceiver,
							new IntentFilter(FayeAlert.POINTS_ALERT));

			if (currentHspot == null)
				sendOnlineStatusToSocket("enablePaint", "nil", null, null,
						null, null);
			else
				sendOnlineStatusToSocket("enablePaint", currentHspot, null,
						null, null, null);
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
			if (isBackPressed) {
				sendOnlineStatusToSocket("hotspotnotrunning", "nil", null,
						null, null, null);
			} else {
				if (currentHspot == null)
					sendOnlineStatusToSocket("disablePaint", "nil", null, null,
							null, null);
				else
					sendOnlineStatusToSocket("disablePaint", currentHspot,
							null, null, null, null);
			}
		}

		super.onPause();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mPointsReceiver);

	}

	private void getStoryProcess() {
		String action = getIntent().getStringExtra(Constants.ACTION);
		ControllerRequest cRequest = null;
		if (action != null) {
			if (action.equalsIgnoreCase(Constants.SELF_HELP)) {
				Utils.putToPreference(mContext, Constants.NEW_MAIL,
						Constants.YES);
				cRequest = new ControllerRequest(mContext,
						GStoryController.ACTION_GET_TAGGED_STORY, new Object[] {
								task_Id, story_Id });
			} else {
				cRequest = new ControllerRequest(mContext,
						GStoryController.ACTION_GET_STORY,
						new Object[] { story_Id });
			}
		} else {
			cRequest = new ControllerRequest(mContext,
					GStoryController.ACTION_GET_STORY,
					new Object[] { story_Id });
		}
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
			mNodeList = (NodeList) response.getResult();
			/*listCriteriaSatisfiedNodes = (ArrayList<MainNode>) response
					.getResult();*/
			listCriteriaSatisfiedNodes =  mNodeList.getMainNodeList();
			rootCauseList = mNodeList.getRootCause();

			if (null != listCriteriaSatisfiedNodes
					&& listCriteriaSatisfiedNodes.size() > 0) {
				subNodeList = listCriteriaSatisfiedNodes.get(0).getSubNodes();
				mainSubNodeList = subNodeList;
				
				

				/******************************************************************************/
				if (subNodeList != null) {
					for (int i = 0; i < subNodeList.size(); i++) {
						subnodeKeys.add((subNodeList.get(i)).getsubNodeKey());
						if (!subNodeList.get(i).isCheckListDone()) {
							uncheckedSubnodeKeys.add((subNodeList.get(i))
									.getsubNodeKey());
						}
					}
				}

				ArrayList<String> checkedSubNodeKeyList = getIntent()
						.getStringArrayListExtra(Constants.SUBNODE_KEYS);

				if (subNodeList != null && checkedSubNodeKeyList != null) {
					for (int i = 0; i < subNodeList.size(); i++) {
						if (checkedSubNodeKeyList.contains((subNodeList.get(i))
								.getsubNodeKey())) {
							subNodeList.get(i).setCheckListDone(true);
						}

					}
				}

				if (checkedSubNodeKeyList != null) {
					for (String s : checkedSubNodeKeyList) {
						uncheckedSubnodeKeys.remove(s);
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
			/*
			 * Toast.makeText(getApplicationContext(),
			 * getString(R.string.stories_not_available),
			 * Toast.LENGTH_SHORT).show();
			 */
			if (null != mCardScrollView) {
				mCardScrollView.removeAllViews();
			}
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
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

		mAdapter = new SubNodeAdapter(getApplicationContext(), subNodeArray,
				imageLoadListener);

		if (flag == MAIN_PAGER) {
			mStoryLayout = (LinearLayout) findViewById(R.id.story_card_layout);
			dots_scrollbar_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder));

		} else {
			mStoryLayout = (LinearLayout) findViewById(R.id.link_story_card_layout);
			dots_scrollbar_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder_link_node));

		}

		mCardScrollView = new CardScrollView(HotSpotActivity.this);
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.setOnItemClickListener(new CardClickListener());
		mCardScrollView.setOnItemSelectedListener(new CardSelectedListener(
				subNodeArray, flag, dots_scrollbar_holder));
		mCardScrollView.activate();
		// mCardScrollView.deactivate();
		mStoryLayout.addView(mCardScrollView);
		if (null != error_txt)
			error_txt.setVisibility(View.GONE);
		updateIndicator(mAdapter, dots_scrollbar_holder, 0);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
			isBackPressed = true;

			if (isAudioPlayingStarted)
				killMediaPlayer();
			else
				finish();
			return true;
		}
		return false;
	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {
			onTap();
		}
	}

	public void onTap() {
		/*
		 * if (btakePicture) { btakePicture = false; //
		 * startActivity(cameraIntent); } else
		 */
		displaySpeechRecognizer();
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
			setCurrentHspot(null);

			if (flag == MAIN_PAGER) {
				currentPageInMain = pos;
				initialize(subNodeArray, currentPageInMain);
				prev_node = subNodeArray.get(pos).getsubNodeKey();
				sendOnlineStatusToSocket("hotspotrunning", "nil", null, null,
						null, null);
			} else {

				currentPageInLink = pos;
				initialize(subNodeArray, currentPageInLink);
				sendOnlineStatusToSocket("hotspotrunning", "nil", null, null,
						null, null);
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

		isCheckListDone = subNodeList.get(currentPage).isCheckListDone();
		ImageView iv = (ImageView) (findViewById(R.id.iv_checkList));

		if (isCheckListDone) {
			if (iv != null) {
				// iv.setImageResource(R.drawable.checklist_done);
				iv.setVisibility(View.GONE);
			}
			messageShown = "Options : Show Hotspot No / Completed / Take a Picture";
			GLog.d("SWIPE", "isCheckListDone True");
		} else {
			if (iv != null)
				iv.setVisibility(View.VISIBLE);
			messageShown = "Options : Show Hotspot No / Completed / Take a Picture / Check";
		}
		dots_scrollbar_holder.removeAllViews();
		DotsScrollBar.createDotScrollBar(mContext, dots_scrollbar_holder,
				currentPage, mAdapter.getCount());
	}

	public void hotspotClickSpeechEvent(HotSpots hspot) {
		currentScreenStatus.add(currentSubNodeKey);
		currentScreenStatus.add(task_Id);
		currentScreenStatus.add(story_Id);
		currentScreenStatus.add(currentHspot);
		currentScreenStatus.add("disablePaint");
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
					sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							null, null, null, null);
					/*
					 * Uncomment this if video is streamed
					 */
					/*
					 * if(pBar != null) pBar.setVisibility(View.VISIBLE);
					 * artTask = new ArtiFactImageTask(mContext, "video",
					 * videoListener);
					 * artTask.execute(link_array.getJSONObject(i).getString(
					 * "artifactId"));
					 */

					Intent videoIntent = new Intent(mContext, VideoScreen.class);
					videoIntent.putExtra("id", link_array.getJSONObject(i)
							.getString("artifactId"));
					videoIntent.putStringArrayListExtra("currentScreenStatus",
							currentScreenStatus);

					startActivity(videoIntent);

				} else if (streamType.equalsIgnoreCase("audio")) {
					// updateStatus(hspot,"start");
					sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							null, null, null, null);
					artTask = new ArtiFactImageTask(mContext, "audio",
							audioListener);

					artTask.execute(link_array.getJSONObject(i).getString(
							"artifactId"));

				} else if (streamType.equalsIgnoreCase("image")) {
					sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							null, null, null, null);
					Intent imageIntent = new Intent(mContext, ImageScreen.class);
					imageIntent.putExtra("artifactId", link_array
							.getJSONObject(i).getString("artifactId"));
					imageIntent.putStringArrayListExtra("currentScreenStatus",
							currentScreenStatus);

					mContext.startActivity(imageIntent);

					//

				} else if (streamType.equalsIgnoreCase("link")) {

					String link_node = link_array.getJSONObject(i).getString(
							"action");
					sendOnlineStatusToSocket("hotspotrunning", hspot.getKey(),
							link_node, null, null, null);
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
			Intent linkNodeIntent = new Intent(mContext, LinkNodeActivity.class);
			linkNodeIntent.putParcelableArrayListExtra("subNodeArray",
					subNodeArray);

			linkNodeIntent.putExtra("prev_node", prev_node);
			linkNodeIntent.putExtra("task_id", task_Id);
			linkNodeIntent.putExtra("story_id", story_Id);
			startActivity(linkNodeIntent);

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

		if ((Constants.SHOW_HOTSPOT_ONE == commandIndex)
				|| (Constants.SHOW_1 == commandIndex)) {

			if (hotSpotList != null && hotSpotList.size() > 0
					&& null != hotSpotList.get(0)) {
				currentHspot = hotSpotList.get(0).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(0));
			}

		} else if (Constants.SHOW_HOTSPOT_TWO == commandIndex
				|| Constants.SHOW_2 == commandIndex) {

			if (hotSpotList != null && hotSpotList.size() > 1
					&& null != hotSpotList.get(1)) {
				currentHspot = hotSpotList.get(1).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(1));
			}
		} else if ((Constants.SHOW_HOTSPOT_THREE == commandIndex)
				|| (Constants.SHOW_3 == commandIndex)) {

			if (hotSpotList != null && hotSpotList.size() > 2
					&& null != hotSpotList.get(2)) {
				currentHspot = hotSpotList.get(2).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(2));
			}

		} else if ((Constants.SHOW_HOTSPOT_FOUR == commandIndex)
				|| (Constants.SHOW_4 == commandIndex)) {

			if (hotSpotList != null && hotSpotList.size() > 3
					&& null != hotSpotList.get(3)) {
				currentHspot = hotSpotList.get(3).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(3));
			}
		} else if ((Constants.SHOW_HOTSPOT_FIVE == commandIndex)
				|| (Constants.SHOW_5 == commandIndex)) {

			if (hotSpotList != null && hotSpotList.size() > 4
					&& null != hotSpotList.get(4)) {
				currentHspot = hotSpotList.get(4).getKey();
				hotspotClickSpeechEvent(hotSpotList.get(4));
			}
		}

		else if ((Constants.TASK_COMPLETED == commandIndex)
				|| (Constants.DONE == commandIndex)) {
			if (uncheckedSubnodeKeys.size() <= 0) {
				Utils.putToPreference(mContext, Keywords.SUBMIT_STATUS,
						Keywords.TRUE);
				openOptionsMenu();

			} else
				Toast.makeText(getApplicationContext(),
						"Please complete the checkList", Toast.LENGTH_SHORT)
						.show();

		} else if ((Constants.TAKE_A_PICTURE == commandIndex)
				|| (Constants.TAKE_PICTURE == commandIndex)) {
			Utils.putToPreference(mContext, Keywords.SUBMIT_STATUS,
					Keywords.FALSE);
			takePicture();
		} else if ((Constants.CHECK == commandIndex)
				|| (Constants.CHECKED == commandIndex)) {
			if (uncheckedSubnodeKeys.contains((subNodeList
					.get(currentPageInMain).getsubNodeKey()))) {
				updateCheckList();

			} else {
				Toast.makeText(mContext, "This is not a checklist",
						Toast.LENGTH_SHORT).show();
			}
		} else if ((Constants.TAKE_VIDEO == commandIndex)
				|| (Constants.TAKE_A_VIDEO == commandIndex)) {
			recordVideo();
		} else if ((Constants.TAKE_AUDIO == commandIndex)
				|| (Constants.TAKE_AN_AUDIO == commandIndex)) {
			Intent i = new Intent(getApplicationContext(), RecordAudio.class);
			startActivityForResult(i, TAKE_AUDIO_REQUEST);
		}

	}

	public void updateCheckList() {

		ControllerRequest request = new ControllerRequest(mContext,
				GUserTasksController.ACTION_CHECK_SUBNODE, new Object[] {
						task_Id, currentSubNodeKey });
		request.setCallbackListener(new CallBackListener() {

			@Override
			public void onSuccess(ControllerResponse response) {
				messageShown = "Options : Show Hotspot No / Completed / Take a Picture";
				subNodeList.get(currentPageInMain).setCheckListDone(true);
				isCheckListDone = true;
				if (uncheckedSubnodeKeys != null) {
					uncheckedSubnodeKeys.remove((subNodeList
							.get(currentPageInMain).getsubNodeKey()));
				}
				Utils.putToPreference(mContext, Constants.NEW_MAIL,
						Constants.YES);
				ImageView iv = (ImageView) (findViewById(R.id.iv_checkList));
				if (iv != null) {
					iv.setImageResource(R.drawable.checklist_done);
					iv.setVisibility(View.GONE);
				}

				Toast.makeText(mContext, "Task status updated in Server",
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onStart() {
			}

			@Override
			public void onError(ControllerResponse response) {
				dismissDialog();
				Toast.makeText(mContext,
						"Unable to update status. Please try again",
						Toast.LENGTH_SHORT).show();
			}

		});

		Controller.executeAsync(request, GUserTasksController.class);

	}

	public void takePicture() {
		cameraIntent = new Intent(mContext, CameraActivity.class);
		cameraIntent.putExtra("TASK_ID", task_Id);
		startActivityForResult(cameraIntent, TAKE_PICTURE_REQUEST);
	}

	@Override
	protected void onStop() {
		Log.d("TCS", "***** onStop in HOTSPOT ACTIVITY");
		super.onStop();
	}

	private static final int SPEECH_REQUEST = 0;

	protected void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, messageShown);
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			getSpeechResults((ArrayList<String>) results);
		} else if (requestCode == TAKE_VIDEO_REQUEST && resultCode == RESULT_OK) {
			String picturePath = data
					.getStringExtra(Intents.EXTRA_VIDEO_FILE_PATH);
			processPictureWhenReady(picturePath);
		} else if (requestCode == TAKE_AUDIO_REQUEST && resultCode == RESULT_OK) {
			String audioPath = data.getStringExtra("AUDIOFILE");
			upLoadMultimedia(new File(audioPath), Constants.AUDIO, " ");
		} else if (requestCode == GET_ROOT_CAUSE_REQUEST
				&& resultCode == RESULT_OK) {
			String rootCause = data.getStringExtra(Keywords.ROOT_CAUSE);
			changeStatus(rootCause);
		} else if (requestCode == TAKE_PICTURE_REQUEST
				&& resultCode == RESULT_OK) {
			String path = data.getStringExtra(Keywords.FILE_PATH);
			upLoadMultimedia(new File(path), Constants.IMAGE, "");
			if (Utils.getFromPreference(mContext, Keywords.SUBMIT_STATUS)
					.equalsIgnoreCase(Keywords.TRUE)) {
				getRootCause();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.upload_skip, menu);

		SpannableStringBuilder text = new SpannableStringBuilder();
		text.append("Upload Photo");
		text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		MenuItem item1 = menu.findItem(R.id.upload);
		item1.setTitle(text);

		MenuItem item2 = menu.findItem(R.id.skip);
		text = new SpannableStringBuilder();
		text.append("Submit Status");
		text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		item2.setTitle(text);

		return true;

	}

	public String getCurrentHspot() {
		return currentHspot;
	}

	public void setCurrentHspot(String currentHspot) {
		this.currentHspot = currentHspot;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean retVal = false;
		switch (item.getItemId()) {
		case R.id.upload:
			takePicture();
			retVal = true;
			break;
		case R.id.skip:
			retVal = true;
			getRootCause();
			break;
		default:
			retVal = true;
			break;
		}
		return retVal;
	}

	public void changeStatus(String rootCause) {

		ControllerRequest request = new ControllerRequest(mContext,
				GUserTasksController.ACTION_CHANGE_TASKS_STATUS, new Object[] {
						task_Id, "finish", rootCause });
		request.setCallbackListener(new CallBackListener() {

			@Override
			public void onSuccess(ControllerResponse response) {
				if (pBar != null)
					pBar.setVisibility(View.GONE);
				Utils.putToPreference(mContext, Keywords.STORY_STATUS,
						"completed");
				Utils.putToPreference(mContext, Constants.NEW_MAIL,
						Constants.YES);
				Toast.makeText(mContext, "Task status updated in Server",
						Toast.LENGTH_SHORT).show();
				Utils.databaseHelper.deleteTask(task_Id);
				HotSpotActivity.this.finish();

			}

			@Override
			public void onStart() {
				if (pBar != null)
					pBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onError(ControllerResponse response) {
				if (pBar != null)
					pBar.setVisibility(View.GONE);

				if (response.getStatus() == Controller.STATUS_USER_NOT_PRIVILAGED) {
					Utils.clearSharedPref(mContext);
					Intent mainPage = new Intent(mContext, MenuActivity.class);
					mainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainPage);
				} else {
					Toast.makeText(
							mContext,
							"Unable to update status. Please try again --- status is "
									+ response.getStatus(), Toast.LENGTH_LONG)
							.show();
				}
			}

		});

		Controller.executeAsync(request, GUserTasksController.class);

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
			if (null == response.getUserId()) {
				return;
			} else {
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
			Bitmap image = null;
			if (((BitmapDrawable) imageView.getDrawable()) == null) {
				image = Bitmap.createBitmap(640, 360, Bitmap.Config.ARGB_8888);
			} else {
				image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
			}

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

			if (currentHspot == null)
				sendOnlineStatusToSocket("enablePaint", "nil", null, null,
						null, null);
			else
				sendOnlineStatusToSocket("enablePaint", currentHspot, null,
						null, null, null);
		}

	};

	public void recordVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(intent, TAKE_VIDEO_REQUEST);
	}

	private void processPictureWhenReady(final String picturePath) {
		final File pictureFile = new File(picturePath);

		if (pictureFile.exists()) {
			upLoadMultimedia(pictureFile, Constants.VIDEO, " ");
		} else {
			final File parentDirectory = pictureFile.getParentFile();
			FileObserver observer = new FileObserver(parentDirectory.getPath(),
					FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
				private boolean isFileWritten;

				@Override
				public void onEvent(int event, String path) {
					if (!isFileWritten) {
						File affectedFile = new File(parentDirectory, path);
						isFileWritten = affectedFile.equals(pictureFile);

						if (isFileWritten) {
							stopWatching();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									processPictureWhenReady(picturePath);
								}
							});
						}
					}
				}
			};
			observer.startWatching();
		}
	}

	public void upLoadMultimedia(File file, int type, String message) {
		ControllerRequest request = new ControllerRequest(mContext,
				GMultimediaUploadController.ACTION_TASK_MULTIMEDIA_UPLOAD,
				new Object[] { task_Id, message, file, new Integer(type) });
		request.setCallbackListener(uploadListener);
		Controller.executeAsync(request, GMultimediaUploadController.class);
	}

	public void getRootCause() {
		Intent rootCauseIntent = new Intent(mContext, RootCauseActivity.class);
		rootCauseIntent.putStringArrayListExtra(Keywords.ROOT_CAUSE_LIST,
				rootCauseList);
		startActivityForResult(rootCauseIntent, GET_ROOT_CAUSE_REQUEST);
	}

	private CallBackListener uploadListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			Toast.makeText(mContext, "File uploaded to the server",
					Toast.LENGTH_SHORT).show();
		}

		public void onStart() {
		}

		public void onError(ControllerResponse response) {
			Toast.makeText(mContext, response.getErrorMessage(),
					Toast.LENGTH_SHORT).show();
		}
	};

}
