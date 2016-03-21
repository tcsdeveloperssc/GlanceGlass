package com.glance.activity;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.glance.R;
import com.glance.database.DatabaseHelper;
import com.glance.utils.Constants.Keywords;
import com.glance.utils.Utils;

public class CameraActivity extends BaseActivity{

	private ImageView thumbNail = null;
	private String taskId;
	private File pictureFile;
	private ProgressBar pbCamera;
	private int CAMERA_PIC_REQUEST = 1;
	private String picturePath = null;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskId = getIntent().getStringExtra("TASK_ID");
		setContentView(R.layout.camera_layout);
		pbCamera = (ProgressBar) findViewById(R.id.pbCamera);
		Intent intent = new Intent(getApplicationContext(), ZoomActivity.class);
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mContext = CameraActivity.this;
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == RESULT_OK) {
				picturePath = data.getStringExtra("picturepath");
				Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
				thumbNail = (ImageView) findViewById(R.id.thumbnail);
				thumbNail.setImageBitmap(bitmap);
				savePicture();
			} else {
				finish();
			}

		}

	}

	public void savePicture() {

		pictureFile = new File(picturePath);

		if (pictureFile.exists()) {

			if (pbCamera != null)
				pbCamera.setVisibility(View.GONE);
			if (Utils.getFromPreference(mContext, "OFFLINE_MODE")
					.equalsIgnoreCase("true")) {
				if (Utils.databaseHelper == null)
					Utils.createDatabase(mContext);
				if ( Utils.getFromPreference ( mContext, Keywords.SUBMIT_STATUS).equalsIgnoreCase(Keywords.TRUE)) {
					Utils.databaseHelper.insertIntermediateImageForUpload(
							picturePath, taskId,
							DatabaseHelper.TABLE_FINAL_IMAGE_FOR_UPLOAD);
				} else {
					Utils.databaseHelper.insertIntermediateImageForUpload(
							picturePath, taskId,
							DatabaseHelper.TABLE_INTERMEDIATE_IMAGE_FOR_UPLOAD);
				}
				Toast.makeText(mContext,
						"Please sync in online mode to upload picture",
						Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(mContext, "TAP TO UPLOAD", Toast.LENGTH_SHORT)
						.show();
			}

		}

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

			if (event.getAction() == KeyEvent.ACTION_UP) {
				if (Utils.getFromPreference(mContext, "OFFLINE_MODE")
						.equalsIgnoreCase("false")) {
					Intent data = new Intent();
					data.putExtra(Keywords.FILE_PATH, picturePath);
					setResult(RESULT_OK, data);
					finish();

				}
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
	public void getUserTasks() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getMails() {
		// TODO Auto-generated method stub
		
	}



}