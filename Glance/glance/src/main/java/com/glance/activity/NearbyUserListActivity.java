package com.glance.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.glance.R;
import com.glance.adapter.UserListsCardAdapter;
import com.glance.bean.model.GUserBean;
import com.glance.bean.response.GGetUserListResponse;
import com.glance.controller.GNearbyUsersController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Utils.GLog;
import com.google.android.glass.widget.CardScrollView;

public class NearbyUserListActivity extends BaseActivity {

	private static String TAG = "NearbyUserListActivity";

	private static String DEFAULT_LAT = "41.9969509";
	private static String DEFAULT_LANG = "12.4916254";
	private static String DEFAULT_DISTANCE = "500";

	private LinearLayout mCardView_userList;
	private CardScrollView mCardScrollView = null;
	private TextView error_txt;
	private ArrayList<GUserBean> userList;
	private int currentPage;
	private LinearLayout dots_holder;
	private UserListsCardAdapter mAdapter;
	private Context mContext;
	private ProgressBar pBarUserList;
	private boolean isTapEnabled = false;

	CallBackListener listener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

			try {

				if (null != mCardView_userList) {
					mCardView_userList.removeAllViews();
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

				GGetUserListResponse response2 = (GGetUserListResponse) response
						.getResult();

				dismissDialog();
				if (response2 != null) {
					userList = response2.getUserList();

					boolean validListSize = false;
					if (null == userList)
						validListSize = false;
					else {
						if (userList.size() <= 0)
							validListSize = false;
						else
							validListSize = true;
					}

					if (validListSize) {
						isTapEnabled = true;
						Log.d(TAG, "NearbyUserListActivity");

						mAdapter = new UserListsCardAdapter(mContext, userList);
						mCardScrollView = new CardScrollView(mContext);
						try {
							mAdapter.notifyDataSetChanged();
							mCardScrollView.setAdapter(mAdapter);
							mCardScrollView
									.setOnItemSelectedListener(new CardSelectedListener());
							mCardScrollView.activate();
							mCardView_userList.addView(mCardScrollView);
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
					error_txt.setText("No users found");
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
			if (null != mCardView_userList) {
				mCardView_userList.removeAllViews();
			}
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			error_txt.setText("No users found");
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.nearby_user_list);
		error_txt = (TextView) findViewById(R.id.tv_error_user_list);
		mCardView_userList = (LinearLayout) findViewById(R.id.users_cards);
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

		getUserList();

	}

	public void getUserList() {

		if (pBarUserList != null) {
			pBarUserList.setVisibility(View.VISIBLE);
		}

		SharedPreferences settings = this.getSharedPreferences("USER-LOCATION",
				MODE_WORLD_WRITEABLE);
		String longitude = settings.getString(GPSService.LONG, DEFAULT_LANG);
		String latitude = settings.getString(GPSService.LAT, DEFAULT_LAT);

		if (longitude.equalsIgnoreCase(DEFAULT_LANG)
				&& latitude.equalsIgnoreCase(DEFAULT_LAT)) {
			GLog.d(TAG, "GPS default values taking ");
		} else {
			GLog.d(TAG, "taking GPS values from pref ");
		}

		ControllerRequest request = new ControllerRequest(mContext,
				GNearbyUsersController.ACTION_USER_LIST, new Object[] {
						longitude, latitude, DEFAULT_DISTANCE });
		request.setCallbackListener(listener);
		Controller.executeAsync(request, GNearbyUsersController.class);

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
