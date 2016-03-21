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

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.glance.bean.model.GpsModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * 13-Nov-2013 528937vnkm Class Description
 **/

public class GLocationClient {

	private static final String TAG = "GPSUtils";
	private Context mContext;
	private LocationClient mLocationClient;
	private GpsClientCallBack gpsClientCallBack;
	private GpsModel gpsModel = new GpsModel();
	// Global variable to hold the current location
	private Location mCurrentLocation;
	private GpsListener mGpsListener;
	private Handler LocationHandler;
	private LocationRequest mLocationRequest;

	/**
	 * Constructor method
	 * 
	 * @param context
	 * @param gpsListener
	 * **/
	public GLocationClient(Context context, GpsListener gpsListener) {
		this.mContext = context;
		this.mGpsListener = gpsListener;
		gpsClientCallBack = new GpsClientCallBack();
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(mContext, gpsClientCallBack,
				gpsClientCallBack);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(10000);
	}

	public void getCurrentLocation() {
		mGpsListener.onStartLocating();
		LocationHandler = new Handler(Looper.getMainLooper());
		LocationHandler.post(new LocationClientRunnable());
	}

	/**
	 * Function to check if GooglePlayServices are available which returns
	 * {@link boolean} status
	 * **/
	public boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(mContext);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Geofence Detection", "Google Play services is available.");
			// Continue
			return true;
		}
		// Google Play services was not available for some reason
		return false;
	}

	class GpsClientCallBack implements
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener {

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			Log.e(TAG, "******* onConnection Failed *********");
			mGpsListener.onError("Unable to determine the position");
		}

		@Override
		public void onConnected(Bundle arg0) {
			Log.e(TAG, "******* onConnected *********");
			try {
				mCurrentLocation = mLocationClient.getLastLocation();
				if (null == mCurrentLocation) {
					mLocationClient.requestLocationUpdates(mLocationRequest,
							new LocationListener() {

								@Override
								public void onLocationChanged(Location arg0) {
									mCurrentLocation = arg0;
									processLocation();
								}
							});
				}else{
					processLocation();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				mGpsListener.onError("Unable to determine the position");
			} finally {
				
			}
		}
		
		private void processLocation(){
			Address addr = getGeoCoderAddress(mCurrentLocation);
			gpsModel.setCurrentAddress(addr);
			gpsModel.setCurrentLocation(mCurrentLocation);
			mGpsListener.onLocationFound(gpsModel);
		}
		
		@Override
		public void onDisconnected() {
			Log.e(TAG, "******* onDisConnected *********");
		}

	}
	
	public void disconnectLocationClient(){
		if(null != mLocationClient){
			mLocationClient.disconnect();
		}
	}
	
	/**
	 * Runnable to connect to LocationClient
	 * **/
	class LocationClientRunnable implements Runnable {

		@Override
		public void run() {
			mLocationClient.connect();
		}

	}
	
	/**
	 * F O R     F U T U R E    U S E
	 * 
	 * **/
	private Address getGeoCoderAddress(Location loc) {
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		List<Address> addresses = null;
		try {
			/*
			 * Return 1 address.
			 */
			addresses = geocoder.getFromLocation(
					mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude(), 1);
			if (addresses != null && !addresses.isEmpty()) {
				return addresses.get(0);
			}
		} catch (Exception e1) {
			Log.e(TAG, "Unable to find Address");
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * Function to calculate distance 
	 * between two {@link Location}s
	 * @param pointA 
	 * @param pointB
	 * @returns float
	 * @return float distance between pointA and pointB
	 * **/
	public float getDistanceBetweenTwoLocations(Location pointA, Location pointB){
		return pointA.distanceTo(pointB);
	}
	
	/**
	 * Interface to update UI
	 * **/
	public interface GpsListener {
		public void onStartLocating();

		public void onLocationFound(GpsModel gpsModel);

		public void onError(String errMessage);
	}

}
