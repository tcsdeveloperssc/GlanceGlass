package com.glance.utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.glance.R;
import com.glance.activity.HotSpotActivity;
import com.glance.controller.core.CallBackListener;
import com.glance.view.HotSpotCircleView;

public class ImageLoad extends AsyncTask<String, Void, Bitmap> {

	private String url;

	private final WeakReference<ImageView> imageViewReference;
	private View layoutView;
	private ImageView hot_img;
	private ProgressBar pBar;
	private RelativeLayout yourRelativeLayout;
	private HotSpotCircleView hotspotCircle;
	private Handler handler;
	private Runnable runnable;
	private Bitmap bitmapImage = null;
	private String artifactId;
	private String imageName;
	private CallBackListener listener;


	public ImageLoad(final View layoutView, CallBackListener listener) {
		this.layoutView = layoutView;
		hot_img = (ImageView) layoutView.findViewById(R.id.hot_img);
		imageViewReference = new WeakReference<ImageView>(hot_img);
		pBar = (ProgressBar) layoutView.findViewById(R.id.pbHotSpot);
		yourRelativeLayout = (RelativeLayout) layoutView
				.findViewById(R.id.hotspots_image);
		this.listener = listener;


		for(int i=0; i<((ViewGroup)yourRelativeLayout).getChildCount(); ++i) {
			View nextChild = ((ViewGroup)yourRelativeLayout).getChildAt(i);
			if (nextChild instanceof HotSpotCircleView){
				hotspotCircle = (HotSpotCircleView) nextChild;
			}
		}

		handler = new Handler();

		runnable = new Runnable() {
			@Override
			public void run() {
				Animation fadeInAnimation = AnimationUtils.loadAnimation(layoutView.getContext(),R.anim.fade_in_anim);
				if (null != hotspotCircle)
					hotspotCircle.startAnimation(fadeInAnimation );
			}
		};

	}

	public ImageLoad(final View layoutView, Bitmap bitmap, int mode) {
		this.layoutView = layoutView;

		if (LoadImageFromDatabaseTask.IMAGE_SCREEN == mode) {
			hot_img = (ImageView) layoutView
					.findViewById(R.id.image_hotspot_view);
			pBar = (ProgressBar) layoutView.findViewById(R.id.pbImageHotSpot);
			if (pBar != null) {
				pBar.setVisibility(View.VISIBLE);
			}
			imageViewReference = new WeakReference<ImageView>(hot_img);
		} else {
			hot_img = (ImageView) layoutView.findViewById(R.id.hot_img);
			imageViewReference = new WeakReference<ImageView>(hot_img);
			pBar = (ProgressBar) layoutView.findViewById(R.id.pbHotSpot);
			yourRelativeLayout = (RelativeLayout) layoutView
					.findViewById(R.id.hotspots_image);

			for (int i = 0; i < ((ViewGroup) yourRelativeLayout)
					.getChildCount(); ++i) {
				View nextChild = ((ViewGroup) yourRelativeLayout).getChildAt(i);
				if (nextChild instanceof HotSpotCircleView) {
					hotspotCircle = (HotSpotCircleView) nextChild;
				}
			}

			handler = new Handler();

			runnable = new Runnable() {
				@Override
				public void run() {
					Animation fadeInAnimation = AnimationUtils.loadAnimation(
							layoutView.getContext(), R.anim.fade_in_anim);
					if (null != hotspotCircle)
						hotspotCircle.startAnimation(fadeInAnimation);
				}
			};
		}

		bitmapImage = bitmap;
	}
	public ImageLoad(View layoutView,String type) {
		this.layoutView = layoutView;
		hot_img = (ImageView) layoutView.findViewById(R.id.image_hotspot_view);
		pBar = (ProgressBar) layoutView.findViewById(R.id.pbImageHotSpot);
		if (pBar != null) {
			pBar.setVisibility(View.VISIBLE);
		}
		imageViewReference = new WeakReference<ImageView>(hot_img);
	}



	protected Bitmap doInBackground(String... params) {
		url = params[0];
//		GLog.d("IMAGE URL", url);
		// url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSGf6YWUbbOPaBLybjJRyh1G-6CNH0l-FaJPP4YVGtOgavEuLqG";
		try {
			if (url == null){
				return bitmapImage;
				
			}
			else{
				
				imageName = params[1];
				Bitmap image = BitmapFactory.decodeStream(new URL(url).openConnection()
						.getInputStream());
				
				artifactId = Utils.databaseHelper.getArtifactId(params[1]);
				Utils.databaseHelper.insertImage(image, artifactId,imageName);
				return image;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(Bitmap result) {
		
		dismissDialog();
		
		if (handler != null){
			handler.post(runnable);
			HotSpotActivity.isImageLoaded = true;
			yourRelativeLayout.setVisibility(View.VISIBLE);
		}
		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(result);
				if (listener != null)
				listener.onSuccess(null);
			}
		}
	}

	protected void onPreExecute() {
		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setBackgroundColor(Color.BLACK);
			}
		}
	}
	public void dismissDialog() {

		Log.d("TCS", "******** DISMISS DIALOG ******** ");
		if (pBar != null) {
			pBar.setVisibility(View.GONE);
		}
	}
}
