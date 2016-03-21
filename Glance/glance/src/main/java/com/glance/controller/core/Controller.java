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
 */
package com.glance.controller.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.glance.R;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.services.GServiceBundle;
import com.glance.services.HttpConnector;
import com.glance.utils.Constants.ErrorCodes;
import com.glance.utils.DeviceManager;


/**
 * @author 578107hmb
 * 
 *         Abstract class which serves as the base class of custom controllers.
 *         This class triggers the execution of derived controller classes in an
 *         asynchronous manner providing the status of execution. This class is
 *         also capable of posting the results to UI, Cancel an execution, Queue
 *         multiple executions, Executions in parallel and serial and lot many
 *         things.
 */
public abstract class Controller {

	/* Public constants */

	/**
	 * Request not found.
	 */
	public static final int STATUS_NOT_FOUND = 0;
	/**
	 * Request is Queued for execution.
	 */
	public static final int STATUS_QUEUED = 1;
	/**
	 * Request Execution has started.
	 */
	public static final int STATUS_STARTED = 2;
	/**
	 * Request Execution is progressing.
	 */
	public static final int STATUS_PROGRESSING = 3;
	/**
	 * Retrying the execution since some error has happened.
	 */
	public static final int STATUS_RETRY = 4;
	/**
	 * Request Execution failed. This will be mostly because of controller side
	 * error, so derived classes should handle this.
	 */
	public static final int STATUS_FAILED = 5;
	/**
	 * Request execution completed with some error.
	 */
	public static final int STATUS_ERROR = 6;
	
	
	/**
	 * Request Execution has completed successfully.
	 */
	public static final int STATUS_SUCCESS = 7;
	
	/**
	 * Request execution completed with some error.
	 */
	public static final int STATUS_USER_NOT_PRIVILAGED = 8;

	/* Private Constants */
	private static final int MAX_THREADS_IN_PARALLEL_MODE = 7;
	private static final int MAX_RETRY_COUNT = 3;

	/* Static Members */
	private static Map<String, ExecuteContoller> controllerRequests;
	private static ExecutorService mExecutorService;

	static {
		controllerRequests = new ConcurrentHashMap<String, Controller.ExecuteContoller>();
		mExecutorService = Executors
				.newFixedThreadPool(MAX_THREADS_IN_PARALLEL_MODE);
	}

	/* Members */
	protected Context mContext;
	protected ControllerRequest mRequest;
	protected CallBackListener mListener;
	protected int mRetryCount;

	/* Private Members */
	private Handler mHandler;
	private int mProgress;
	private Method methodInfo;

	/* Runnable */
	private Runnable cascadeStartRunnable;
	private Runnable cascadeProgressRunnable;
	private Runnable cascadeResultRunnable;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public Controller(Context context) {
		mContext = context;
		mHandler = new Handler(Looper.getMainLooper());
	}

	/**
	 * Executes a controller request asynchronously
	 * 
	 * @param request
	 *            The Controller request to be executed
	 * @param controllerType
	 *            Which controller class should execute this request.
	 * @return the status of the request.
	 */
	public static <T extends Controller> int executeAsync(
			ControllerRequest request, Class<T> controllerType) {
		return executeAsync(request, controllerType, null);
	}

	/**
	 * Executes a controller request asynchronously
	 * 
	 * @param request
	 *            The Controller request to be executed
	 * @param controllerType
	 *            Which controller class should execute this request.
	 * @param requestTraceTag
	 *            A unique string to identify the request. This tag can be used
	 *            later on to enquire the status, for cancelling execution etc.
	 * @return The status of the request. If the request is already there in the
	 *         queue or is processing, the request will not be taken in again.
	 */
	public static <T extends Controller> int executeAsync(
			ControllerRequest request, Class<T> controllerType,
			String requestTraceTag) {

		ExecuteContoller exeController = null;

		if (!TextUtils.isEmpty(requestTraceTag)) {
			exeController = controllerRequests.get(requestTraceTag);

			if (exeController != null) {
				int status = exeController.getRequest().getStatus();
				if (status != STATUS_SUCCESS || status != STATUS_FAILED) {
					return status;
				}
			}
		}
		exeController = new ExecuteContoller(request, controllerType,
				requestTraceTag);
		request.setStatus(STATUS_QUEUED);

		if (!TextUtils.isEmpty(requestTraceTag)) {
			controllerRequests.put(requestTraceTag, exeController);
		}

		mExecutorService.execute(exeController);

		return STATUS_QUEUED;
	}

	/**
	 * Gets the request status
	 * 
	 * @param requestTraceTag
	 *            The tag to identify the request.
	 * @return The request status
	 */
	public static int getRequestStatus(String requestTraceTag) {
		ExecuteContoller exeController = controllerRequests
				.get(requestTraceTag);

		if (exeController != null) {
			return exeController.getRequest().getStatus();
		} else {
			return STATUS_NOT_FOUND;
		}
	}

	/**
	 * Cancels the request. There in no guarantee that the processing will be
	 * cancelled if it has started already. If required you can use
	 * cancelRequest method with forceCancel override to cancel it forcefully.
	 * 
	 * @param requestTraceTag
	 *            The tag to identify the request.
	 */
	public static void cancelRequest(String requestTraceTag) {
		cancelRequest(requestTraceTag, false);
	}

	/**
	 * Cancels the request
	 * 
	 * @param requestTraceTag
	 *            The tag to identify the request.
	 * @param forceCancel
	 *            if set true, the undertaking thread will be interrupted.
	 */
	public static void cancelRequest(String requestTraceTag, boolean forceCancel) {
		ExecuteContoller exeController = controllerRequests
				.get(requestTraceTag);

		if (exeController != null) {
			exeController.cancelExecution(forceCancel);
		}
	}

	/**
	 * This method helps to change or set the callback listener after the
	 * request has started.
	 * 
	 * @param requestTraceTag
	 *            The tag to identify the request.
	 * @param listener
	 *            The Callback listener to be set.
	 * @return Returns the callback listener setting is success or not.
	 */
	public static boolean setCallbackListener(String requestTraceTag,
			CallBackListener listener) {
		ExecuteContoller exeController = controllerRequests
				.get(requestTraceTag);

		if (exeController != null) {
			ControllerRequest request = exeController.getRequest();
			synchronized (request) {
				int status = request.getStatus();
				if (status != STATUS_SUCCESS || status != STATUS_FAILED) {
					Controller controller = exeController
							.getControllerInstance();
					request.setCallbackListener(listener);
					if (controller != null) {
						controller.setCallbackListener(listener);
					}
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * Sets the mode of Controller Execution
	 * 
	 * @param isParallel
	 *            Whether the execution should happen in sequential or parallel
	 *            way.
	 */
	public static void setExecutionMode(boolean isParallel) {
		synchronized (mExecutorService) {
			if (isParallel) {
				mExecutorService = Executors
						.newFixedThreadPool(MAX_THREADS_IN_PARALLEL_MODE);
			} else {
				mExecutorService = Executors.newSingleThreadExecutor();
			}
		}

	}

	/**
	 * Executes the request as a normal method call. Its recommended to use
	 * executeAsync method since this method may result in ANR.
	 * 
	 * @param request
	 *            The controller request to be executed.
	 */
	public final void execute(ControllerRequest request) {
		mRequest = request;
		setCallbackListener(mRequest.getCallbackListener());
		mRequest.setStatus(STATUS_STARTED);
		postStart();
		executeInternal();
	}

	private void setCallbackListener(CallBackListener listener) {
		mListener = mRequest.getCallbackListener();
	}

	/**
	 * This method will be called if the controller request doesn't contain any
	 * information regarding the action to perform.
	 * 
	 * @param params
	 *            The params as object array for processing which is passed from
	 *            the controller request.
	 */
	protected abstract void execute(Object[] params);

	private void executeInternal() {
		String actionName = mRequest.getActionName();
		Object[] params = mRequest.getActionParams();

		try {
			if (mRequest.getStatus() == STATUS_RETRY && methodInfo != null) {
				if (params != null) {
					methodInfo.invoke(this, params);
				} else {
					methodInfo.invoke(this);
				}
			} else {
				if (actionName == null || actionName.length() == 0) {
					execute(params);
				} else {
					if (params != null) {
						Class<?>[] paramTypes = new Class<?>[params.length];

						for (int i = 0; i < params.length; i++) {
							paramTypes[i] = params[i].getClass();
						}

						// methodInfo = getClass().getDeclaredMethod(actionName,
						// paramTypes);
						// methodInfo.setAccessible(true);
						methodInfo = getClass().getMethod(actionName,
								paramTypes);
						methodInfo.invoke(this, params);
					} else {
						// methodInfo =
						// getClass().getDeclaredMethod(actionName);
						// methodInfo.setAccessible(true);
						methodInfo = getClass().getMethod(actionName);
						methodInfo.invoke(this);
					}
				}
			}
		} catch (Exception e) {
			ControllerResponse response = new ControllerResponse(STATUS_FAILED,
					"Failed to execute", mRequest.getRequestTag());
			postResult(response);
			e.printStackTrace();
		}

	}

	/**
	 * This will intimate the UI that the request processing has started. No
	 * need to call this method from derived classes since its already been
	 * called from the base.
	 */
	private void postStart() {
		if (mListener == null) {
			return;
		}

		if (cascadeStartRunnable == null) {
			cascadeStartRunnable = new Runnable() {

				@Override
				public void run() {
					mListener.onStart();
				}
			};
		}
		mHandler.post(cascadeStartRunnable);
		// TODO : Need to move the below status to appropriate position.
		mRequest.setStatus(STATUS_PROGRESSING);
	}

	/**
	 * This method post the progress to UI.
	 * 
	 * @param progress
	 *            The progress as integer.
	 */
	protected void postProgress(int progress) {
		mProgress = progress;

		if (mListener == null) {
			return;
		}

		if (cascadeProgressRunnable == null) {
			cascadeProgressRunnable = new Runnable() {

				@Override
				public void run() {
					mListener.onProgress(mProgress);
				}

			};
		}
		mHandler.post(cascadeProgressRunnable);
	}

	/**
	 * Posts the result to UI
	 * 
	 * @param response
	 *            Controller response to be posted.
	 */
	protected void postResult(final ControllerResponse response) {
		if (mListener == null) {
			return;  
		}

		// Commenting out this line since the result will remains same from
		// second time posting onwards since we are creating the instance with
		// first time Controller response only.
		// if (cascadeResultRunnable == null) {
		cascadeResultRunnable = new Runnable() {

			@Override
			public void run() {
				if (response.getStatus() == STATUS_SUCCESS) {
					mListener.onSuccess(response);
				} else {
					mListener.onError(response);
				}
			}
		};
		// }

		if (response.getStatus() != STATUS_SUCCESS
				&& mListener.shouldRetry(response)
				&& mRetryCount <= MAX_RETRY_COUNT) {
			mRequest.setStatus(STATUS_RETRY);
			mRetryCount++;
			executeInternal();
		} else {
			mHandler.post(cascadeResultRunnable);
		}
	}

	/**
	 * Derived classes can use this method to make web service calls.
	 * 
	 * @param bundle
	 *            The service bundle to be passed to make the service call.
	 * @return Returns the service response.
	 */
	protected ResponseBean fetchDataFromService(GServiceBundle bundle) {
		ResponseBean rBean = null;
		if (DeviceManager.isNetworkAvailable(mContext)) {
			HttpConnector connector = new HttpConnector(mContext);
			try {
				rBean = connector.makeConnection(bundle);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			rBean = new ResponseBean();
			rBean.setStatusBean(new StatusBean(ErrorCodes.NO_NETWORK,
					StatusBean.ERROR, mContext.getString(R.string.no_network)));
		}
		return rBean;
	}

	/**
	 * This is an Integration method which converts the response bean to
	 * controller response and post to UI.
	 * 
	 * @param rBean
	 *            Response bean to be posted.
	 */
	protected void postResultToUI(ResponseBean rBean) {
		ControllerResponse response = null;
		if (rBean.getStatusBean() != null) {
			if (rBean.getStatusBean().isSuccess()) {
				response = new ControllerResponse(STATUS_SUCCESS,
						rBean.getResponse(), mRequest.getRequestTag());
			} else {
				response = new ControllerResponse(STATUS_ERROR, rBean
						.getStatusBean().getStatusMessage(),
						mRequest.getRequestTag());
			}
		} else {
			response = new ControllerResponse(STATUS_SUCCESS,
					rBean.getResponse(), mRequest.getRequestTag());
		}

		postResult(response);
	}

	/**
	 * Method to post the result to UI
	 * 
	 * @param status
	 *            Whether it is Error or Success
	 * @param result
	 *            The result to be passed to UI. In UI, the end user can get
	 *            this result by calling getResult() of Controller Response.
	 */
	protected void postResultToUI(int status, Object result) {
		ControllerResponse response = new ControllerResponse(status, result,
				mRequest.getRequestTag());
		postResult(response);
	}

	/**
	 * Runnable class to Execute each controller Requests.
	 * 
	 * @author 578107hmb
	 * 
	 */
	private static class ExecuteContoller implements Runnable {

		private ControllerRequest mRequest;
		private Class<?> mControllerType;
		private String mRequestTraceTag;
		private boolean isCancelled;
		private Controller mControllerInstance;

		public ExecuteContoller(ControllerRequest request,
				Class<?> controllerType, String requestTraceTag) {
			mRequest = request;
			mControllerType = controllerType;
			mRequestTraceTag = requestTraceTag;
		}

		public void cancelExecution(boolean forceCancel) {
			isCancelled = true;
			if (forceCancel) {
				Thread.currentThread().interrupt();
			}
		}

		@Override
		public void run() {

			if (isCancelled) {
				return;
			}

			try {
				Context context = getRequest().getContext();

				mControllerInstance = (Controller) mControllerType
						.getConstructor(Context.class).newInstance(context);
				mControllerInstance.execute(getRequest());

				if (!TextUtils.isEmpty(mRequestTraceTag)) {
					controllerRequests.remove(mRequestTraceTag);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public ControllerRequest getRequest() {
			return mRequest;
		}

		public Controller getControllerInstance() {
			return mControllerInstance;
		}
	}
}
