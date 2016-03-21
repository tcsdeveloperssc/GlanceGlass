package com.glance.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.KeyEvent;

import com.glance.R;
import com.glance.bean.model.SubNodes;
import com.glance.utils.Utils;



public class LinkNodeActivity extends HotSpotActivity{
	private String prev_node = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		inLinkNodeActivity = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.link_node_activity);
		ArrayList<SubNodes> subNodeArray = getIntent().getParcelableArrayListExtra("subNodeArray");
		prev_node = getIntent().getStringExtra("prev_node");
		task_Id = getIntent().getStringExtra("task_id");
		story_Id = getIntent().getStringExtra("story_id");
		initialize(subNodeArray,getCurrentPageInLink());
		setupLayout(subNodeArray,HotSpotActivity.LINK_PAGER);
		mContext = LinkNodeActivity.this;
	}

	@Override
	public void onTap() {
		/*if (btakePicture){
			btakePicture = false;
			startActivity(cameraIntent);
		}
		else{*/
			if (Utils.getFromPreference(mContext, "OFFLINE_MODE").equalsIgnoreCase("true")){
				//open options menu in offlineHotspotActivity
			}
			else{
				displaySpeechRecognizer();
			}
//		}
	}
	
	
	@Override
	protected void onDestroy() {
		inLinkNodeActivity = false;
		super.onDestroy();
		}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		

		if (keyCode == 4)
		{
			sendOnlineStatusToSocket("hotspotnotrunning", "nil", null, null,
					null, null);

			if (isAudioPlayingStarted){
				killMediaPlayer();

			}
			else{

				if (Utils.getFromPreference(mContext, "OFFLINE_MODE").equalsIgnoreCase("false")){
					sendOnlineStatusToSocket("hotspotrunning", "nil", null,task_Id,story_Id,prev_node);
				}


				finish();

			}

			/*killMediaPlayer();
    		finish();*/


			return true;
		}
		return false;
	}




}
