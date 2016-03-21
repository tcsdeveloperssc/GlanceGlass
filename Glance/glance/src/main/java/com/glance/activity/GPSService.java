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
 14-Nov-2013
 528937vnkm
 Class Description
 **/
package com.glance.activity;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.glance.bean.model.GpsModel;
import com.glance.controller.GsaveUserLocationController;
import com.glance.controller.core.CallBackListener;
import com.glance.controller.core.Controller;
import com.glance.controller.core.ControllerRequest;
import com.glance.controller.core.ControllerResponse;
import com.glance.gps.GLocationClient.GpsListener;
import com.glance.gps.GLocationManager;
import com.glance.utils.Utils.GLog;

public class GPSService extends IntentService {

	public static String LAT = "LAT";
	public static String LONG = "LONG";
	public static String ACCURANCY = "ACCURANCY";
	public static String TIME = "TIME";
	public static String TAG = "GPSService";

	private GLocationManager utils;
	GpsModel currentModel = null;
	static SharedPreferences settings;
	static SharedPreferences.Editor configEditor;

	CallBackListener callbackListener = new CallBackListener() {

		@Override
		public void onSuccess(ControllerResponse response) {
			Toast.makeText(getApplicationContext(), "Location updated",
					Toast.LENGTH_SHORT).show();

		}

		public void onError(ControllerResponse response) {

			GLog.d(TAG, "Problem in updating user location on server "
					+ response.getErrorMessage());
		};
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (currentModel != null) {
				configEditor.putString(LAT, ""
						+ currentModel.getCurrentLocation().getLatitude());
				configEditor.putString(LONG, ""
						+ currentModel.getCurrentLocation().getLongitude());
				configEditor.putString(ACCURANCY, ""
						+ currentModel.getCurrentLocation().getAccuracy());
				configEditor.putString(TIME, ""
						+ currentModel.getCurrentLocation().getTime());
				configEditor.commit();

				// update location in server
				String lat = String.valueOf(currentModel.getCurrentLocation()
						.getLatitude());
				String lon = String.valueOf(currentModel.getCurrentLocation()
						.getLongitude());

				ControllerRequest request = new ControllerRequest(
						GPSService.this,
						GsaveUserLocationController.SAVE_USER_LOCATION,
						new Object[] { lon, lat });
				request.setCallbackListener(callbackListener);
				Controller.executeAsync(request,
						GsaveUserLocationController.class);

			}
		}
	};

	public GPSService() {
		super("GPSService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		utils = new GLocationManager(this, listener);
		settings = this.getSharedPreferences("USER-LOCATION",
				MODE_WORLD_WRITEABLE);
		configEditor = settings.edit();

		utils.getCurrentLocation();

	}

	private void setCurrentLocation(GpsModel loc) {
		currentModel = loc;
	}

	GpsListener listener = new GpsListener() {

		@Override
		public void onStartLocating() {

		}

		@Override
		public void onLocationFound(GpsModel gpsModel) {

			if (gpsModel != null) {

				GLog.d(TAG, "location udated "
						+ gpsModel.getCurrentLocation().getLatitude() + " , "
						+ gpsModel.getCurrentLocation().getLongitude());
				setCurrentLocation(gpsModel);
				handler.sendEmptyMessage(0);
			}
		}

		@Override
		public void onError(String errMessage) {
			// TODO Auto-generated method stub
			Toast.makeText(
					getBaseContext(),
					"Location sharing setting is not on please enable gps and network location update to get User Location",
					Toast.LENGTH_LONG).show();

		}
	};
}
