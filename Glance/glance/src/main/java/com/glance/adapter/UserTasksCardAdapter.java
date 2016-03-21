package com.glance.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.glance.R;
import com.glance.bean.model.GUserTask;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class UserTasksCardAdapter extends CardScrollAdapter {

	ArrayList<GUserTask> userTasks;
	private LayoutInflater inflater;
	private Context mContext;

	RobotoTextView textView, statusTextView;

	public UserTasksCardAdapter(Context ctx, ArrayList<GUserTask> userTasks) {
		this.mContext = ctx;
		this.userTasks = userTasks;
	}

	@Override
	public int getCount() {

		return userTasks.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.tasks, parent, false);

		ImageView ivPriority = (ImageView) view.findViewById(R.id.ivPriority);

		if (userTasks != null) {
			String name = ((GUserTask) userTasks.get(position)).getTaskName();
			if (name == null){
				name = ((GUserTask) userTasks.get(position)).getTitle();
			}
			String desc = ((GUserTask) userTasks.get(position))
					.getTaskDescription();
			if (desc == null)
				desc = ((GUserTask) userTasks.get(position))
						.getDescription();
			String priority = ((GUserTask) userTasks.get(position))
					.getPriority();
			String status = ((GUserTask) userTasks.get(position)).getStatus();
			if (status == null)
				status = ((GUserTask) userTasks.get(position)).getTaskStatus();

			textView = (RobotoTextView) view
					.findViewById(com.glance.R.id.newtext);
			statusTextView = (RobotoTextView) view
					.findViewById(R.id.statusText);
			statusTextView.setText(status);
			textView.setText(name + "\n\n" + desc);

			if (priority.equalsIgnoreCase("high")) {
				ivPriority.setImageResource(R.drawable.high);
			} else if (priority.equalsIgnoreCase("medium")) {
				ivPriority.setImageResource(R.drawable.medium);
			} else {
				ivPriority.setImageResource(R.drawable.low);
			}

		}

		view.setTag(position);
		return view;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return userTasks.get(arg0);
	}

	public int getPosition(Object arg0) {
		// TODO Auto-generated method stub
		return userTasks.indexOf(arg0);
	}

}