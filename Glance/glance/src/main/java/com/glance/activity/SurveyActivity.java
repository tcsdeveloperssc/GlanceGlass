package com.glance.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.glance.R;
import com.glance.adapter.SurveyAdapter;
import com.glance.adapter.UserListsCardAdapter;
import com.glance.bean.model.GAssignedSurvey;
import com.glance.bean.response.GAssignedSurveyResponse;
import com.glance.controller.GNearbyUsersController;
import com.glance.controller.GSurveyController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Utils.GLog;
import com.google.android.glass.widget.CardScrollView;

public class SurveyActivity extends BaseActivity {

	



	private static String TAG = "SurveyActivity";

	

	private LinearLayout mCardViewSurveyList;
	private CardScrollView mCardScrollView = null;
	private TextView error_txt;
	private ArrayList<GAssignedSurvey> surveyList;
	private int currentPage;
	private LinearLayout dots_holder;
	private SurveyAdapter mAdapter;
	private Context mContext;
	private ProgressBar pBarUserList;
	private boolean isTapEnabled = false;
	public static final String SURVEY_ID = "surveyId";
	
	CallBackListener listener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

			try {

				if (null != mCardViewSurveyList) {
					mCardViewSurveyList.removeAllViews();
				}
				if (null != mAdapter) {
					try {
						mAdapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (pBarUserList != null) {
					pBarUserList.setVisibility(View.GONE);
				}
				Log.d("TCS", "****** Connecting complete ************ ");
				error_txt.setVisibility(View.GONE);

				GAssignedSurveyResponse response2 = (GAssignedSurveyResponse) response
						.getResult();

				dismissDialog();
				if (response2 != null) {
					surveyList = response2.getAssignedSurveys();

					boolean validListSize = false;
					if (null == surveyList)
						validListSize = false;
					else {
						if (surveyList.size() <= 0)
							validListSize = false;
						else
							validListSize = true;
					}

					if (validListSize) {
						isTapEnabled = true;
						Log.d(TAG, "NearbyUserListActivity");

						mAdapter = new SurveyAdapter(mContext, surveyList);
						mCardScrollView = new CardScrollView(mContext);
						try {
							mAdapter.notifyDataSetChanged();
							mCardScrollView.setAdapter(mAdapter);
							mCardScrollView
									.setOnItemSelectedListener(new CardSelectedListener());
							mCardScrollView.activate();
							mCardScrollView.setOnItemClickListener(new CardClickListener());
							mCardViewSurveyList.addView(mCardScrollView);
						} catch (Exception e) {
							e.printStackTrace();
						}
						dots_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder_user_list));
						updateIndicator(0);

					} else {
						isTapEnabled = false;

						error_txt.setText("No users found");
						error_txt.setVisibility(View.VISIBLE);

						error_txt.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

							}
						});

					}

				} else {
					error_txt.setText("No surveys found");
					error_txt.setVisibility(View.VISIBLE);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		}

		public void onError(ControllerResponse response) {
			if (pBarUserList != null) {
				pBarUserList.setVisibility(View.GONE);
			}
			dismissDialog();
			if (null != mCardViewSurveyList) {
				mCardViewSurveyList.removeAllViews();
			}
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			error_txt.setText("No surveys found");
			error_txt.setVisibility(View.VISIBLE);
			Log.i(TAG, "Got response");
		};
	};

	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			currentPage = pos;
			updateIndicator(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {
			Intent surveyIntent = new Intent(mContext, SurveyDetailActivity.class);
			String id = ((GAssignedSurvey)mCardScrollView.getAdapter().getItem(position)).getPublishedSurveyId();
			surveyIntent.putExtra(SURVEY_ID, id);
			startActivity(surveyIntent);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.nearby_user_list);
		error_txt = (TextView) findViewById(R.id.tv_error_user_list);
		mCardViewSurveyList = (LinearLayout) findViewById(R.id.users_cards);
		pBarUserList = (ProgressBar) findViewById(R.id.pbUserList);
		if (pBarUserList != null)
			pBarUserList.setVisibility(View.VISIBLE);

		GLog.d(TAG, "***************** OnCreate ****************** ");

	}

	@Override
	public void onStop() {
		GLog.d(TAG, "***************** OnStop ****************** ");
		super.onStop();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (dots_holder != null) {
			dots_holder.removeAllViews();
		}

		getSurveyList();

	}

	public void getSurveyList() {

		if (pBarUserList != null) {
			pBarUserList.setVisibility(View.VISIBLE);
		}

		

		ControllerRequest request = new ControllerRequest(mContext,
				GSurveyController.ACTION_GET_ASSIGNED_SURVEY);
		request.setCallbackListener(listener);
		Controller.executeAsync(request, GSurveyController.class);

	}

	public void updateIndicator(int currentPage) {

		if (dots_holder != null && mAdapter != null) {
			dots_holder.removeAllViews();
			DotsScrollBar.createDotScrollBar(mContext, dots_holder,
					currentPage, mAdapter.getCount());
		}

	}

	
	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

}
