package com.glance.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.glance.view.ImageScreen;

public class LoadImageFromDatabaseTask extends
		AsyncTask<String, String, ImageHelper> {
	private Context context;
	private View layoutView;
	public static int IMAGE_BACKGROUND = 0;
	public static int IMAGE_SCREEN = 1;
	private int mode = IMAGE_BACKGROUND;
	private String tableName = null;

	public LoadImageFromDatabaseTask(Context context, View layoutView, int mode) {
		this.context = context;
		this.layoutView = layoutView;
		this.mode = mode;
	}

	@Override
	protected ImageHelper doInBackground(String... args) {
		tableName = args[1];
		Log.d("LoadImageFromDatabaseTask", "doInBackground" + args[0]);
		String url = null;
		if (Utils.databaseHelper == null)
			Utils.createDatabase(context);
		if (tableName.equalsIgnoreCase("ImageTable")) {
			url = Utils.databaseHelper.getImageUrl(args[0]);
			if (url != null)
				return Utils.databaseHelper.getImage(url);

		}
		return null;

	}

	protected void onProgressUpdate(Integer... progress) {
	}

	protected void onPostExecute(ImageHelper imageHelper) {
		if (imageHelper == null) {
			Toast.makeText(context, "Hotspot was not viewed in online mode",
					Toast.LENGTH_SHORT).show();
			if (context instanceof ImageScreen)
				((ImageScreen) context).finish();
		} else {
			byte[] bytes = imageHelper.getImageByteArray();
			Bitmap bitmap = null;
			if (bytes != null) {
				try {
					bitmap = BitmapFactory.decodeByteArray(bytes, 0,
							bytes.length);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			ImageLoad downloader = new ImageLoad(layoutView, bitmap, mode);
			downloader.execute((String) null, (String) null);
		}

	}
}