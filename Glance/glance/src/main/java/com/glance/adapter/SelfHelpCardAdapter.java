package com.glance.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glance.R;
import com.glance.bean.response.GGetStoryListResponseBean.Story;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class SelfHelpCardAdapter extends CardScrollAdapter {

	ArrayList<Story> storyList;
	private LayoutInflater inflater;
	private Context mContext;

	private RobotoTextView menuItem;

	public SelfHelpCardAdapter(Context ctx, ArrayList<Story> storyList) {
		this.mContext = ctx;
		this.storyList = storyList;
	}

	@Override
	public int getCount() {
		return storyList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.self_help_card, parent, false);
		menuItem = (RobotoTextView) itemView.findViewById(R.id.tv_storyitem);
		menuItem.setText(storyList.get(position).getStoryName());
		itemView.setTag(position);
		return itemView;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return storyList.get(arg0);
	}

	public int getPosition(Object arg0) {
		// TODO Auto-generated method stub
		return storyList.indexOf(arg0);
	}

}