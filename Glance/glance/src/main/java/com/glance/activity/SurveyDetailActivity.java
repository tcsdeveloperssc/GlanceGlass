package com.glance.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
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
import com.glance.adapter.SurveyDetailAdapter;
import com.glance.bean.model.GSurveyOption;
import com.glance.bean.model.GSurveyResultResponse;
import com.glance.bean.model.SurveyResponses;
import com.glance.controller.GMultimediaUploadController;
import com.glance.controller.GSurveyController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.utils.Constants;
import com.glance.utils.RecordAudio;
import com.google.android.glass.content.Intents;
import com.google.android.glass.widget.CardScrollView;

public class SurveyDetailActivity extends BaseActivity {

	private String surveyId;
	private LinearLayout dots_holder;
	private SurveyDetailAdapter mAdapter;
	private Context mContext;
	private CardScrollView mCardScrollView = null;
	private TextView error_txt;
	private LinearLayout mCardViewQuesList;
	private ProgressBar pBarQuesList;
	GSurveyResultResponse surveyDetail;
	private int currentPosition;
	private String currentType;
	private static final int TAKE_PICTURE_REQUEST = 21;

	private static final int TAKE_VIDEO_REQUEST = 22;

	private static final int TAKE_AUDIO_REQUEST = 23;
	
	CallBackListener surveyDetailListener = new CallBackListener() {
		
		@Override
		public void onSuccess(ControllerResponse response) {

		 surveyDetail = (GSurveyResultResponse) response
					.getResult();

			if (pBarQuesList != null)
				pBarQuesList.setVisibility(View.GONE);
			if (surveyDetail != null) {

				ArrayList<SurveyResponses> result = surveyDetail
						.getQnsAndResponses();
				if (result != null && result.size() > 0) {
					mAdapter = new SurveyDetailAdapter(mContext, result);
					mCardScrollView = new CardScrollView(mContext);
					try {
						mAdapter.notifyDataSetChanged();
						mCardScrollView.setAdapter(mAdapter);
						mCardScrollView
								.setOnItemSelectedListener(new CardSelectedListener());
						mCardScrollView.activate();
						mCardScrollView
								.setOnItemClickListener(new CardClickListener());
						mCardViewQuesList.addView(mCardScrollView);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dots_holder = (LinearLayout) (findViewById(R.id.dots_scrollbar_holder_user_list));
					//updateIndicator(0);
				} else {

				

					error_txt.setText("No users found");
					error_txt.setVisibility(View.VISIBLE);

					

				}
			} else {

				error_txt.setText("No surveys found");
				error_txt.setVisibility(View.VISIBLE);

			}

		}
		
			
		
	};
	
	public class CardSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			currentPosition = pos;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	public class CardClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view1, int position,
				long arg3) {
			
			if(surveyDetail != null){
				currentType = surveyDetail.getQnsAndResponses().get(position).getOptionType();
				if(Constants.OPT_IMAGE.equalsIgnoreCase(currentType)){
					takePicture();
				}else if(Constants.OPT_AUDIO.equalsIgnoreCase(currentType)){
					takeAudio();
				}else if(Constants.OPT_VIDEO.equalsIgnoreCase(currentType)){
					takeVideo();
				}else{
					openOptionsMenu();
				}
				
			}
			
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.nearby_user_list);
		error_txt = (TextView) findViewById(R.id.tv_error_user_list);
		mCardViewQuesList = (LinearLayout) findViewById(R.id.users_cards);
		pBarQuesList = (ProgressBar) findViewById(R.id.pbUserList);
		if (pBarQuesList != null)
			pBarQuesList.setVisibility(View.VISIBLE);
		if(getIntent() != null){
			Bundle bundle = getIntent().getExtras();
			if(bundle != null){
				this.surveyId = bundle.getString(SurveyActivity.SURVEY_ID);
				getSurveyResults(surveyId);
			}
		}
		
	}
	
	private void getSurveyResults(String surveyId){
		
		ControllerRequest surveyRequest = new ControllerRequest(mContext, GSurveyController.ACTION_GET_SURVEY_RESULT, new Object[]{surveyId});
		surveyRequest.setCallbackListener(surveyDetailListener);
		Controller.executeAsync(surveyRequest, GSurveyController.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");
		List<String> options = new ArrayList<String>();
		if(surveyDetail != null){
			List<GSurveyOption> items = surveyDetail.getQnsAndResponses().get(currentPosition).getOptions();
			
			for(GSurveyOption option :items){
				options.add(option.getOptionText());
			}
		}
		
		for (int i = 0; i < options.size(); i++) {
			MenuItem item = menu.add(Menu.NONE, i, Menu.NONE, options.get(i));

			SpannableStringBuilder text1 = new SpannableStringBuilder();
			text1.append(options.get(i));
			text1.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
					text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			text1.setSpan (new com.glance.utils.CustomTypefaceSpan("", font), 0,
			text1.length(),Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
			item.setTitle(text1);
		}

		return true;

	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		takeOptions(item.getTitle().toString());
		
		return true;
	}
	@Override
	public void getUserTasks() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMails() {
		// TODO Auto-generated method stub

	}
	
	private void takePicture() {
		
		Intent intent = new Intent(getApplicationContext(), ZoomActivity.class);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	}

	private void takeAudio() {
		Intent intent = new Intent(this, RecordAudio.class);
		startActivityForResult(intent, TAKE_AUDIO_REQUEST);
	}

	private void takeVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
		startActivityForResult(intent, TAKE_VIDEO_REQUEST);
	}
	
	private void takeOptions(String options)
	{
		Toast.makeText(getApplicationContext(),
				"Response Saved Successfully", Toast.LENGTH_SHORT).show();
		GSurveyOption selectedOption = surveyDetail.getQnsAndResponses().get(currentPosition).getOptionsSelected().get(currentPosition);
		selectedOption.setOptionText(options);
	}
	
	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent data) {
		
		if (requestcode == TAKE_AUDIO_REQUEST && resultcode == RESULT_OK) {
			File audioFile = new File(data.getStringExtra("AUDIOFILE"));
			Toast.makeText(getApplicationContext(), "Audio Saved Successfully",
					Toast.LENGTH_SHORT).show();
			upLoadMultimedia(audioFile, Constants.AUDIO, "");
		} else if (requestcode == TAKE_PICTURE_REQUEST
				&& resultcode == RESULT_OK) {
			String picturePath = data.getStringExtra("picturepath");
			upLoadMultimedia(new File(picturePath), Constants.IMAGE, "");
			
		} else if (requestcode == TAKE_VIDEO_REQUEST && resultcode == RESULT_OK) {
			String videoPath = data
					.getStringExtra(Intents.EXTRA_VIDEO_FILE_PATH);
			upLoadMultimedia(new File(videoPath), Constants.VIDEO, "");
		}
		super.onActivityResult(requestcode, resultcode, data);
	}

	public void upLoadMultimedia(File file, int type, String message) {
		ControllerRequest request = new ControllerRequest(mContext,
				GMultimediaUploadController.ACTION_TASK_MULTIMEDIA_UPLOAD,
				new Object[] { "", message, file, new Integer(type) });
		request.setCallbackListener(uploadListener);
		Controller.executeAsync(request, GMultimediaUploadController.class);
	}
	
	private CallBackListener uploadListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			Toast.makeText(mContext, "File uploaded to the server",
					Toast.LENGTH_SHORT).show();
			
			GSurveyOption selectedOption = surveyDetail.getQnsAndResponses().get(currentPosition).getOptionsSelected().get(currentPosition);
			//get the url here and save
			//selectedOption.setOptionText(options);
			
		}

		public void onStart() {
		}

		public void onError(ControllerResponse response) {
			Toast.makeText(mContext, response.getErrorMessage(),
					Toast.LENGTH_SHORT).show();
		}
	};

}
