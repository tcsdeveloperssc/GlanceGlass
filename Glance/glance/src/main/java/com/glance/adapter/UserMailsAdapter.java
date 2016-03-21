package com.glance.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glance.R;
import com.glance.bean.model.GUserMail;
import com.glance.bean.model.GUserMail.Receivers;
import com.glance.view.RobotoTextView;
import com.google.android.glass.widget.CardScrollAdapter;

public class UserMailsAdapter extends CardScrollAdapter {
	private Context mContext;;
	private LayoutInflater inflater;
	private ArrayList<GUserMail> userMails = new ArrayList<GUserMail>();
	private RobotoTextView mail_text;
	private RobotoTextView mail_sender;
	private RobotoTextView mail_receivers;
	private RobotoTextView mail_time;
	private Date date;

	public UserMailsAdapter(FragmentManager fragmentManager, Context ctx,
			ArrayList<GUserMail> userMails) {

		this.mContext = ctx;
		this.userMails = userMails;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userMails.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return userMails.get(arg0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.mails, parent, false);

		if (userMails != null) {
			String sender = "From : "
					+ ((GUserMail) userMails.get(position)).getSenderName();
			String time = ((GUserMail) userMails.get(position)).getTime();
			String receivers = "To : ";// +((GUserMail)userMails.get(position)).getReceiverName();
			long milliSec = Long.parseLong(time);
			String body = ((GUserMail) userMails.get(position)).getText();

			ArrayList<Receivers> receiverList = ((GUserMail) userMails
					.get(position)).getReceivers();
			if (receiverList != null) {
				for (int i = 0; i < receiverList.size(); i++) {
					receivers = receivers
							+ ((receiverList.get(i)).getReceiverName()) + ";";
				}
			}

			mail_text = (RobotoTextView) (itemView.findViewById(R.id.mailtext));
			mail_sender = (RobotoTextView) (itemView.findViewById(R.id.sender_name));
			mail_receivers = (RobotoTextView)(itemView
					.findViewById(R.id.receiver_name));
			mail_time = (RobotoTextView) (itemView.findViewById(R.id.mail_time));
			date = new Date(milliSec);
			mail_sender.setText(sender);
			mail_receivers.setText(receivers);
			mail_time.setText(date.toString());
			mail_text.setText(body);

		}

		itemView.setTag(position);
		return itemView;
	}

	@Override
	public int getPosition(Object arg0) {
		// TODO Auto-generated method stub
		return userMails.indexOf(arg0);
	}

}