/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.glance.augmented.app;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glance.R;
import com.glance.augmented.AugmentedApplicationControl;
import com.glance.augmented.AugmentedApplicationException;
import com.glance.augmented.AugmentedApplicationSession;
import com.glance.augmented.app.VideoPlayerHelper.MEDIA_STATE;
import com.glance.augmented.utils.AugmentedApplicationGLView;
import com.glance.augmented.utils.LoadingDialogHandler;
import com.glance.augmented.utils.Texture;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TargetFinder;
import com.qualcomm.vuforia.TargetSearchResult;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;





public class ImageTargets extends Activity implements AugmentedApplicationControl
{
    private static final String LOGTAG = "ImageTargets";
    
    AugmentedApplicationSession vuforiaAppSession;
    
    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    
    // Our OpenGL view:
    private AugmentedApplicationGLView mGlView;
    
    // Our renderer:
    private ImageTargetRenderer mRenderer;
    
    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    
    
    private boolean mContAutofocus = false;
  
    private RelativeLayout mUILayout;
      
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
        
 // Movie for the Targets:
    public static final int NUM_TARGETS = 1;
    public static final int STONES = 0;
    public static final int CHIPS = 1;
    private VideoPlayerHelper mVideoPlayerHelper[] = null;
    private int mSeekPosition[] = null;
    private boolean mWasPlaying[] = null;
    private String mMovieName[] = null;
    
    private TextView textView;
    
    static final int INIT_SUCCESS = 2;
    static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
    static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
    static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
    static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
    static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
    static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
    static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
    static final int UPDATE_ERROR_UPDATE_SDK = -6;
    static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
    static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;
    
    static final int HIDE_LOADING_DIALOG = 0;
    static final int SHOW_LOADING_DIALOG = 1;
    
    private static final String kAccessKey = "869a299f9911cd84f189d69fe8d5f79f35304372";
    private static final String kSecretKey = "ad4a7110ad50100b22474f166d7ef4f5b3887a30";
    
    // Error message handling:
    private int mlastErrorCode = 0;
    private int mInitErrorCode = 0;
    private boolean mFinishActivityOnError;
    
    
    // Called when the activity first starts or the user navigates back to an
    // activity.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        vuforiaAppSession = new AugmentedApplicationSession(this);
        
        startLoadingAnimation();
        mDatasetStrings.add("MyTest.xml");
        
        vuforiaAppSession
            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        
        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();
    
        
        
        mVideoPlayerHelper = new VideoPlayerHelper[NUM_TARGETS];
        mSeekPosition = new int[NUM_TARGETS];
        mWasPlaying = new boolean[NUM_TARGETS];
        mMovieName = new String[NUM_TARGETS];
        
        // Create the video player helper that handles the playback of the movie
        // for the targets:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            mVideoPlayerHelper[i] = new VideoPlayerHelper();
            mVideoPlayerHelper[i].init();
            mVideoPlayerHelper[i].setActivity(this);
        }
        
        mMovieName[STONES] = "VideoPlayback/Fit4Life_Anthem.mp4";
        
    }
    
   
    
    
 // We want to load specific textures from the APK, which we will later
    // use for rendering.
    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk(
            "VideoPlayback/LG_G2_poster.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk(
            "VideoPlayback/VuforiaSizzleReel_2.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/play.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/busy.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VideoPlayback/error.png",
            getAssets()));
		mTextures.add(Texture.loadTextureFromApk("offer.png",
            getAssets()));
		
       
    }
    
    
    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();
        
       
        
        try
        {
            vuforiaAppSession.resumeAR();
        } catch (AugmentedApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
        
    }
    
    
    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        
        //vuforiaAppSession.onConfigurationChanged();
    }
    
    
    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
        
       
        
        
     // Store the playback state of the movies and unload them:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // If the activity is paused we need to store the position in which
            // this was currently playing:
            if (mVideoPlayerHelper[i].isPlayableOnTexture())
            {
                mSeekPosition[i] = mVideoPlayerHelper[i].getCurrentPosition();
                mWasPlaying[i] = mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING ? true
                    : false;
            }
            
            // We also need to release the resources used by the helper, though
            // we don't need to destroy it:
            if (mVideoPlayerHelper[i] != null)
                mVideoPlayerHelper[i].unload();
        }
        
        try
        {
            vuforiaAppSession.pauseAR();
        } catch (AugmentedApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
    }
    

    
    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // If the activity is destroyed we need to release all resources:
            if (mVideoPlayerHelper[i] != null)
                mVideoPlayerHelper[i].deinit();
            mVideoPlayerHelper[i] = null;
        }
        
        try
        {
            vuforiaAppSession.stopAR();
        } catch (AugmentedApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Unload texture:
        mTextures.clear();
        mTextures = null;
        
        System.gc();
    }
    
    public void deinitCloudReco()
    {
        // Get the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        if (imageTracker == null)
        {
            Log.e(LOGTAG,
                "Failed to destroy the tracking data set because the ImageTracker has not"
                    + " been initialized.");
            return;
        }
        
        // Deinitialize target finder:
        TargetFinder finder = imageTracker.getTargetFinder();
        finder.deinit();
    }
    
    
    // Initializes AR application components.
    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        mGlView = new AugmentedApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);
        
        mRenderer = new ImageTargetRenderer(this, vuforiaAppSession);
        mRenderer.setTextures(mTextures);
       
        
        // The renderer comes has the OpenGL context, thus, loading to texture
        // must happen when the surface has been created. This means that we
        // can't load the movie from this thread (GUI) but instead we must
        // tell the GL thread to load it once the surface has been created.
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
            mRenderer.requestLoad(i, mMovieName[i], 0, true);
        }
        
        mGlView.setRenderer(mRenderer);
        
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            float[] temp = { 0f, 0f };
            mRenderer.targetPositiveDimensions[i].setData(temp);
            mRenderer.videoPlaybackTextureID[i] = -1;
        }
        
    }
    
    private void startLoadingAnimation()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay,
            null, false);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        
        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
            .findViewById(R.id.loading_indicator);
        
        // Shows the loading indicator at start
        loadingDialogHandler
            .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
        
        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
        textView = (TextView) findViewById(R.id.textView1);
        
    }
    
    public void setStatusTextViewVisiblity(final boolean visible) {
   	 runOnUiThread(new Runnable()
   	 {
   		 public void run()
   		 {
   			 if (visible) {
   				 textView.setVisibility(View.VISIBLE);
   			 } else {
   				 textView.setVisibility(View.GONE);
   			 }
   		 }
   	 });
   	
   }
    
    
    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData()
    {
    	Log.d(LOGTAG, "initCloudReco");

    	// Get the image tracker:
    	TrackerManager trackerManager = TrackerManager.getInstance();
    	ImageTracker imageTracker = (ImageTracker) trackerManager
    			.getTracker(ImageTracker.getClassType());
    	
    	
    	{
    		
    		 if (imageTracker == null)
    	            return false;
    	        
    	        if (mCurrentDataset == null)
    	            mCurrentDataset = imageTracker.createDataSet();
    	        
    	        if (mCurrentDataset == null)
    	            return false;
    	        
    	        if (!mCurrentDataset.load(
    	            mDatasetStrings.get(mCurrentDatasetSelectionIndex),
    	            DataSet.STORAGE_TYPE.STORAGE_APPRESOURCE))
    	            return false;
    	        
    	        if (!imageTracker.activateDataSet(mCurrentDataset))
    	            return false;
    	        
    	        int numTrackables = mCurrentDataset.getNumTrackables();
    	        for (int count = 0; count < numTrackables; count++)
    	        {
    	            Trackable trackable = mCurrentDataset.getTrackable(count);
    	            String name = "Current Dataset : " + trackable.getName();
    	            trackable.setUserData(name);
    	            Log.d(LOGTAG, "UserData:Set the following user data "
    	                + (String) trackable.getUserData());
    	        }
    	}
    	

    	// Initialize target finder:
    	TargetFinder targetFinder = imageTracker.getTargetFinder();

    	// Start initialization:
    	if (targetFinder.startInit(kAccessKey, kSecretKey))
    	{
    		targetFinder.waitUntilInitFinished();
    	}

    	int resultCode = targetFinder.getInitState();
    	if (resultCode != TargetFinder.INIT_SUCCESS)
    	{
    		if(resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION)
    		{
    			mInitErrorCode = UPDATE_ERROR_NO_NETWORK_CONNECTION;
    		}
    		else
    		{
    			mInitErrorCode = UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
    		}

    		Log.e(LOGTAG, "Failed to initialize target finder.");
    		return false;
    	}

    	// Use the following calls if you would like to customize the color of
    	// the UI
    	// targetFinder->setUIScanlineColor(1.0, 0.0, 0.0);
    	// targetFinder->setUIPointColor(0.0, 0.0, 1.0);

    	return true;
    }
    
    
    @Override
    public boolean doUnloadTrackersData()
    {
    	return true;
    }
    
//    public boolean onKeyUp(int keyCode, KeyEvent event)
//	{
////	    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
////	    {
////
////
////	    	if(event.getAction() == KeyEvent.ACTION_UP){
////	    		displaySpeechRecognizer();
////				return true;
////	    	}
////	    }
//
//	    if (keyCode == 4)
//	    {	
//	    	 Log.e(LOGTAG, "@ onKeyUp before stopAR");
//	    	 try
//	         {
//	             vuforiaAppSession.stopAR();
//	         } catch (AugmentedApplicationException e)
//	         {
//	             Log.e(LOGTAG, e.getString());
//	         }
//	         
//	         // Unload texture:
//	         mTextures.clear();
//	         mTextures = null;
//	         Log.e(LOGTAG, "@ onKeyUp before killProcess");
//	         android.os.Process.killProcess(android.os.Process.myPid());
//			return true;
//	    }
//
//	    return false;
//	} 
//    
    
    @Override
    public void onInitARDone(AugmentedApplicationException exception)
    {
        
        if (exception == null)
        {
            initApplicationAR();
            
            mRenderer.mIsActive = true;
            
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
            
            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();
            
            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            
            try
            {
                vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
            } catch (AugmentedApplicationException e)
            {
                Log.e(LOGTAG, e.getString());
            }
            
            boolean result = CameraDevice.getInstance().setFocusMode(
                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
            
            if (result)
                mContAutofocus = true;
            else
                Log.e(LOGTAG, "Unable to enable continuous autofocus");
            
          
            Log.d(LOGTAG, "!@ onInitARDone");
        } else
        {
            Log.e(LOGTAG, exception.getString());
            finish();
        }
    }
    
    
    @Override
    public void onQCARUpdate(State state)
    {

        // Get the tracker manager:
        TrackerManager trackerManager = TrackerManager.getInstance();
        
        // Get the image tracker:
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        // Get the target finder:
        TargetFinder finder = imageTracker.getTargetFinder();
        
        // Check if there are new results available:
        final int statusCode = finder.updateSearchResults();
        
        // Show a message if we encountered an error:
        if (statusCode < 0)
        {
            
            boolean closeAppAfterError = (
                statusCode == UPDATE_ERROR_NO_NETWORK_CONNECTION ||
                statusCode == UPDATE_ERROR_SERVICE_NOT_AVAILABLE);
            
        } else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE)
        {
            // Process new search results
            if (finder.getResultCount() > 0)
            {
                TargetSearchResult result = finder.getResult(0);
                
                // Check if this target is suitable for tracking:
                if (result.getTrackingRating() > 0)
                {
                    Trackable trackable = finder.enableTracking(result);
                    
                }
            }
        }
    
    }
    
    
    @Override
    public boolean doInitTrackers()
    {
    	 TrackerManager tManager = TrackerManager.getInstance();
         Tracker tracker;
         
         // Indicate if the trackers were initialized correctly
         boolean result = true;
         
         tracker = tManager.initTracker(ImageTracker.getClassType());
         if (tracker == null)
         {
             Log.e(
                 LOGTAG,
                 "Tracker not initialized. Tracker already initialized or the camera is already started");
             result = false;
         } else
         {
             Log.i(LOGTAG, "Tracker successfully initialized");
         }
         
         return result;
    }
    
    
    @Override
    public boolean doStartTrackers()
    {
    	 // Indicate if the trackers were started correctly
        boolean result = true;
        
        // Start the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        imageTracker.start();
        
        // Start cloud based recognition if we are in scanning mode:
        TargetFinder targetFinder = imageTracker.getTargetFinder();
        targetFinder.startRecognition();
        
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers()
    {
    	 // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        if(imageTracker != null)
        {
            imageTracker.stop();
            
            // Stop cloud based recognition:
            TargetFinder targetFinder = imageTracker.getTargetFinder();
            targetFinder.stop();
            
            // Clears the trackables
            targetFinder.clearTrackables();
        }
        else
        {
            result = false;
        }
        
        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers()
    {
    	 // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ImageTracker.getClassType());
        
        return result;
    }
    
    
    
    
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;

}
