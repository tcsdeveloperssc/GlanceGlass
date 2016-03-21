/**
Copyright (c) 2013, TATA Consultancy Services Limited (TCSL)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of TCSL nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

/**
13-Dec-2013
528937vnkm
Class Description
**/
package com.glance.faye;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.glance.controller.core.CallBackListener;
import com.glance.utils.Constants;
import com.glance.utils.Utils.GLog;

public class MyService extends Service
{
    protected AudioManager mAudioManager; 
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    protected boolean mIsListening;
    protected volatile boolean mIsCountDownOn;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;
    
    static final String TAG = "MyService";
    
    private CallBackListener speechListener;

	public CallBackListener getSpeechListener() {
		return speechListener;
	}
	public void setSpeechListener(CallBackListener speechListener) {
		this.speechListener = speechListener;
	}


    @Override
    public void onCreate()
    {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                         RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                                         this.getPackageName());
        Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
        try {
			mServerMessenger.send(message);
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
    }
    
    protected static class IncomingHandler extends Handler
    {
        private WeakReference<MyService> mtarget;

        IncomingHandler(MyService target)
        {
            mtarget = new WeakReference<MyService>(target);
        }


        @Override
        public void handleMessage(Message msg)
        {
            final MyService target = mtarget.get();
            
            Log.d("TCS", "************************* Got Message ************************ "+ msg.what);
            
            switch (msg.what)
            {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        // turn off beep sound  
                    	if(null != target && null!= target.mAudioManager){
                    		target.mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    	}
                    }
                    
                    Log.d("TCS", "**target.mIsListening******* "+ target.mIsListening);
                    
                     if (!target.mIsListening)
                     {
                         target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                         target.mIsListening = true;
                        Log.d(TAG, "message start listening"); //$NON-NLS-1$
                     }
                     break;

                 case MSG_RECOGNIZER_CANCEL:
                      target.mSpeechRecognizer.cancel();
                      target.mIsListening = false;
                      Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
                      break;
             }
       } 
    } 

    // Count down timer for Jelly Bean work around
    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000)
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try
            {
                mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {

            }
        }
    };
    
    @Override
    public void onDestroy()
    {
        stopSelf();
        GLog.d("TCS", "************ Speech Service Stopped ************** ");
        
        if (mIsCountDownOn)
        {
            mNoSpeechCountDown.cancel();
        }
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
        
        super.onDestroy();
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            // speech input will be processed, so there is no need for count down anymore
        	Log.d("TCS", "**** Begin Speech ***** ");
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }               
            Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
        	Log.d("TCS", "**** onEndOfSpeech ***** ");
         }

        @Override
        public void onError(int error)
        {
        	
        	Log.d("TCS", "**** onError ***** ");
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
             mIsListening = false;
             Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
             try
             {
                    mServerMessenger.send(message);
             }
             catch (RemoteException e)
             {

             }
            Log.d(TAG, "error = " + error); //$NON-NLS-1$
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
        	Log.d("TCS", "**** onReadyForSpeech ***** ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true; // original
                mIsCountDownOn = false; // mine
//                mNoSpeechCountDown.start();// original
//                mNoSpeechCountDown.start(); mine
                if(null != mAudioManager){
                	mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                }
            }
            Log.d("MYSERVICESPEECH", "onReadyForSpeech"); //$NON-NLS-1$
        }
        
        @Override
        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults"); //$NON-NLS-1$
        	Log.d("TCS", "**** onResults ***** ");
        	ArrayList<String> strlist = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        	Log.d("TCS", "***** SPeech Results ****** "+ strlist);
        	
        	final Intent intent = new Intent(Constants.SPEECH_INTENT);
			intent.putExtra(Constants.SPEECH_ARRAY, strlist);
			GLog.d("SPEECH_SERVICE", "Broadcasting..");
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        	
        	 Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
             try
             {
                    mServerMessenger.send(message);
                    message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                    mServerMessenger.send(message);
             }
             catch (RemoteException e)
             {

             }
        	
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {

        }

    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
 