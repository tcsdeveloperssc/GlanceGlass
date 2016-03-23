package com.glance.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.UserTasksCardAdapter;
import com.glance.bean.model.GUserTask;
import com.glance.bean.response.GGetUserTaskQueueResponse;
import com.glance.controller.GMultimediaUploadController;
import com.glance.controller.GUserTasksController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.database.DatabaseHelper;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.GFileManager;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.Gson;

public class TaskListActivity extends BaseActivity {

	private static String TAG = "TaskListActivity";
	private LinearLayout mCardView_tasks;
	private CardScrollView mCardScrollView = null;
	// private LinearLayout mCardView_tasks;
	private TextView error_txt;
	private ArrayList<GUserTask> tasks;
	private int commandIndex;
	private int currentPage;
	public static String STORY_ID = "storyId";
	public static String TASK_ID = "taskId";
	private LinearLayout dots_holder;
	private UserTasksCardAdapter mAdapter;
	private static boolean isCallFromSpeechRecognizer = false;
	private Context mContext;
	private ProgressBar pTaskListBar;
	private boolean isTapEnabled = false;
	private String assetId;
	private boolean isFromSync = false;
	private String rootCause;
	private String toastMessage = "Tap and speak a command";
	private int lastSelectedPage = 0;

	CallBackListener listener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

			try {

				if (null != mCardView_tasks) {
					mCardView_tasks.removeAllViews();
				}
				if (null != mAdapter) {
					try {
						mAdapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (pTaskListBar != null) {
					pTaskListBar.setVisibility(View.GONE);
				}
				Log.d("TCS", "****** Connecting complete ************ ");
				error_txt.setVisibility(View.GONE);
				GGetUserTaskQueueResponse response2 = (GGetUserTaskQueueResponse) response
						.getResult();

				dismissDialog();
				if (response2 != null) {
					tasks = response2.getUserTasksList();

					boolean validTaskSize = false;
					if (null == tasks)
						validTaskSize = false;
					else {
						if (tasks.size() <= 0)
							validTaskSize = false;
						else
							validTaskSize = true;
					}

					if (!validTaskSize
							&& (Utils.getFromPreference(
									getApplicationContext(),
									Constants.OFFLINE_MODE)
									.equalsIgnoreCase("true"))) {
						/*
						 * Toast.makeText(getApplicationContext(),
						 * "No data was saved in online mode",
						 * Toast.LENGTH_SHORT).show();
						 */
						error_txt.setText("No tasks found");
						error_txt.setVisibility(View.VISIBLE);

					} else {
						if (null != tasks && tasks.size() > 0) {
							isTapEnabled = true;
							Log.d("Task Test", "TaskListActivity");

							if (Utils.getFromPreference(
									getApplicationContext(),
									Constants.OFFLINE_MODE).equalsIgnoreCase(
									"true")) {
								toastMessage = "Tap to view the options";
							} else {
								toastMessage = "Tap and speak a command";
							}
							Toast.makeText(mContext, toastMessage,
									Toast.LENGTH_SHORT).show();

							mAdapter = new UserTasksCardAdapter(mContext, tasks);

							/*
							 * mCardView_tasks.setOffscreenPageLimit(5);
							 * mCardView_tasks.setPageMargin(10);
							 */
							mCardScrollView = new CardScrollView(mContext);
							try {
								mAdapter.notifyDataSetChanged();
								mCardScrollView.setAdapter(mAdapter);
								mCardScrollView
										.setOnItemClickListener(new CardClickListener());
								mCardScrollView
										.setOnItemSelectedListener(new CardSelectedListener());
								mCardScrollView.activate();
								mCardView_tasks.addView(mCardScrollView);
							} catch (Exception e) {
								e.printStackTrace();
							}

							/*
							 * mCardView_tasks.setOnPageChangeListener(new
							 * OnPageChangeListener() {
							 * 
							 * @Override public void onPageSelected(int
							 * position) { currentPage = position;
							 * updateIndicator(currentPage);
							 * 
							 * }
							 * 
							 * @Override public void onPageScrolled(int arg0,
							 * float arg1, int arg2) { }
							 * 
							 * @Override public void
							 * onPageScrollStateChanged(int arg0) { //
							 * displaySpeechRecognizer();
							 * 
							 * } });
							 */

							dots_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder1));
							updateIndicator(0);

						} else {
							isTapEnabled = false;

							if (String.valueOf(response2.getStatus())
									.equalsIgnoreCase("103")) {
								// Toast.makeText(mContext,
								// response2.getMessage(),
								// Toast.LENGTH_SHORT).show();
								logOut();
							} else if (String.valueOf(response2.getStatus())
									.equalsIgnoreCase("104")) {
								// Toast.makeText(mContext,
								// response2.getMessage(),
								// Toast.LENGTH_SHORT).show();
								logOut();
							} else {
								if (String.valueOf(response2.getStatus())
										.equalsIgnoreCase("100")) {
									String story_status = Utils
											.getFromPreference(mContext,
													Keywords.STORY_STATUS);

									if (story_status
											.equalsIgnoreCase("completed")) {
										if (null != mCardView_tasks) {
											mCardView_tasks.removeAllViews();
										}
										if (null != mAdapter) {
											mAdapter.notifyDataSetChanged();
										}

										Utils.putToPreference(mContext,
												Keywords.STORY_STATUS,
												"started");
									}
								} else if (String
										.valueOf(response2.getStatus())
										.equalsIgnoreCase("0")) {
									error_txt.setText("No tasks found");
									error_txt.setVisibility(View.VISIBLE);
								} else {
									if (dots_holder != null ) 
										dots_holder.removeAllViews();
									error_txt.setText(response2.getMessage());
									error_txt.setVisibility(View.VISIBLE);

									error_txt
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {

												}
											});
								}

							}
						}
					}
				} else {
					/*
					 * Toast.makeText(mContext, "No tasks found",
					 * Toast.LENGTH_SHORT).show();
					 */
					error_txt.setText("No tasks found");
					error_txt.setVisibility(View.VISIBLE);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		}

		public void onError(ControllerResponse response) {
			if (pTaskListBar != null) {
				pTaskListBar.setVisibility(View.GONE);
			}
			dismissDialog();
			if (null != mCardView_tasks) {
				mCardView_tasks.removeAllViews();
			}
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			error_txt.setText("No tasks found");
			error_txt.setVisibility(View.VISIBLE);
			Log.i(TAG, "Got response");
		};
	};

	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			// position=pos;
			currentPage = pos;
			updateIndicator(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isCallFromSpeechRecognizer = false;

		mContext = this;
		Utils.putToPreference(mContext, Constants.NEW_MAIL, Constants.NO);
		if (Utils.checkNetwork(mContext)) {
			Utils.putToPreference(mContext, Constants.OFFLINE_MODE, "false");
		} else {
			Utils.putToPreference(mContext, Constants.OFFLINE_MODE, "true");
			Toast.makeText(mContext, "Currently in Offline mode",
					Toast.LENGTH_SHORT).show();

		}

		setContentView(R.layout.task_list);

		error_txt = (TextView) findViewById(R.id.tv_error);

		// mPager = (ViewPager) findViewById(R.id.story_pager);
		mCardView_tasks = (LinearLayout) findViewById(R.id.tasks_cards);
		pTaskListBar = (ProgressBar) findViewById(R.id.pbTaskList);
		if (pTaskListBar != null)
			pTaskListBar.setVisibility(View.VISIBLE);

		GLog.d("TASK LIST", "***************** OnCreate ****************** ");
		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			if (Constants.OFFLINE_MODE.equalsIgnoreCase(getIntent().getExtras()
					.getString("activity"))) {
				isFromSync = true;
			} else {
				isFromSync = false;
			}
		}
		if (!isFromSync) {
			if (intent.getExtras() != null) {
				assetId = getIntent().getExtras().getString(
						AssetDetailActivity.ASSET_ID);
			}

			if (assetId != null) {
				getAssetUserTasks(assetId);
			} else {
				getUserTasks();
			}
		}

	}

	@Override
	public void onStop() {
		GLog.d("TASK LIST", "***************** OnStop ****************** ");
		super.onStop();
		// LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mFayeAlertReceiver);
		// utils.removeLocationListener();

	}

	@Override
	public void onResume() {
		GLog.d("MailAlert", "************ onResume TASK LIST************* ");

		super.onResume();

		Intent intent = getIntent();
		if (intent.getExtras() != null)
			assetId = getIntent().getExtras().getString(
					AssetDetailActivity.ASSET_ID);

		if (intent.getExtras() != null)
			if (Constants.OFFLINE_MODE.equalsIgnoreCase(getIntent().getExtras()
					.getString("activity")))
				isFromSync = true;

		// if (dots_holder != null) {
		// dots_holder.removeAllViews();
		// }

		if (!isCallFromSpeechRecognizer && !isFromSync /* && assetId == null */) {
			isCallFromSpeechRecognizer = false;

			String story_status = Utils.getFromPreference(this,
					Keywords.STORY_STATUS);
			updateIndicator(-1);
			/*
			 * if (null != mAdapter) { mAdapter.notifyDataSetChanged(); }
			 */
			if (story_status.equalsIgnoreCase("completed")) {
				showCustomisedDialog("Loading");
				if (null != mCardView_tasks) {
					mCardView_tasks.removeAllViews();
				}
				if (null != mAdapter) {
					mAdapter.notifyDataSetChanged();
				}

			}

			if (Utils.getFromPreference(mContext, Constants.NEW_MAIL)
					.equalsIgnoreCase(Constants.YES)) {
				if (assetId != null) {
					getAssetUserTasks(assetId);
				} else {
					getUserTasks();
				}
			}

		} else if (isFromSync) {
			isCallFromSpeechRecognizer = false;

			if (pTaskListBar != null) {
				pTaskListBar.setVisibility(View.GONE);
			}
			tasks = Utils.databaseHelper.getTaskList();
			if (tasks.size() > 0) {
				Toast.makeText(mContext, "Tap to sync", Toast.LENGTH_SHORT)
						.show();
				mAdapter = new UserTasksCardAdapter(mContext, tasks);
				mCardScrollView = new CardScrollView(mContext);
				mCardScrollView.setOnItemClickListener(new CardClickListener());
				mCardScrollView
						.setOnItemSelectedListener(new CardSelectedListener());

				try {
					mCardScrollView.setAdapter(mAdapter);
					mCardScrollView.activate();
					mCardView_tasks.addView(mCardScrollView);
					mAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}

				dots_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder1));
				updateIndicator(0);
			} else {

				error_txt.setText("No tasks found");
				error_txt.setVisibility(View.VISIBLE);
			}

		} else {
			GLog.d("FIRST", "************ onResume *************--2 ");
			isCallFromSpeechRecognizer = false;
		}
		mContext = this;
	}

	public void getUserTasks() {
		 if (assetId != null) {
		 getAssetUserTasks(assetId);
		 } else {
		tasks = null;
		if (pTaskListBar != null) {
			pTaskListBar.setVisibility(View.VISIBLE);
		}
		ControllerRequest request = new ControllerRequest(mContext,
				GUserTasksController.ACTION_USER_TASKS_QUEUE);
		request.setCallbackListener(listener);
		Controller.executeAsync(request, GUserTasksController.class);
		 }

	}

	public void getAssetUserTasks(String assetId) {
		tasks = null;
		if (pTaskListBar != null) {
			pTaskListBar.setVisibility(View.VISIBLE);
		}
		ControllerRequest request = new ControllerRequest(mContext,
				GUserTasksController.ACTION_ASSET_TASKS_QUEUE,
				new Object[] { assetId });
		request.setCallbackListener(listener);
		Controller.executeAsync(request, GUserTasksController.class);
	}

	public void updateIndicator(int currentPage) {

		if (currentPage == -1)
			currentPage = lastSelectedPage;

		if (dots_holder != null && mAdapter != null) {
			dots_holder.removeAllViews();
			DotsScrollBar.createDotScrollBar(mContext, dots_holder,
					currentPage, mAdapter.getCount());
			lastSelectedPage = currentPage;
		}

	}

	public void getSpeechResults(ArrayList<String> results) {

		GLog.d("SPEECH_SERVICE", "Inside getSpeechResults : task list");

		commandIndex = Utils.keyFinder(results);
		String task_id = ((GUserTask) tasks.get(currentPage)).getTaskId();
		if (Constants.SHOW_TASK == commandIndex) {

			String task_status = ((GUserTask) tasks.get(currentPage))
					.getTaskStatus();
			if (task_status == null)
				task_status = ((GUserTask) tasks.get(currentPage)).getStatus();
			if (task_id != null) {
				if (task_status.equalsIgnoreCase(Constants.ASSIGNED))
					this.changeTaskStatus("start", task_id);
				else
					startHotspotActivity();

			}

		} else if (Constants.NAVIGATE == commandIndex) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String latitude = ((GUserTask) tasks.get(currentPage))
					.getLatitude();
			String longitude = ((GUserTask) tasks.get(currentPage))
					.getLongitude();
			// intent.setData(Uri.parse("google.navigation:q=48.649469,-2.02579"));
			if (latitude != null && longitude != null) {
				intent.setData(Uri.parse("google.navigation:q=" + latitude
						+ "," + longitude));
				startActivity(intent);
			} else {
				Toast.makeText(mContext, "No location set for the task",
						Toast.LENGTH_SHORT).show();
			}
		} else if (isFromSync) {

			rootCause = results.get(0);
			if (Utils.databaseHelper == null)
				Utils.createDatabase(mContext);
			ArrayList<String> imagePath = Utils.databaseHelper
					.getIntermediateImageForUpload(task_id,
							DatabaseHelper.TABLE_FINAL_IMAGE_FOR_UPLOAD);
			changeTaskStatus("finish", task_id);
			Toast.makeText(mContext, "Task status updated in server",
					Toast.LENGTH_SHORT).show();
			finish();
			if (imagePath.size() > 0) {
				upLoadImageAndMessage(rootCause, task_id, imagePath.get(0));
			}

			ArrayList<String> imageInterPath = Utils.databaseHelper
					.getIntermediateImageForUpload(task_id,
							DatabaseHelper.TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD);
			if (imageInterPath.size() > 0) {
				for (String path : imageInterPath) {
					upLoadImageAndMessage("Intermediate pic in offline",
							task_id, path);
				}
				Toast.makeText(mContext, "Image will be uploaded",
						Toast.LENGTH_SHORT).show();
			} else {
				/*
				 * Toast.makeText(mContext, "No images left to upload",
				 * Toast.LENGTH_SHORT).show();
				 */
			}

			Utils.databaseHelper.deleteTask(task_id);

		}

	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			// Include null check
			if (tasks != null && (currentPage < tasks.size())) {
				// Bug--------------------------NMK--------------------
				String status = ((GUserTask) tasks.get(currentPage))
						.getStatus();
				if (status == null)
					status = ((GUserTask) tasks.get(currentPage))
							.getTaskStatus();
				String task_id = ((GUserTask) tasks.get(currentPage))
						.getTaskId();
				String story_id = ((GUserTask) tasks.get(currentPage))
						.getStoryId();
				if (Utils.getFromPreference(mContext, Constants.OFFLINE_MODE)
						.equals("false")) {

					if (isFromSync) {

						if (Constants.DRAFT_COMPLETE.equalsIgnoreCase(status)) {
							displaySpeechRecognizer("Speak Root cause");
						} else {
							if (Utils.databaseHelper == null)
								Utils.createDatabase(mContext);

							ArrayList<String> imageInterPath = Utils.databaseHelper
									.getIntermediateImageForUpload(
											task_id,
											DatabaseHelper.TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD);
							String checked_keys = Utils.databaseHelper
									.getCheckList(task_id);
							if (imageInterPath.size() > 0
									|| checked_keys != null) {
								for (String path : imageInterPath)
									upLoadImageAndMessage(
											"Intermediate pic in offline",
											task_id, path);
								Utils.databaseHelper.deleteTask(task_id);
								Toast.makeText(mContext,
										"The images will be uploaded",
										Toast.LENGTH_SHORT).show();
								syncCheckedKeys(task_id);
								tasks = Utils.databaseHelper.getTaskList();
								if (tasks.size() > 0) {
									mAdapter = new UserTasksCardAdapter(
											mContext, tasks);
									mCardScrollView = new CardScrollView(
											mContext);
									mCardScrollView
											.setOnItemClickListener(new CardClickListener());
									mCardScrollView
											.setOnItemSelectedListener(new CardSelectedListener());
									/*
									 * mCardView_tasks.setOffscreenPageLimit(5);
									 * mCardView_tasks.setPageMargin(10);
									 */
									currentPage = 0;
									updateIndicator(currentPage);
									try {
										mCardScrollView.setAdapter(mAdapter);
										mCardView_tasks
												.addView(mCardScrollView);
										mAdapter.notifyDataSetChanged();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									if (mCardView_tasks != null) {
										mCardView_tasks
												.setVisibility(View.INVISIBLE);
									}
									error_txt
											.setText("No tasks available for sync");
									error_txt.setVisibility(View.VISIBLE);
								}

							}
						}
					} else {
						String user = ((GUserTask) tasks.get(currentPage))
								.getUserName();
						String currentUser = Utils.getFromPreference(mContext,
								Keywords.USER_NAME);
						if (currentUser != null
								&& currentUser.equalsIgnoreCase(user)) {
							GUserTask task = (GUserTask) tasks.get(currentPage);
							task.getAssignedTime();
							String taskType = ((GUserTask) tasks
									.get(currentPage)).getTaskType();
							String taskStatus = ((GUserTask) tasks
									.get(currentPage)).getStatus();
							if (taskStatus == null)
								taskStatus = ((GUserTask) tasks
										.get(currentPage)).getTaskStatus();
							ArrayList<String> checkedSubNodeKeys = ((GUserTask) tasks
									.get(currentPage)).getCheckedSubNodeKeys();
							if (taskType != null) {
								if ((taskType.equalsIgnoreCase("yes"))
										&& (taskStatus
												.equalsIgnoreCase("assigned"))) {
									Intent selfHelp = new Intent(mContext,
											SelfHelpActivity.class);
									selfHelp.putExtra(Constants.ACTION,
											Constants.TASK_LIST);
									selfHelp.putStringArrayListExtra(
											Constants.SUBNODE_KEYS,
											checkedSubNodeKeys);
									selfHelp.putExtra(Constants.STORY_ID,
											story_id);
									selfHelp.putExtra(Constants.TASK_ID,
											task_id);
									startActivity(selfHelp);
								} else {
									displaySpeechRecognizer("Options : Show Task / Navigate");
								}
							} else {
								displaySpeechRecognizer("Options : Show Task / Navigate");
							}
						} else {
							Toast.makeText(mContext,
									"This task is assigned to another user",
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {

					/*
					 * if (Constants.DRAFT_COMPLETE.equalsIgnoreCase(status)) {
					 * 
					 * Toast.makeText(mContext,
					 * "Task completed in offline mode",
					 * Toast.LENGTH_SHORT).show();
					 * 
					 * } Intent intent = new Intent(mContext,
					 * OfflineHotSpotActivity.class); intent.putExtra(STORY_ID,
					 * story_id); intent.putExtra(TASK_ID, task_id);
					 * startActivity(intent);
					 */if (Constants.DRAFT_COMPLETE.equalsIgnoreCase(status)) {

						Toast.makeText(mContext,
								"Task completed in offline mode",
								Toast.LENGTH_SHORT).show();

					} else if (Constants.ASSIGNED.equalsIgnoreCase(status)) {
						Toast.makeText(mContext,
								"Task not viewed in online mode",
								Toast.LENGTH_SHORT).show();
					} else {
						ArrayList<String> checkedSubNodeKeys = ((GUserTask) tasks
								.get(currentPage)).getCheckedSubNodeKeys();
						Intent intent = new Intent(mContext,
								OfflineHotSpotActivity.class);
						intent.putExtra(STORY_ID, story_id);
						intent.putStringArrayListExtra(Constants.SUBNODE_KEYS,
								checkedSubNodeKeys);
						intent.putExtra(TASK_ID, task_id);
						if (story_id == null) {
							Toast.makeText(mContext,
									"Task not viewed in online mode",
									Toast.LENGTH_SHORT).show();
						} else
							startActivity(intent);
					}

				}

			}

		}

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

			if (event.getAction() == KeyEvent.ACTION_UP) {
				// Include null check
				if (tasks != null && (currentPage < tasks.size())) {
					// Bug--------------------------NMK--------------------
					String status = ((GUserTask) tasks.get(currentPage))
							.getStatus();
					String task_id = ((GUserTask) tasks.get(currentPage))
							.getTaskId();
					if (Utils.getFromPreference(mContext,
							Constants.OFFLINE_MODE).equals("false")) {

						if (isFromSync) {

							if (Constants.DRAFT_COMPLETE
									.equalsIgnoreCase(status)) {
								displaySpeechRecognizer("Speak Root cause");
							} else {
								if (Utils.databaseHelper == null)
									Utils.createDatabase(mContext);

								ArrayList<String> imageInterPath = Utils.databaseHelper
										.getIntermediateImageForUpload(
												task_id,
												DatabaseHelper.TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD);
								if (imageInterPath.size() > 0) {
									for (String path : imageInterPath)
										upLoadImageAndMessage(
												"Intermediate pic in offline",
												task_id, path);
									Utils.databaseHelper.deleteTask(task_id);
									Toast.makeText(mContext,
											"The images will be uploaded",
											Toast.LENGTH_SHORT).show();

									tasks = Utils.databaseHelper.getTaskList();
									if (tasks.size() > 0) {
										mAdapter = new UserTasksCardAdapter(
												mContext, tasks);
										mCardScrollView = new CardScrollView(
												mContext);
										mCardScrollView
												.setOnItemClickListener(new CardClickListener());
										mCardScrollView
												.setOnItemSelectedListener(new CardSelectedListener());
										/*
										 * mCardView_tasks.setOffscreenPageLimit(
										 * 5);
										 * mCardView_tasks.setPageMargin(10);
										 */
										currentPage = 0;
										updateIndicator(currentPage);
										try {
											mCardScrollView
													.setAdapter(mAdapter);
											mCardView_tasks
													.addView(mCardScrollView);
											mAdapter.notifyDataSetChanged();
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										if (mCardView_tasks != null) {
											mCardView_tasks
													.setVisibility(View.INVISIBLE);
										}
										error_txt
												.setText("No tasks available for sync");
										error_txt.setVisibility(View.VISIBLE);
									}

								}
							}
						} else {
							displaySpeechRecognizer("Options : Show Task / Navigate");
						}
					} else {

						if (Constants.DRAFT_COMPLETE.equalsIgnoreCase(status)) {

							Toast.makeText(mContext,
									"Task completed in offline mode",
									Toast.LENGTH_SHORT).show();
							return true;
						}
						Intent intent = new Intent(mContext,
								OfflineHotSpotActivity.class);

						String story_id = ((GUserTask) tasks.get(currentPage))
								.getStoryId();

						intent.putExtra(STORY_ID, story_id);
						intent.putExtra(TASK_ID, task_id);

						/*
						 * tasks = null; mAdapter = null;
						 */

						startActivity(intent);
					}

					return true;
				}
			}
		}

		if (keyCode == 4) {
			finish();
			return true;
		}

		return false;
	}

	private static final int SPEECH_REQUEST = 0;

	private void displaySpeechRecognizer(String msg) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, msg);
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		GLog.d("FIRST", "************ onActivityResult ************* "
				+ requestCode + resultCode);
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			GLog.d("FIRST", "************ onActivityResult --OK ************* ");
			isCallFromSpeechRecognizer = true;
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			getSpeechResults((ArrayList<String>) results);
		}
	}

	public void changeTaskStatus(final String status, final String task_Id) {

		ControllerRequest request = new ControllerRequest(mContext,
				GUserTasksController.ACTION_CHANGE_TASKS_STATUS, new Object[] {
						task_Id, status, "" });
		request.setCallbackListener(new CallBackListener() {

			@Override
			public void onSuccess(ControllerResponse response) {
				dismissDialog();
				if (!isFromSync) {
					if (pTaskListBar != null)
						pTaskListBar.setVisibility(View.GONE);
					String task_status = ((GUserTask) tasks.get(currentPage))
							.getTaskStatus();
					if (task_status != null) {
						((GUserTask) tasks.get(currentPage))
								.setTaskStatus(Constants.RUNNING);
					} else {
						task_status = ((GUserTask) tasks.get(currentPage))
								.getStatus();
						((GUserTask) tasks.get(currentPage))
								.setStatus(Constants.RUNNING);
					}
					String tId = null;
					if ((tasks != null) && (tasks.size() > 0)
							&& (currentPage < tasks.size())) {
						tId = ((GUserTask) tasks.get(currentPage))
								.getTaskId();
					}
					changeFile(tId);
					if (mAdapter != null)
						mAdapter.notifyDataSetChanged();
					startHotspotActivity();

				}

			}

			@Override
			public void onStart() {
				showCustomisedDialog("");
				if (pTaskListBar != null)
					pTaskListBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onError(ControllerResponse response) {
				if (pTaskListBar != null)
					pTaskListBar.setVisibility(View.GONE);
				dismissDialog();

				if (response.getStatus() == Controller.STATUS_USER_NOT_PRIVILAGED) {
					Utils.clearSharedPref(mContext);
					Intent mainPage = new Intent(mContext, MenuActivity.class);
					mainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainPage);
				}
			}

		});
		Controller.executeAsync(request, GUserTasksController.class);

	}

	public void upLoadImageAndMessage(String message, String taskId,
			String imagePath) {

		ControllerRequest request = new ControllerRequest(mContext,
				GMultimediaUploadController.ACTION_TASK_MULTIMEDIA_UPLOAD, new Object[] {
						taskId, message, new File(imagePath) });
		request.setCallbackListener(uploadListener);
		Controller.executeAsync(request, GMultimediaUploadController.class);
	}

	private CallBackListener uploadListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

			Toast.makeText(mContext, "Image uploaded to the server",
					Toast.LENGTH_SHORT).show();

		}

		public void onStart() {
		}

		public void onError(ControllerResponse response) {
			Toast.makeText(mContext, response.getErrorMessage(),
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}

	public void startHotspotActivity() {

		Intent intent = new Intent(mContext, HotSpotActivity.class);
		if (tasks != null) {
			String story_id = ((GUserTask) tasks.get(currentPage)).getStoryId();
			String task_id = ((GUserTask) tasks.get(currentPage)).getTaskId();
			ArrayList<String> checkedSubNodeKeys = ((GUserTask) tasks
					.get(currentPage)).getCheckedSubNodeKeys();
			intent.putExtra(STORY_ID, story_id);
			intent.putStringArrayListExtra(Constants.SUBNODE_KEYS,
					checkedSubNodeKeys);
			intent.putExtra(TASK_ID, task_id);

			Log.d("glance.tom", "story ID: " + story_id + " , task_id: " + task_id);

			// tasks = null;
			// mPager = null;
			// mAdapter = null;

			startActivity(intent);
		}

	}

	public void changeFile(String task_Id) {
		GGetUserTaskQueueResponse response = null;
		String fileName = null;
		if (assetId == null)
			fileName = GFileManager.USER_TASKS;
		else
			fileName = GFileManager.ASSET_TASKS;
		Gson gson_obj = new Gson();
		response = gson_obj.fromJson(GFileManager.getStoryFromFile(
				fileName, mContext),
				GGetUserTaskQueueResponse.class);
		if (response != null){
		GUserTask changedTask = response.getTaskByTaskId(task_Id);
		changedTask.setStatus(Constants.RUNNING);
		response.setTaskByTaskId(task_Id, changedTask);
		Utils.putToPreference(mContext, Constants.NEW_MAIL, Constants.YES);
		String json = gson_obj.toJson(response);

		// writing edited file
		GFileManager.writeStoryToFile(mContext, fileName, json);
		}

	}

	public void syncCheckedKeys(String task_id) {
		if (Utils.databaseHelper == null)
			Utils.createDatabase(mContext);
		String checked_keys = Utils.databaseHelper.getCheckList(task_id);
		List<String> items = Arrays.asList(checked_keys.split("\\s*,\\s*"));

		if (items.size() > 0) {
			for (String key : items)
				updateCheckKeysInServer(task_id, key);
			Utils.databaseHelper.deleteTask(task_id);

		}
	}

	public void updateCheckKeysInServer(String task_Id, String key) {

		ControllerRequest request = new ControllerRequest(mContext,
				GUserTasksController.ACTION_CHECK_SUBNODE, new Object[] {
						task_Id, key });
		request.setCallbackListener(new CallBackListener() {

			@Override
			public void onSuccess(ControllerResponse response) {

			}

			@Override
			public void onStart() {
			}

			@Override
			public void onError(ControllerResponse response) {
				dismissDialog();

				/*
				 * if (response.getStatus() ==
				 * Controller.STATUS_USER_NOT_PRIVILAGED) {
				 * Utils.clearSharedPref(mContext); Intent mainPage = new
				 * Intent(mContext, MenuActivity.class);
				 * mainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 * startActivity(mainPage); } else {
				 */
				Toast.makeText(mContext,
						"Unable to update status. Please try again",
						Toast.LENGTH_SHORT).show();
				// }
			}

		});

		Controller.executeAsync(request, GUserTasksController.class);

	}

}
