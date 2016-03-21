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

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.glance.R;
import com.glance.bean.model.GpsModel;
import com.glance.gps.GLocationClient.GpsListener;
import com.glance.gps.GLocationManager;

public class GpsTestActivity extends BaseActivity{
	
	TextView textView, textView2;
	private Location baseLocation;
	// Accuracy to 85 mts : Cub 6305
	public static double lat = 10.0102051, lon = 76.3657923;
	private GLocationManager utils;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gps_test);
		
		baseLocation = new Location("GlanceBaseTo85");
		baseLocation.setLatitude(lat);
		baseLocation.setLongitude(lon);
		
		textView = (TextView)findViewById(R.id.txt);
		textView2 = (TextView)findViewById(R.id.txt2);
		
//		GLocationClient utils = new GLocationClient(this, listener);
		utils = new GLocationManager(this, listener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(null == utils){
			utils = new GLocationManager(this, listener);
		}
		utils.getCurrentLocation();
		mContext = GpsTestActivity.this;
	};
	
	GpsListener listener = new GpsListener() {
		
		@Override
		public void onStartLocating() {
			
			textView.setText("on Start Locating Called ");
		}
		
		@Override
		public void onLocationFound(GpsModel gpsModel) {
			// TODO Auto-generated method stub
			textView.setText("Curr Lat:"+gpsModel.getCurrentLocation().getLatitude()+"\n"
					        +"Lon:"+gpsModel.getCurrentLocation().getLongitude()+"\n"
					        +"Acc:"+gpsModel.getCurrentLocation().getAccuracy()+"\n"
					        +"Distance to Base:"+ GLocationManager.getDistanceBetweenTwoLocations(baseLocation, gpsModel.getCurrentLocation()));
		}
		
		@Override
		public void onError(String errMessage) {
			// TODO Auto-generated method stub
			textView.setText("Errrr:- "+ errMessage);
			
		}
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		utils.removeLocationListener();
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
 