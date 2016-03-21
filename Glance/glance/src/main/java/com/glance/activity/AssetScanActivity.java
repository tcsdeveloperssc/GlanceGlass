package com.glance.activity;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.google.zxing.client.android.CaptureActivity;

public class AssetScanActivity extends CaptureActivity {

	private static int SCAN_RESULT = 0;
	public static String ASSET_ID = "assetId";
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0) {                    
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				
				contents = contents.replace("ASSET","");
				Intent intent = new Intent(this, AssetDetailActivity.class);
				intent.putExtra(ASSET_ID, contents);
				startActivity(intent);
				this.finish();

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.d("CapturePictureActivity", "RESULT_CANCELED");
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if (keyCode == 4)
	    {
    		finish();
			return true;
	    }
	    
		return false;
	}
	
}
