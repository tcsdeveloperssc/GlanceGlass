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
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.MenuAdapter;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Constants.Keywords;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollView;

public class RootCauseActivity extends BaseActivity{
	
	private MenuAdapter mAdapter;
	private Context mContext;
	private LinearLayout dots_holder;
	private int currentPage;
	private CardScrollView mCardScrollView = null;
	private ArrayList<String> rootCauseList = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.root_cause);
		rootCauseList = getIntent().getStringArrayListExtra(Keywords.ROOT_CAUSE_LIST);
		mContext = RootCauseActivity.this;
	}
	
	@Override
	protected void onResume() {
		setupPager();
		super.onResume();
	}
	
	public void setupPager(){
		Toast.makeText(mContext, "Please tap and select an option", Toast.LENGTH_SHORT).show();
		currentPage = 0;
		LinearLayout card_view = (LinearLayout) findViewById(R.id.card_root_cause);
		dots_holder = (LinearLayout) findViewById(R.id.ll_scrollbar_menu_root_cause);
		

		if (card_view != null)
			card_view.removeAllViews();
		mCardScrollView = new CardScrollView(this);
		mAdapter = new MenuAdapter(getApplicationContext(), rootCauseList);
		mAdapter.notifyDataSetChanged();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.setOnItemClickListener(new CardClickListener());
		mCardScrollView.setOnItemSelectedListener(new CardSelectedListener());
		mCardScrollView.activate();
		card_view.addView(mCardScrollView);

		updateIndicator(currentPage);
		
	}
	
	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view1, int position,
				long id) {
			View view = mCardScrollView.getSelectedView();

			String menu_item = (String) ((RobotoTextView) view
					.findViewById(R.id.tv_menu_item)).getText();
			Intent data = new Intent();
			data.putExtra(Keywords.ROOT_CAUSE, menu_item);
			setResult(RESULT_OK, data);
			finish();
			
		}
		
	}
	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			currentPage = position;
			updateIndicator(position);
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
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