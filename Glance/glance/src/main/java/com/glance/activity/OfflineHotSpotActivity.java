package com.glance.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
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
import com.glance.bean.model.GUserTask;
import com.glance.bean.model.HotSpots;
import com.glance.bean.model.MainNode;
import com.glance.bean.model.SubNodes;
import com.glance.bean.response.GGetUserTaskQueueResponse;
import com.glance.controller.GStoryController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.controller.core.StreamListener;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.ArtiFactImageTask;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.CustomTypefaceSpan;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.glance.view.ImageScreen;
import com.glance.view.RobotoTextView;
import com.glance.view.VideoScreen;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.Gson;

public class OfflineHotSpotActivity extends BaseActivity {

	private ArrayList<SubNodes> subNodeList = new ArrayList<SubNodes>();
	private ArrayList<SubNodes> mainSubNodeList = new ArrayList<SubNodes>();
	public static ArrayList<MainNode> listCriteriaSatisfiedNodes = new ArrayList<MainNode>();
	private TextView error_txt;
	private LinearLayout mStoryLayout;
	private CardScrollView mCardScrollView;
	private static MediaPlayer mediaPlayer;
	private SubNodeAdapter mAdapter;
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

	private ArtiFactImageTask artTask;
	private String user_id = "", security_token = "", tenant_id = "",
			imageName = "";
	public String story_Id, task_Id;
	private String prev_node;
	public Intent cameraIntent;
	public boolean btakePicture = false;
	private boolean isCheckListDone = true;
	private ArrayList<String> uncheckedSubnodeKeys = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.putToPreference(OfflineHotSpotActivity.this, Constants.NEW_MAIL,
				Constants.NO);
		setContentView(R.layout.story_activity);
		((ProgressBar) findViewById(R.id.pb_getstory)).setVisibility(View.GONE);
		Utils.putToPreference(mContext, Keywords.STORY_STATUS, "started");

		if (!inLinkNodeActivity) {
			story_Id = getIntent().getStringExtra(Constants.STORY_ID);
			task_Id = getIntent().getStringExtra(Constants.TASK_ID);
			error_txt = (RobotoTextView) findViewById(R.id.error_txt);
			getStoryProcess();
		} else {
			error_txt = (RobotoTextView) findViewById(R.id.error_txt_link_node);
		}

	}

	@Override
	protected void onResume() {

		super.onResume();

		String story_status = Utils.getFromPreference(this,
				Keywords.STORY_STATUS);
		if (story_status.equalsIgnoreCase("completed")) {
			OfflineHotSpotActivity.this.finish();
		}

		mContext = OfflineHotSpotActivity.this;
		ImageView iv = (ImageView) (findViewById(R.id.iv_checkList));
		if (isCheckListDone) {
			if (iv != null) {
				// iv.setImageResource(R.drawable.checklist_done);
				iv.setVisibility(View.GONE);
			}
		} else {
			if (iv != null) {
				// iv.setImageResource(R.drawable.checklist_done);
				iv.setVisibility(View.VISIBLE);
			}

		}
	}

	@Override
	protected void onPause() {

		GLog.d("SPEECH_RECEIVER", "Calling base activity on pause : HotSpot");
		killMediaPlayer();
		super.onPause();

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
			Toast.makeText(getApplicationContext(),
					response.getErrorMessage()/*
											 * getString(R.string.
											 * stories_not_available)
											 */, Toast.LENGTH_SHORT).show();
			error_txt.setText(getString(R.string.stories_not_available));
			error_txt.setVisibility(View.GONE);
			GLog.d("TCS",
					"*************** on error occured  ****************** ");
			finish();
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

		mCardScrollView = new CardScrollView(mContext);
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
			if (btakePicture) {
				btakePicture = false;
				// startActivity(cameraIntent);
			} else {
				openOptionsMenu();
			}
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

			supportInvalidateOptionsMenu();
			if (flag == MAIN_PAGER) {
				currentPageInMain = pos;
				initialize(subNodeArray, currentPageInMain);
				prev_node = subNodeArray.get(pos).getsubNodeKey();
			} else {

				currentPageInLink = pos;
				initialize(subNodeArray, currentPageInLink);
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

			GLog.d("SWIPE", "isCheckListDone True");
		} else {
			if (iv != null)
				iv.setVisibility(View.VISIBLE);

		}

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
				if (streamType.equalsIgnoreCase("video")) {

					/*
					 * artTask = new
					 * ArtiFactImageTask(mContext,"video",videoListener);
					 * artTask.execute(link_array.getJSONObject(i).getString(
					 * "artifactId"));
					 */
					if (Utils.databaseHelper == null)
						Utils.createDatabase(mContext);

					Intent videoIntent = new Intent(mContext, VideoScreen.class);
					String url = Utils.databaseHelper.getImageUrl(link_array
							.getJSONObject(i).getString("artifactId"));
					videoIntent.putExtra(Constants.VIDEO_URL_KEY,
							Utils.databaseHelper.getVideoName(url));

					videoListener.onSuccess(url);
					mContext.startActivity(videoIntent);

				} else if (streamType.equalsIgnoreCase("audio")) {
					/*
					 * audioListener.setStreamName(imageName);
					 * audioListener.onSuccess();
					 */
				} else if (streamType.equalsIgnoreCase("image")) {

					Intent imageIntent = new Intent(mContext, ImageScreen.class);
					imageIntent.putExtra("artifactId", link_array
							.getJSONObject(i).getString("artifactId"));
					mContext.startActivity(imageIntent);
				} else if (streamType.equalsIgnoreCase("link")) {

					String link_node = link_array.getJSONObject(i).getString(
							"action");

					GLog.d("GLANCE", "calling navigateToLink" + link_node);

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
			linkNodeIntent.putExtra("task_id", task_Id);
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

	public void takePicture(String taskStatus) {
		cameraIntent = new Intent(mContext, CameraActivity.class);
		cameraIntent.putExtra("TASK_ID", task_Id);
		cameraIntent.putExtra("TASK_ACTION", taskStatus);
		/*
		 * Toast.makeText(mContext, "Please focus and tap to take picture",
		 * Toast.LENGTH_SHORT).show();
		 */
		btakePicture = true;
		startActivity(cameraIntent);// uncommented
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
				if (btakePicture) {
					btakePicture = false;
					// startActivity(cameraIntent);
				} else {
					openOptionsMenu();
				}
				return true;
			}
		}

		if (keyCode == 4) {
			if (isAudioPlayingStarted)
				killMediaPlayer();
			else
				finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.offlinemenu, menu);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");

		SpannableStringBuilder text = new SpannableStringBuilder();
		if (hotSpotList != null) {
			int count = hotSpotList.size();
			for (int i = 0; i < count; i++) {
				String title = "";
				switch (i) {
				case 0:
					title = getString(R.string.show_one);
					break;
				case 1:
					title = getString(R.string.show_two);
					break;
				case 2:
					title = getString(R.string.show_three);
					break;
				case 3:
					title = getString(R.string.show_four);
					break;
				case 4:
					title = getString(R.string.show_five);
					break;

				default:
					break;
				}
				MenuItem item = menu.add(title);
				text = new SpannableStringBuilder();
				text.append(item.getTitle());
				text.setSpan(new CustomTypefaceSpan("", font), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				/*
				 * text.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
				 * text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				 */
				item.setTitle(text);

				item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						if (item.getTitle()
								.toString()
								.equalsIgnoreCase(
										(getString(R.string.show_one)))) {
							if (hotSpotList != null && hotSpotList.size() > 0)
								hotspotClickSpeechEvent(hotSpotList.get(0));
						} else if (item
								.getTitle()
								.toString()
								.equalsIgnoreCase(
										(getString(R.string.show_two)))) {
							if (hotSpotList != null && hotSpotList.size() > 0)
								hotspotClickSpeechEvent(hotSpotList.get(1));
						} else if (item
								.getTitle()
								.toString()
								.equalsIgnoreCase(
										getString(R.string.show_three))) {
							if (hotSpotList != null && hotSpotList.size() > 0)
								hotspotClickSpeechEvent(hotSpotList.get(2));
						} else if (item
								.getTitle()
								.toString()
								.equalsIgnoreCase(getString(R.string.show_four))) {
							if (hotSpotList != null && hotSpotList.size() > 0)
								hotspotClickSpeechEvent(hotSpotList.get(3));
						} else if (item
								.getTitle()
								.toString()
								.equalsIgnoreCase(getString(R.string.show_five))) {
							if (hotSpotList != null && hotSpotList.size() > 0)
								hotspotClickSpeechEvent(hotSpotList.get(4));
						}

						return true;
					}
				});
			}
		}

		/*
		 * text.append(getString(R.string.show_one)); text.setSpan(new
		 * ForegroundColorSpan(Color.WHITE), 0, text.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); MenuItem item1 =
		 * menu.findItem(R.id.show_1); item1.setTitle(text);
		 * 
		 * 
		 * MenuItem item2 = menu.findItem(R.id.show_2); text = new
		 * SpannableStringBuilder(); text.append(getString(R.string.show_two));
		 * text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); item2.setTitle(text);
		 */

		MenuItem item3 = menu.add(R.string.complete_with_photo);
		text = new SpannableStringBuilder();
		text.append(getString(R.string.complete_with_photo));
		text.setSpan(new CustomTypefaceSpan("", font), 0, text.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		/*
		 * text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 */
		item3.setTitle(text);
		item3.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				if (uncheckedSubnodeKeys.size() > 0){
					Toast.makeText(getApplicationContext(),
							"Please complete the checkList", Toast.LENGTH_SHORT)
							.show();
				}
				else{
				takePicture("finish");
				Utils.putToPreference(mContext, Keywords.STORY_STATUS,
						"completed");
				Utils.putToPreference(mContext, Constants.NEW_MAIL,
						Constants.YES);

				GGetUserTaskQueueResponse response = null;
				Gson gson_obj = new Gson();
				response = gson_obj.fromJson(GFileManager.getStoryFromFile(
						GFileManager.USER_TASKS, mContext),
						GGetUserTaskQueueResponse.class);
				GUserTask changedTask = response.getTaskByTaskId(task_Id);

				changedTask.setStatus(Constants.DRAFT_COMPLETE);
				String json = gson_obj.toJson(response);
				String desc, stat, tname;
				desc = changedTask.getTaskDescription();
				if (desc == null)
					desc = changedTask.getDescription();
				stat = changedTask.getStatus();
				if (stat == null)
					stat = changedTask.getTaskStatus();
				tname = changedTask.getTaskName();
				if (tname == null)
					tname = changedTask.getStoryName();

				Utils.databaseHelper.insertToTaskList(tname, desc,
						changedTask.getPriority(), stat,
						changedTask.getTaskId());
				// writing edited file
				GFileManager.writeStoryToFile(mContext,
						GFileManager.USER_TASKS, json);
				}
				return true;
			}
		});

		MenuItem statusMenu = menu.add(R.string.submit_status);
		text = new SpannableStringBuilder();
		text.append(getString(R.string.submit_status));
		text.setSpan(new CustomTypefaceSpan("", font), 0, text.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		/*
		 * text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 */
		Utils.putToPreference(mContext, Constants.NEW_MAIL,
				Constants.YES);
		statusMenu.setTitle(text);
		statusMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				if (uncheckedSubnodeKeys.size() > 0){
					Toast.makeText(getApplicationContext(),
							"Please complete the checkList", Toast.LENGTH_SHORT)
							.show();
				}
				else{
				Toast.makeText(mContext,
						"Status will be submitted during sync",
						Toast.LENGTH_SHORT).show();
				// editing the written response
				GGetUserTaskQueueResponse response = null;
				Gson gson_obj = new Gson();
				response = gson_obj.fromJson(GFileManager.getStoryFromFile(
						GFileManager.USER_TASKS, mContext),
						GGetUserTaskQueueResponse.class);

				GUserTask changedTask = response.getTaskByTaskId(task_Id);
				changedTask.setStatus(Constants.DRAFT_COMPLETE);
				response.setTaskByTaskId(task_Id, changedTask);
				String json = gson_obj.toJson(response);

				// writing edited file
				GFileManager.writeStoryToFile(mContext,
						GFileManager.USER_TASKS, json);

				Utils.databaseHelper.insertToTaskList(
						changedTask.getTaskName(),
						changedTask.getTaskDescription(),
						changedTask.getPriority(), changedTask.getStatus(),
						changedTask.getTaskId());
				finish();
				}
				return true;
			}
		});

		MenuItem item4 = menu.add(R.string.take_a_picture);
		text = new SpannableStringBuilder();
		text.append(getString(R.string.take_a_picture));
		text.setSpan(new CustomTypefaceSpan("", font), 0, text.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		/*
		 * text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 */
		item4.setTitle(text);
		item4.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				takePicture("start");
				GGetUserTaskQueueResponse response = null;
				Gson gson_obj = new Gson();
				response = gson_obj.fromJson(GFileManager.getStoryFromFile(
						GFileManager.USER_TASKS, mContext),
						GGetUserTaskQueueResponse.class);

				GUserTask changedTask = response.getTaskByTaskId(task_Id);
				changedTask.setStatus(Constants.OFFLINE_EDIT);
				response.setTaskByTaskId(task_Id, changedTask);
				Utils.putToPreference(mContext, Constants.NEW_MAIL,
						Constants.YES);
				String json = gson_obj.toJson(response);

				// writing edited file
				GFileManager.writeStoryToFile(mContext,
						GFileManager.USER_TASKS, json);

				Utils.databaseHelper.insertToTaskList(
						changedTask.getTaskName(),
						changedTask.getTaskDescription(),
						changedTask.getPriority(), changedTask.getStatus(),
						changedTask.getTaskId());

				return true;
			}
		});

		if (uncheckedSubnodeKeys.contains((subNodeList.get(currentPageInMain)
				.getsubNodeKey()))) {
			addMenuOption(menu);

		}

		return true;
	}

	/*
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { switch (item.getItemId()) { case R.id.take_a_picture:
	 * takePicture("start"); return true;
	 * 
	 * case R.id.completed: takePicture("finish");
	 * Utils.putToPreference(mContext, Keywords.STORY_STATUS, "completed");
	 * return true;
	 * 
	 * case R.id.show_1: hotspotClickSpeechEvent(hotSpotList.get(0)); return
	 * true;
	 * 
	 * case R.id.show_2: hotspotClickSpeechEvent(hotSpotList.get(1)); return
	 * true;
	 * 
	 * 
	 * default: return true; } }
	 */

	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}

	private CallBackListener imageLoadListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

		}
	};

	public void addMenuOption(Menu menu) {
		MenuItem item4 = menu.add(R.string.take_a_picture);
		SpannableStringBuilder text = new SpannableStringBuilder();
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");
		text.append(getString(R.string.check));
		text.setSpan(new CustomTypefaceSpan("", font), 0, text.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		/*
		 * text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 */
		item4.setTitle(text);
		item4.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {

				GGetUserTaskQueueResponse response = null;
				Gson gson_obj = new Gson();
				response = gson_obj.fromJson(GFileManager.getStoryFromFile(
						GFileManager.USER_TASKS, mContext),
						GGetUserTaskQueueResponse.class);

				GUserTask changedTask = response.getTaskByTaskId(task_Id);
				changedTask.setStatus(Constants.OFFLINE_EDIT);
				ArrayList<String> keyList = changedTask.getCheckedSubNodeKeys();
				if (null == keyList)
					keyList = new ArrayList<String>();
				
				
				keyList.add(subNodeList.get(currentPageInMain).getsubNodeKey());
				changedTask.setCheckedSubNodeKeys(keyList);
				response.setTaskByTaskId(task_Id, changedTask);
				Utils.putToPreference(mContext, Constants.NEW_MAIL,
						Constants.YES);
				String json = gson_obj.toJson(response);

				// writing edited file
				GFileManager.writeStoryToFile(mContext,
						GFileManager.USER_TASKS, json);

				Utils.databaseHelper.insertToTaskList(
						changedTask.getTaskName(),
						changedTask.getTaskDescription(),
						changedTask.getPriority(), changedTask.getStatus(),
						changedTask.getTaskId());

				String checkList = Utils.databaseHelper
						.getCheckList(changedTask.getTaskId());
				if (checkList != null)
					checkList += ","
							+ subNodeList.get(currentPageInMain)
									.getsubNodeKey();
				else
					checkList = subNodeList.get(currentPageInMain)
							.getsubNodeKey();
				Utils.databaseHelper.insertToTaskCheckList(
						changedTask.getTaskId(), checkList);

				if (uncheckedSubnodeKeys.contains((subNodeList
						.get(currentPageInMain).getsubNodeKey()))) {
					subNodeList.get(currentPageInMain).setCheckListDone(true);
					if (uncheckedSubnodeKeys != null) {
						uncheckedSubnodeKeys.remove((subNodeList
								.get(currentPageInMain).getsubNodeKey()));
					}
					Utils.putToPreference(mContext, Constants.NEW_MAIL,
							Constants.YES);
					// mAdapter.notifyDataSetChanged();

					ImageView iv = (ImageView) (findViewById(R.id.iv_checkList));
					if (iv != null) {
						iv.setImageResource(R.drawable.checklist_done);
						iv.setVisibility(View.GONE);
					}

				}

				return true;
			}
		});
	}

}
