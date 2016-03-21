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
package com.glance.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.glance.R;
import com.glance.bean.model.ResponseBean;
import com.glance.bean.model.StatusBean;
import com.glance.utils.DeviceManager;
import com.glance.utils.GSONUtils;
import com.glance.utils.Utils;
import com.glance.utils.Utils.GLog;

/**
 * @author 528937vnkm
 *
 */
public class HttpConnector {
	
	// Log Tag
	private static final String TAG = "HttpConnector";
	// Http Connection TimeOut
	public static final int TIMEOUT_2 = 30 * 1000; // 30 Seconds
	// Http Connection TimeOut
	public static final int TIMEOUT_4 = 1 * 60000; // 4 Minutes
	// Context
	private Context context;
	// ServiceBundle containing ServiceCall Data
	private GServiceBundle serviceBundle;
	// Generic HttpUriRequest
	private HttpUriRequest httpUriRequest;
	
	/**
	 * Constructor with Single argument
	 * @param context
	 * **/
	public HttpConnector(Context context){
		this.context = context;
	}

	/**
	 * Function to make Connection
	 * 
	 * **/
	public ResponseBean makeConnection(GServiceBundle _serviceBundle) throws JSONException{
		
		this.serviceBundle = _serviceBundle;
		
		
		URL url = null;
		URI uri = null;
		try {
			url = new URL(serviceBundle.getServiceUrl());
			uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		
		ResponseBean responseBean = new ResponseBean();
		
		try {
			switch (serviceBundle.getConnectionType()) {
			
			case GServiceBundle.POST:
					
				httpUriRequest = new HttpPost(serviceBundle.getServiceUrl());
				
				 break;
			case GServiceBundle.GET:
				
				httpUriRequest = new HttpGet(uri);
				 
				 break;
			case GServiceBundle.PUT:
				
				httpUriRequest = new HttpPut(uri);
				
				 break;
			case GServiceBundle.DELETE:
				
				httpUriRequest = new HttpDelete(serviceBundle.getServiceUrl());
				
				 break;
			default:
				
				httpUriRequest = new HttpPost(serviceBundle.getServiceUrl());
				
				 break;
			}
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			responseBean.setStatusBean(new StatusBean(103, StatusBean.ERROR, e1.getMessage()));
			return responseBean;
		}
		
		httpUriRequest.addHeader("Accept", "application/json");
		httpUriRequest.addHeader("Content-Type"," text/plain; charset=UTF-8");
		
		if(serviceBundle.isUserCredentialsRequired())
		{
			SharedPreferences pref = Utils.getCredentials(context);
			String t =pref.getString(context.getString(R.string.preference_security_token), "");
			String s = t;
			httpUriRequest.addHeader("securityToken",pref.getString(context.getString(R.string.preference_security_token), ""));
		}
		
		StringEntity stringEntity = null;
		if(serviceBundle.getRequestObject() != null 
				&& serviceBundle.getRequestObject() instanceof JSONObject){
			//Throws unsupported encoding exception
			try {
				
				stringEntity = new StringEntity(serviceBundle.getRequestObject().toString());
				
				/**
				 * HttpDelete request will not take any request with Body
				 * 
				 * Reference in case : http://stackoverflow.com/questions/3773338/httpdelete-with-body
				 * **/
				
				if(stringEntity != null && !HttpDelete.METHOD_NAME.equalsIgnoreCase(httpUriRequest.getMethod())){
					((HttpEntityEnclosingRequest)httpUriRequest).setEntity(stringEntity);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block // Ignore for now
				e.printStackTrace();
				responseBean.setStatusBean(new StatusBean(103, StatusBean.ERROR, e.getMessage()));
				return responseBean;
			}
		}
		
		responseBean = triggerHttpRequest();
		
		return responseBean;
	}
	
	
	private ResponseBean triggerHttpRequest(){
		
		ResponseBean httpResponseBean = new ResponseBean();

		if(!DeviceManager.isNetworkAvailable(context)){
			httpResponseBean.setStatusBean(new StatusBean(103, StatusBean.ERROR, context.getString(R.string.no_network)));
			return httpResponseBean;
		}
		
		HttpResponse response = null;
		
		DefaultHttpClient httpclient = new GCoreHttpClient();
		
		try {
			
			//Check for interruption(1) this will mean interruption before parameters for service execution are set.
			if(Thread.interrupted()){
				throw new InterruptedException("Execution cancelled at (1)");
			}
			
			HttpParams httpParams = null;
			
			httpParams = httpclient.getParams();
			
			HttpConnectionParams.setConnectionTimeout(httpParams,serviceBundle.getServiceTimeOutInMillis());
			HttpConnectionParams.setSoTimeout(httpParams,serviceBundle.getServiceTimeOutInMillis());
			
			GLog.d("TCS", ">>>> Request Object -> "+serviceBundle.getRequestObject()+"\n>>>> Connecting to >>>>> "+httpUriRequest.getURI());
			
			//Check for interruption(2) this will mean interruption before service execution starts.
			if(Thread.interrupted()){
				throw new InterruptedException("Execution cancelled at (2)");
			}
			
			response = (HttpResponse) httpclient.execute(httpUriRequest);
			
			//Check for interruption(3) this will mean interruption after service execution is completed.
			if(Thread.interrupted()){
				throw new InterruptedException("Execution cancelled at (3)");
			}

			//Status Check. Check if the connection with server was successful
			final int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) { 
				httpResponseBean = processErrorOutput(response);
				
				return httpResponseBean;
			}

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				
				// convert content stream to a String
				String resultString= convertInputStreamToString(instream);
				
				GLog.d("TCS", "**** Response for **** "+ serviceBundle.getServiceUrl());
				GLog.d("TCS", "**** Result String **** "+ resultString);
				
				//Fill the response Object
				if(Utils.isValidString(resultString)){
					httpResponseBean.setResponse(resultString);
					httpResponseBean.setStatusBean(new StatusBean(statusCode, StatusBean.SUCCESS, "OK"));
					httpResponseBean = processOutput(httpResponseBean);
				}else{
					httpResponseBean.setResponse(resultString);
					httpResponseBean.setStatusBean(new StatusBean(101, StatusBean.ERROR, "Invalid data."));
				}
				instream.close();

				//Check for interruption(4) this will mean interruption after service execution and extraction of response are completed.
				if(Thread.interrupted()){
					throw new InterruptedException("Execution cancelled at (4)");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			httpResponseBean.setResponse(null);
			httpResponseBean.setStatusBean(new StatusBean(101, StatusBean.ERROR, context.getString(R.string.unable_to_fetch_data)));
			
		} finally{
			GLog.d("TCS", "Response below for ** "+ httpUriRequest.getURI());
			Log.d("TCS", "HTTP***** "+ response);
			if(response != null){
				Log.d("TCS", "HTTP***** "+ response.getStatusLine());
				Log.d("TCS", "HTTP***** "+ response.getStatusLine().getStatusCode());
			}
			//TODO Check the position of this code
			httpclient.getConnectionManager().shutdown();
		}

		return httpResponseBean;
	}
	
	
	/**
	 * @param response 
	 * 
	 */
	private ResponseBean processErrorOutput(HttpResponse response) {
		ResponseBean rBean = new ResponseBean();
		
		HttpEntity entity = response.getEntity();
		String errResponseString = null;
		JSONObject errObject = null;
		
		final int statusCode = response.getStatusLine().getStatusCode();
		
		final String reasonPhrase = response.getStatusLine().getReasonPhrase();
		
		try {	
			//Status Check. Check if the connection with server was successful
			
			
			if(statusCode == HttpStatus.SC_NO_CONTENT){
				rBean.setStatusBean(new StatusBean(HttpStatus.SC_NO_CONTENT, StatusBean.ERROR, context.getString(R.string.no_content)));
				return rBean;
			}
			else{
				    rBean.setStatusBean(new StatusBean(statusCode, StatusBean.ERROR, context.getString(R.string.unable_to_fetch_data)));
					if(entity != null){
						errResponseString = convertInputStreamToString(entity.getContent());
						GLog.d(TAG, "*** Error Body *** "+ errResponseString);
						
						errObject = new JSONObject(errResponseString);
					
						String errCodeS = errObject.optString("code");
						int errCode = -1;
						
						// Sample Error Code : COMM_303
						
						if(!TextUtils.isEmpty(errCodeS)){
							errCode = Integer.parseInt(errCodeS.substring(errCodeS.indexOf("_")+1, errCodeS.length()));
						}
						
						if(errCode == -1){
							errCode = statusCode;
						}
						
						rBean.setStatusBean(new StatusBean(errCode, StatusBean.ERROR, errObject.optString("message")));
						return rBean;
					}
				}
				
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rBean.setStatusBean(new StatusBean(statusCode, StatusBean.ERROR, "Invalid response"));
			}
			catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rBean.setStatusBean(new StatusBean(statusCode, StatusBean.ERROR, reasonPhrase));
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rBean.setStatusBean(new StatusBean(statusCode, StatusBean.ERROR, reasonPhrase));
			} 
		
		return rBean;
	}

	
	/**
	 * @param responseBean 
	 * 
	 */
	private ResponseBean processOutput(ResponseBean responseBean){
		if(responseBean.getStatusBean().isSuccess()){
			
			//TODO Check for interruption 2 this will mean interruption after service execution is completed
			
			String responseString = (String) responseBean.getResponse();
			
			GLog.d(TAG, "**** RAW DATA **** \n"+ responseString);
			
			if(Utils.isValidString(responseString)){
									
				
				
				if(serviceBundle.getResponseType().equals(String.class)){
					Object response=responseString;
					responseBean.setResponse(response);
				}
				else{
					Object response = null;
					try {
						response = GSONUtils.getInstance().fromJson(responseString, serviceBundle.getResponseType());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					GLog.d("TCS", "**** Successfully Transformed to ResponseType Object **** "+ response.getClass());
					
					responseBean.setResponse(response);
				}
				
			}else{
				responseBean.setResponse(null);
				responseBean.setStatusBean(new StatusBean(101, "error", "Request failed"));
			}
		}
		return responseBean;
	}
	
	private String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream), 4096);
		String line;
		StringBuilder sb =  new StringBuilder();

		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		rd.close();
		return sb.toString();

	}
	
	
}

