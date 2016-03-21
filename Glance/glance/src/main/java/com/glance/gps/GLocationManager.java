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

package com.glance.gps;

import android.R.integer;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.glance.bean.model.GpsModel;
import com.glance.gps.GLocationClient.GpsListener;
import com.glance.utils.Utils.GLog;


/**
 * 14-Nov-2013 528937vnkm Class Description
 **/

public class GLocationManager {
	
	private Context mContext;
	private GpsListener mGpsListener;
	private LocationManager mLocationManager;
	private Handler mLocationHandler;
	private GLocationListener locationListener;
	private boolean isGpsEnabled, isNetworkProviderEnabled;
	public static Location CurrentLocation;
	private boolean isLocationFound;
	private GpsModel gpsModel = new GpsModel();

	public GLocationManager(Context context, GpsListener listener) {
		mContext = context;
		mGpsListener = listener;
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new GLocationListener();
	}

	public void getCurrentLocation() {
		mGpsListener.onStartLocating();
		mLocationHandler = new Handler(Looper.getMainLooper());
		mLocationHandler.post(new LocationManagerRunnable());
	}
	
	/**
	 * F O R   F U T U R E   U S E  
	 ***/
	@SuppressWarnings("unused")
	private Location getLastKnownLocationByProvider(String provider) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			try {
				location = mLocationManager.getLastKnownLocation(provider);
			} catch (IllegalArgumentException e) {
				Log.d("TCS", "Cannot acces Provider " + provider);
			}
		}
		return location;
	}

	/**
	 * Function to calculate distance between two {@link Location}s
	 * 
	 * @param pointA
	 * @param pointB
	 * @returns {@link integer}
	 * @return whole numbered distance between pointA and pointB
	 * **/
	public static int getDistanceBetweenTwoLocations(Location pointA,
			Location pointB) {
		return Math.round(pointA.distanceTo(pointB));
	}
	
	
	public static double getDistanceBetweenTwoLatLon(double lat1, double lon1, double lat2, double lon2) {
	      double theta = lon1 - lon2;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	       return (dist);
	    }

	public static double deg2rad(double deg) {
	      return (deg * Math.PI / 180.0);
	    }
	public static double rad2deg(double rad) {
	      return (rad * 180.0 / Math.PI);
	    }
	/**
	 * Runnable to find the recent location updates
	 * **/
	class LocationManagerRunnable implements Runnable {

		@Override
		public void run() {
			try {
				if(null == locationListener){
					locationListener = new GLocationListener();
				}
				if(null == mLocationManager){
					mLocationManager = (LocationManager) mContext
							.getSystemService(Context.LOCATION_SERVICE);
				}
				
				Criteria criteria = new Criteria();
				criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
				
				 String provider = mLocationManager.getBestProvider(criteria, false);
				 Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				 
				 if(location == null){
					 location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
				 }

				    // Initialize the location fields
				    if (location != null) {
				      GLog.d("TCS","Provider " + provider + " has been selected.");
				      GpsModel gModel = new GpsModel();
				      gModel.setCurrentLocation(location);
				      mGpsListener.onLocationFound(gModel);
				      removeLocationListener();
				      return;
				    } 
				
				if(null == mLocationManager && mContext != null){
					mLocationManager = (LocationManager) mContext
							.getSystemService(Context.LOCATION_SERVICE);
				}
				
				if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					isGpsEnabled = false;
				}else{
					isGpsEnabled = true;
				}
				
				if(!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					isNetworkProviderEnabled = false;
				}else{
					isNetworkProviderEnabled = true;
				}
				
				if(!isGpsEnabled && !isNetworkProviderEnabled){
					mGpsListener.onError("Neither GPS nor Network is available to locate your position");
				}else
				{
					if (locationListener != null && mLocationManager != null) {
						if(isNetworkProviderEnabled){
							mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 300, locationListener);
						}
						if(isGpsEnabled){
							mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 300,
									locationListener);
							mLocationManager.addGpsStatusListener(locationListener);
						}
						
						mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 300,
								locationListener);
					}
				}
				// handle no gps value found case
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if(!isLocationFound){
							Location defaultLoc = new Location("glancerollback");
							defaultLoc.setLatitude(10.01012);
							defaultLoc.setLongitude(76.36554);
							gpsModel.setCurrentLocation(defaultLoc);
							mGpsListener.onLocationFound(gpsModel);
							removeLocationListener();
						}
						
					}
				}, 5000);
			} catch (Exception e) {
				e.printStackTrace();
				// Ignore for now
			} finally {
				// Thinking what to do
			}
		}

	}

	/**
	 * Function for remove location listener class from android location manager
	 */
	public void removeLocationListener() {
		if (mLocationManager != null && locationListener != null) {
			mLocationManager.removeUpdates(locationListener);
			mLocationManager.removeGpsStatusListener(locationListener);
			locationListener = null;
			mLocationManager = null;
		}
	}

	/**
	 * Location listener class which is register with location manager class it
	 * get call back for each GPS location update.
	 * 
	 */
	class GLocationListener implements LocationListener, GpsStatus.Listener {

		private final String TAG = "GLocationListener";
		

		@Override
		public void onGpsStatusChanged(int event) {
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				isLocationFound = true;
				Log.e("TCS", "********** LOCATION FOUND ************* "+ location.getProvider());
				gpsModel.setCurrentLocation(location);
				mGpsListener.onLocationFound(gpsModel);
				removeLocationListener();
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Log.d(TAG, "Provider Disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			if(LocationManager.GPS_PROVIDER == provider){
				isGpsEnabled = true;
			}
			else if(LocationManager.NETWORK_PROVIDER == provider){
				isNetworkProviderEnabled = true;
			}
			Log.d(TAG, "Provider Enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "Provider onStatusChanged");
		}

	}

}
