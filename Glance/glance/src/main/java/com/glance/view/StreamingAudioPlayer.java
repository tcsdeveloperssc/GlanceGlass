package com.glance.view;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.glance.R;
import com.glance.utils.Constants;

public class StreamingAudioPlayer extends Activity implements OnClickListener, OnTouchListener, OnCompletionListener, OnBufferingUpdateListener{
	
	private Button buttonPlayPause;
	private SeekBar seekBarProgress;
	public EditText editTextSongURL;
	
	private Runnable notification;
	
	private MediaPlayer mediaPlayer;
	private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class
	
	private final Handler handler = new Handler();
	
	String audioUrl = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audiolayout);
        
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (!bundle.isEmpty()) {
			if (bundle.containsKey(Constants.AUDIO_URL_KEY)) {
				audioUrl = bundle.getString(Constants.AUDIO_URL_KEY);
				System.out.println("videoUrl->"+audioUrl);
			//	videoUrl  = Utils.getVideoResourceUrl(VideoScreen.this,videoUrl);   /// For getting resource url. Remove this line after demo
			}
		}
        initView();
    }
    
    /** This method initialise all the views in project*/
    private void initView() {
		buttonPlayPause = (Button)findViewById(R.id.ButtonTestPlayPause);
		buttonPlayPause.setOnClickListener(this);
		
		seekBarProgress = (SeekBar)findViewById(R.id.SeekBarTestPlay);	
		seekBarProgress.setMax(99); // It means 100% .0-99
		seekBarProgress.setOnTouchListener(this);
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
	}

	/** Method which updates the SeekBar primary progress by current song playing position*/
    private void primarySeekBarProgressUpdater() {
    	seekBarProgress.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100)); // This math construction give a percentage of "was playing"/"song length"
		if (mediaPlayer.isPlaying()) {
			 notification = new Runnable() {
		        public void run() {
		        	primarySeekBarProgressUpdater();
				}
		    };
		    handler.postDelayed(notification,1000);
    	}
    }
	
	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
		// TODO Auto-generated method stub
		seekBarProgress.setSecondaryProgress(percent);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		buttonPlayPause.setText("play");
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		// TODO Auto-generated method stub

		if(v.getId() == R.id.SeekBarTestPlay){
			/** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
			if(mediaPlayer.isPlaying()){
		    	SeekBar sb = (SeekBar)v;
				int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
				mediaPlayer.seekTo(playPositionInMillisecconds);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v.getId() == R.id.ButtonTestPlayPause){
			
			try {
				mediaPlayer.setDataSource(audioUrl);//("http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3"); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
				mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer. 
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
			
			if(!mediaPlayer.isPlaying()){
				mediaPlayer.start();
				buttonPlayPause.setText("pause");
			}else {
				mediaPlayer.pause();
				buttonPlayPause.setText("play");
			}
			
			primarySeekBarProgressUpdater();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
                handler.removeCallbacks(notification);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    
	}
}

