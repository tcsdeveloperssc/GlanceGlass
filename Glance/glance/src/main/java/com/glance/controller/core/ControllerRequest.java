package com.glance.controller.core;

import android.content.Context;


/*
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
 
/**
 * @author 578107hmb
 * 
 */
public final class ControllerRequest {

	private String mAction;
	private CallBackListener mListener;
	private Object mRequestTag;
	private Object[] mActionParams;
	private Context mContext;
	private int status;

	public ControllerRequest(Context context) {
		this(context, null, null, null);
	}

	public ControllerRequest(Context context, String actionName) {
		this(context, actionName, null, null);
	}

	public ControllerRequest(Context context, String actionName, Object[] params) {
		this(context, actionName, params, null);
	}

	public ControllerRequest(Context context, String actionName,
			Object[] params, CallBackListener listener) {
		this(context, actionName, params, listener, null);
	}

	public ControllerRequest(Context context, String actionName,
			Object[] params, CallBackListener listener, Object requestTag) {
		mContext = context;
		mAction = actionName;
		mActionParams = params;
		mListener = listener;
		mRequestTag = requestTag;
	}

	public String getActionName() {
		return mAction;
	}

	public CallBackListener getCallbackListener() {
		return mListener;
	}

	public void setCallbackListener(CallBackListener listener) {
		mListener = listener;
	}

	public Object[] getActionParams() {
		return mActionParams;
	}

	public Object getRequestTag() {
		return mRequestTag;
	}

	public void setRequestTag(Object mRequestTag) {
		this.mRequestTag = mRequestTag;
	}

	public Context getContext() {
		return mContext;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
