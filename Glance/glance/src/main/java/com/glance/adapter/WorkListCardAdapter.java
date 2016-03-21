package com.glance.adapter;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glance.R;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class WorkListCardAdapter extends CardScrollAdapter{
	private RobotoTextView menuItem;
	private LayoutInflater inflater;
	List<String> workList=new ArrayList<String>();
	Context ctx;
	public WorkListCardAdapter(List<String> workList,Context ctx)
	{
		this.workList=workList;
		this.ctx=ctx;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return workList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return workList.get(arg0);
	}

	public int getPosition(Object arg0) {
		// TODO Auto-generated method stub
		return workList.indexOf(arg0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.menu, parent, false);

		menuItem = (RobotoTextView) itemView.findViewById(R.id.tv_menu_item);

		menuItem.setText(workList.get(position));

		itemView.setTag(position);
		
		
		return itemView;
		
	}

}
