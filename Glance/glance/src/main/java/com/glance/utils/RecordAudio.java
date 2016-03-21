package com.glance.utils;

import java.io.IOException;
import java.util.Calendar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.glance.R;
import com.glance.activity.BaseActivity;

public class RecordAudio extends BaseActivity {

	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;

	private TextView text;

	private boolean started = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_audio);

		text = (TextView) findViewById(R.id.text1);
		// store it to sd card
		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/"
				+ Calendar.getInstance().getTimeInMillis() + "glance.3gpp";

		myRecorder = new MediaRecorder();

		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myRecorder.setOutputFile(outputFile);
		myRecorder.setMaxDuration(6000);

	}

	public void start() {
		try {
			started = true;
			myRecorder.prepare();
			myRecorder.start();
		} catch (IllegalStateException e) {
			// start:it is called before prepare()
			// prepare: it is called after start() or before setOutputFormat()
			e.printStackTrace();
		} catch (IOException e) {
			// prepare() fails
			e.printStackTrace();
		}

		text.setText("Recording.Tap to stop");

	}

	public void stop() {
		try {
			myRecorder.stop();
			myRecorder.release();
			myRecorder = null;
			Intent i = new Intent();
			i.putExtra("AUDIOFILE", outputFile);
			setResult(RESULT_OK, i);
			finish();
		} catch (IllegalStateException e) {
			// it is called before start()
			e.printStackTrace();
		} catch (RuntimeException e) {
			// no valid audio/video data has been received
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(outputFile);
			myPlayer.prepare();
			myPlayer.start();

			text.setText("Recording Point: Playing");

			Toast.makeText(getApplicationContext(),
					"Start play the recording...", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopPlay() {
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				myPlayer.release();
				myPlayer = null;

				text.setText("Recording Point: Stop playing");

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				// displaySpeechRecognizer();
				if (!started) {
					start();
				} else {
					stop();

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