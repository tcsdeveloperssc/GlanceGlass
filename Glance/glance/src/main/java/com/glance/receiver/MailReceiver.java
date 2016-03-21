package com.glance.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.glance.controller.core.CallBackListener;
import com.glance.utils.Constants.Keywords;

public class MailReceiver extends BroadcastReceiver{
	CallBackListener listener;
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// Get extra data included in the Intent
					if(null != intent){
						if(Keywords.SUCCESS == intent.getStringExtra(Keywords.DATA)){
							
							if (listener != null)
							listener.onSuccess(null);
						}else{
							// Do SOmething else
						}
					}
		
	}
	public void setListener (CallBackListener listener){
		this.listener = listener;
	}
	
	
	
}