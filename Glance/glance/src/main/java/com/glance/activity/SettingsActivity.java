package com.glance.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;

import com.glance.R;
import com.glance.adapter.SettingsAdapter;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Utils;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollView;

public class SettingsActivity extends BaseActivity {

	private LinearLayout mSettingsCardLayout;
	private CardScrollView settingsCardView;
	private SettingsAdapter mAdapter;
	private Context mContext;
	private LinearLayout dots_holder;
	private int currentPage;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_pager);
		mContext = this;
		mSettingsCardLayout = (LinearLayout) findViewById(R.id.setting_layout);
		dots_holder = (LinearLayout) findViewById(R.id.ll_scrollbar);
		final ArrayList<String> list = Utils.getUrlList();
		mAdapter = new SettingsAdapter(mContext, list);
		settingsCardView = new CardScrollView(mContext);
		settingsCardView.setAdapter(mAdapter);
		settingsCardView.setOnItemClickListener(new CardClickListener());
		settingsCardView.setOnItemSelectedListener(new CardSelectedListener());
		settingsCardView.activate();
		mSettingsCardLayout.addView(settingsCardView);
		updateIndicator(currentPage);

	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {

			View view = settingsCardView.getSelectedView();

			String url = (String) ((RobotoTextView) view
					.findViewById(R.id.tv_url)).getText();

			String savedUrl = Utils.getSavedUrl(mContext);
			if(!url.equalsIgnoreCase(savedUrl)){
				
				Intent data = new Intent();
				data.putExtra(Utils.URL, url);
				mAdapter.notifyDataSetChanged();
				setResult(RESULT_OK, data);
				finish();
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

	public void updateIndicator(int currentPage) {

		if (dots_holder != null && mAdapter != null) {
			dots_holder.removeAllViews();
			DotsScrollBar.createDotScrollBar(mContext, dots_holder,
					currentPage, mAdapter.getCount());
		}

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				return true;
			}
		}

		if (keyCode == 4) {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}

		return false;
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
