package com.glance.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glance.R;
import com.glance.bean.model.GUserBean;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class UserListsCardAdapter extends CardScrollAdapter {

	ArrayList<GUserBean> userListal;
	private LayoutInflater inflater;
	private Context mContext;

	RobotoTextView userNameView, distanceView;

	public UserListsCardAdapter(Context ctx, ArrayList<GUserBean> userlists) {
		this.mContext = ctx;
		this.userListal = userlists;
	}

	@Override
	public int getCount() {

		return userListal.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.nearby_user, parent, false);



		if (userListal != null) {
			String firstName = ((GUserBean) userListal.get(position)).getFirst_name();
			String lastName = ((GUserBean) userListal.get(position)).getLast_name();
			

			String distance = ((GUserBean) userListal.get(position)).getDistance();
			userNameView = (RobotoTextView) view
					.findViewById(com.glance.R.id.nearby_username);
			distanceView = (RobotoTextView) view
					.findViewById(R.id.tv_distance);
			distanceView.setText(distance + " miles");
			userNameView.setText(firstName + " " + lastName);



		}

		view.setTag(position);
		return view;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return userListal.get(arg0);
	}

	public int getPosition(Object arg0) {
		// TODO Auto-generated method stub
		return userListal.indexOf(arg0);
	}

}