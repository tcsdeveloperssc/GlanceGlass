package com.glance.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.SelfHelpCardAdapter;
import com.glance.bean.response.GGetStoryListResponseBean;
import com.glance.bean.response.GGetStoryListResponseBean.Story;
import com.glance.controller.GStoryController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Constants;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.Utils;
import com.google.android.glass.widget.CardScrollView;

public class SelfHelpActivity extends BaseActivity {

	private static String TAG = "MailBoxActivity";
	private LinearLayout selfHelpLayout;
	private CardScrollView mCardScrollView;
	private TextView error_txt;
	private ArrayList<Story> storyList;
	private int currentPage;
	private LinearLayout dots_holder;
	private SelfHelpCardAdapter mAdapter;
	private String action = null;
	private String taskId = null;
	private String storyId = null;
	private Context mContext;
	private ProgressBar pBarSelfHelp;
	public static String STORY_ID = "storyId";
	public static String TASK_ID = "taskId";
	private boolean callingHotspotActivity = false;
	private Intent intent = null;

	private CallBackListener listener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			if (pBarSelfHelp != null) {
				pBarSelfHelp.setVisibility(View.GONE);
			}
			Log.d("TCS", "****** Connecting complete ************ ");
			error_txt.setVisibility(View.GONE);

			/* GGetMailResponse */
			GGetStoryListResponseBean response2 = (GGetStoryListResponseBean) response
					.getResult();

			dismissDialog();
			storyList = (ArrayList<Story>) response2.getStories();
			if (response2 != null) {

				if (null != storyList && storyList.size() > 0) {
					if (null != selfHelpLayout) {
						selfHelpLayout.removeAllViews();
					}
					if (null != mAdapter) {
						try {
							mAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					mCardScrollView = new CardScrollView(SelfHelpActivity.this);
					mAdapter = new SelfHelpCardAdapter(mContext, storyList);
					mCardScrollView.setAdapter(mAdapter);
					mCardScrollView
							.setOnItemClickListener(new CardClickListener());
					mCardScrollView
							.setOnItemSelectedListener(new CardSelectedListener());
					mCardScrollView.activate();

					selfHelpLayout.addView(mCardScrollView);

					dots_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder_self_help));
					updateIndicator(0);

				} else {

					if (String.valueOf(response2.getStatus()).equalsIgnoreCase(
							"103")) {
						logOut();
					} else if (String.valueOf(response2.getStatus())
							.equalsIgnoreCase("104")) {
						logOut();
					} else {
						error_txt.setText("No user manuels found");
						error_txt.setVisibility(View.VISIBLE);

					}
				}
			} else {

				error_txt.setText("No user manuels found");
				error_txt.setVisibility(View.VISIBLE);
			}

		}

		public void onError(ControllerResponse response) {
			if (pBarSelfHelp != null) {
				pBarSelfHelp.setVisibility(View.GONE);
			}
			if (error_txt != null) {
				error_txt.setText("No user manuels found");
				error_txt.setVisibility(View.VISIBLE);
			}
			dismissDialog();
			Log.i(TAG, "Got response");
		};
	};

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {
			
			if (action != null && action.equalsIgnoreCase(Constants.TASK_LIST)) {
				callingHotspotActivity = true;
				intent = new Intent(mContext, HotSpotActivity.class);
				intent.putExtra(Constants.ACTION, Constants.SELF_HELP);
				if (storyList != null) {
					storyId = storyList.get(position).getStoryId();
				}
				if (storyId != null) {
					intent.putExtra(STORY_ID, storyId);
					intent.putExtra(TASK_ID, taskId);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(mContext, "No story found",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				callingHotspotActivity = false;
				intent = new Intent(mContext, UserManuelActivity.class);
				if (storyList != null) {
					storyId = storyList.get(position).getStoryId();
				}
				if (storyId != null) {
					intent.putExtra(STORY_ID, storyId);
					intent.putExtra(TASK_ID, taskId);
					startActivity(intent);
				} else {
					Toast.makeText(mContext, "No story found",
							Toast.LENGTH_SHORT).show();
				}

			}

		}
	}

	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			currentPage = pos;
			updateIndicator(currentPage);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.self_help);
		action = getIntent().getStringExtra(Constants.ACTION);
		taskId = getIntent().getStringExtra(Constants.TASK_ID);
		error_txt = (TextView) findViewById(R.id.sh_error);
		mContext = this;
		selfHelpLayout = (LinearLayout) findViewById(R.id.card_layout_self_help);
		pBarSelfHelp = (ProgressBar) findViewById(R.id.pb_self_help);
		Utils.putToPreference(mContext, Keywords.STORY_STATUS, "started");

	}

	@Override
	public void onStop() {

		super.onStop();

	}

	@Override
	public void onResume() {

		super.onResume();
		if (error_txt != null)
			error_txt.setVisibility(View.GONE);
		if (dots_holder != null) {
			dots_holder.removeAllViews();
		}
		getStoryList();
		mContext = SelfHelpActivity.this;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void getStoryList() {
		if (pBarSelfHelp != null) {
			pBarSelfHelp.setVisibility(View.VISIBLE);
		}

		ControllerRequest request = new ControllerRequest(
				getApplicationContext(),
				GStoryController.ACTION_GET_STORIES_LIST);
		request.setCallbackListener(listener);
		Controller.executeAsync(request, GStoryController.class);
	}

	public void updateIndicator(int currentPage) {

		if (dots_holder != null && mAdapter != null) {
			dots_holder.removeAllViews();
			DotsScrollBar.createDotScrollBar(mContext, dots_holder,
					currentPage, mAdapter.getCount());
		}

	}

	@Override
	protected void onDestroy() {
		/*if (callingHotspotActivity && intent != null)
			startActivity(intent);*/
		
		super.onDestroy();
	}

	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}

}
