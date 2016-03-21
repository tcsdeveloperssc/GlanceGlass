package com.glance.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.adapter.UserMailsAdapter;
import com.glance.bean.model.GUserMail;
import com.glance.bean.model.GUserMail.Receivers;
import com.glance.bean.model.Recipient;
import com.glance.bean.response.GGetMailsResponse;
import com.glance.controller.GMailController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.fragments.DotsScrollBar;
import com.glance.utils.Utils.GLog;
import com.google.android.glass.widget.CardScrollView;

public class MailboxActivity extends BaseActivity {

	private static String TAG = "MailBoxActivity";
	private LinearLayout mail_layout;
	private CardScrollView mCardScrollView;
	private TextView error_txt;
	private ArrayList<GUserMail> mails;
	private int currentPage;
	private LinearLayout dots_holder;
	private UserMailsAdapter mAdapter;
	private static boolean isCallFromSpeechRecognizer = false;
	private Context mContext;
	private ProgressBar pMailBoxBar;
	private ArrayList<Recipient> recepientList = new ArrayList<Recipient>();
	private String action = null;
	private boolean alertsAvailable = false;

	private CallBackListener usermailListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			if (pMailBoxBar != null) {
				pMailBoxBar.setVisibility(View.GONE);
			}
			Log.d("TCS", "****** Connecting complete ************ ");
			error_txt.setVisibility(View.GONE);

			/* GGetMailResponse */
			GGetMailsResponse response2 = (GGetMailsResponse) response
					.getResult();

			dismissDialog();
			if (response2 != null) {
				if (action.equalsIgnoreCase("inbox"))
					mails = response2.getReceived();
				else if (action.equalsIgnoreCase("sentItems"))
					mails = response2.getSent();

				if (null != mails && mails.size() > 0) {
					if (null != mail_layout) {
						mail_layout.removeAllViews();
					}
					if (null != mAdapter) {
						try {
							mAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					alertsAvailable = true;
					mCardScrollView = new CardScrollView(MailboxActivity.this);
					mAdapter = new UserMailsAdapter(
							getSupportFragmentManager(), mContext, mails);
					mCardScrollView.setAdapter(mAdapter);
					mCardScrollView.setOnItemClickListener(new CardClickListener());
					mCardScrollView.setOnItemSelectedListener(new CardSelectedListener());
					mCardScrollView.activate();
					
					mail_layout.addView(mCardScrollView);

					

					dots_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder_mail));
					updateIndicator(0);

				} else {
					alertsAvailable = false;

					if (String.valueOf(response2.getStatus()).equalsIgnoreCase(
							"103")) {
						// Toast.makeText(mContext, response2.getMessage(),
						// Toast.LENGTH_SHORT).show();
						logOut();
					} else if (String.valueOf(response2.getStatus())
							.equalsIgnoreCase("104")) {
						// Toast.makeText(mContext, response2.getMessage(),
						// Toast.LENGTH_SHORT).show();
						logOut();
					} else {

						/* No mails */

						if (((response2.getMessage())
								.equalsIgnoreCase("success"))
								&& (action.equalsIgnoreCase("sentItems")))
							error_txt.setText("Sent Items is Empty");
						else if (((response2.getMessage())
								.equalsIgnoreCase("success"))
								&& (action.equalsIgnoreCase("inbox")))
							error_txt.setText("Inbox is Empty");
						else if (((response2.getMessage())
								.equalsIgnoreCase("No alerts available for user"))
								&& (action.equalsIgnoreCase("sentItems")))
							error_txt.setText("Sent Items is Empty");
						else if (((response2.getMessage())
								.equalsIgnoreCase("No alerts available for user"))
								&& (action.equalsIgnoreCase("inbox")))
							error_txt.setText("Inbox is Empty");

						else
							error_txt.setText(response2.getMessage());
						error_txt.setVisibility(View.VISIBLE);

					}
				}
			} else {
				if (action.equalsIgnoreCase("sentItems")) {
					error_txt.setText("Sent Items is Empty");
				} else {
					error_txt.setText("Inbox is Empty");
				}
				error_txt.setVisibility(View.VISIBLE);
			}

		}

		public void onError(ControllerResponse response) {
			if (pMailBoxBar != null) {
				pMailBoxBar.setVisibility(View.GONE);
			}
			if (error_txt != null){
			if (action.equalsIgnoreCase("sentItems"))
				error_txt.setText("Sent Items is Empty");
			else if (action.equalsIgnoreCase("inbox"))
				error_txt.setText("Inbox is Empty");
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
			if (alertsAvailable)
				openOptionsMenu();
			
		}
	}
	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			currentPage = pos;
			updateIndicator(currentPage);
			setReceivers();
		
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void setReceivers() {
		recepientList.clear();
		String senderName = ((GUserMail) mails.get(currentPage))
				.getSenderName();
		String senderId = ((GUserMail) mails.get(currentPage)).getSenderId();
		Recipient r1 = new Recipient();
		r1.setUserId(senderId);
		r1.setName(senderName);
		recepientList.add(r1);

		ArrayList<Receivers> receiverList = ((GUserMail) mails.get(currentPage))
				.getReceivers();

		if (receiverList != null) {
			for (int i = 0; i < receiverList.size(); i++) {
				String rName = ((receiverList.get(i)).getReceiverName());
				String rId = ((receiverList.get(i)).getReceiverId());
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = getIntent().getStringExtra("activity");
		setContentView(R.layout.mail_box);

		error_txt = (TextView) findViewById(R.id.mb_error);
		mContext = this;
		mail_layout = (LinearLayout) findViewById(R.id.card_layout_mail);
		pMailBoxBar = (ProgressBar) findViewById(R.id.pbMailBox);

		GLog.d("MAIL BOX", "***************** OnCreate ****************** ");
	}

	@Override
	public void onStop() {
		GLog.d("MAIL BOX", "***************** OnStop ****************** ");
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

		if (!isCallFromSpeechRecognizer) {
			getMails();
		} else {
			GLog.d("MAIL BOX", "************ onResume *************--2 ");
			isCallFromSpeechRecognizer = false;
		}
		mContext = MailboxActivity.this;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void getMails() {
		if (pMailBoxBar != null) {
			pMailBoxBar.setVisibility(View.VISIBLE);
		}

		ControllerRequest request = new ControllerRequest(getApplicationContext(),
				GMailController.ACTION_GET_MAIL);
		request.setCallbackListener(usermailListener);
		Controller.executeAsync(request, GMailController.class);
	}

	CallBackListener replymailListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {

			Toast.makeText(mContext, "Mail Sent Successfully",
					Toast.LENGTH_SHORT).show();

		}

		public void onError(ControllerResponse response) {
			Toast.makeText(mContext, "Mail Sending failed", Toast.LENGTH_SHORT)
					.show();
		}
	};

	public void sendMail(String text) {
		ControllerRequest request = new ControllerRequest(mContext,
				GMailController.ACTION_SEND_MAIL, new Object[] { recepientList,
						text });
		request.setCallbackListener(replymailListener);
		Controller.executeAsync(request, GMailController.class);
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
				if (alertsAvailable)
					openOptionsMenu();
				return true;
			}
		}

		if (keyCode == 4) {
			finish();
			return true;
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.reply_cancel, menu);

		SpannableStringBuilder text = new SpannableStringBuilder();
		text.append("REPLY");
		text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		MenuItem item1 = menu.findItem(R.id.reply_menu_item);
		item1.setTitle(text);

		MenuItem item2 = menu.findItem(R.id.cancel_it_menu_item);
		text = new SpannableStringBuilder();
		text.append("CANCEL");
		text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		item2.setTitle(text);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// Handle item selection. Menu items typically start another
		// activity, start a service, or broadcast another intent.
		switch (item.getItemId()) {
		case R.id.reply_menu_item:
			displaySpeechRecognizer();
			return true;

		case R.id.cancel_it_menu_item:

			return true;

		default:
			return true;
		}
	}

	private static final int SPEECH_REQUEST = 0;

	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speak the message to be sent");
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		GLog.d("FIRST", "************ onActivityResult ************* "
				+ requestCode + resultCode);
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			GLog.d("FIRST", "************ onActivityResult --OK ************* ");
			isCallFromSpeechRecognizer = true;
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			sendMail(results.get(0));

		}
	}

	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

}
